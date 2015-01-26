/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.mbroker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringUtils;

import org.carewebframework.common.JSONUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.mbroker.PollingThread.IHostEventHandler;
import org.carewebframework.vista.mbroker.Request.Action;
import org.carewebframework.vista.mbroker.Response.ResponseType;
import org.carewebframework.vista.mbroker.Security.AuthResult;

public class BrokerSession {
    
    /**
     * Callback interface for asynchronous RPC's.
     */
    public interface IAsyncRPCEvent {
        
        /**
         * Called when an asynchronous remote procedure request has completed.
         *
         * @param handle Unique handle of the request.
         * @param data Data returned by the request.
         */
        void onRPCComplete(int handle, String data);
        
        /**
         * Called when an asynchronous remote procedure terminated with an error.
         *
         * @param handle Unique handle of the request.
         * @param code Error code returned by the server.
         * @param text Error text returned by the server.
         */
        void onRPCError(int handle, int code, String text);
        
    }
    
    /**
     * Authentication method to be employed.
     */
    public static enum AuthMethod {
        Normal, Cache, NT
    }
    
    /**
     * Server capabilities descriptor.
     */
    public static class ServerCaps implements Cloneable {
        
        private AuthMethod authMethod = AuthMethod.Normal;
        
        private Version serverVersion;
        
        private boolean caseSensitivePassword;
        
        private boolean contextCached;
        
        private boolean concurrentMode;
        
        private String domainName;
        
        private String siteName;
        
        private String cipherKey;
        
        public ServerCaps() {
        }
        
        public void clear() {
            authMethod = AuthMethod.Normal;
            serverVersion = null;
            caseSensitivePassword = false;
            contextCached = false;
            concurrentMode = false;
            domainName = null;
            siteName = null;
            cipherKey = null;
        }
        
        private void init(String init) {
            String[] pcs = StrUtil.split(init, StrUtil.U, 6, true);
            concurrentMode = StrUtil.toBoolean(pcs[0]);
            authMethod = AuthMethod.values()[StrUtil.toInt(pcs[1])];
            serverVersion = new Version(pcs[2]);
            caseSensitivePassword = StrUtil.toBoolean(pcs[3]);
            contextCached = StrUtil.toBoolean(pcs[4]);
            cipherKey = pcs[5];
        }
        
        public AuthMethod getAuthMethod() {
            return authMethod;
        }
        
        public Version getServerVersion() {
            return serverVersion;
        }
        
        public boolean isCaseSensitivePassword() {
            return caseSensitivePassword;
        }
        
        public boolean isContextCached() {
            return contextCached;
        }
        
        public boolean isConcurrentMode() {
            return concurrentMode;
        }
        
        public String getDomainName() {
            return domainName;
        }
        
        public String getSiteName() {
            return siteName;
        }
        
        public String getCipherKey() {
            return cipherKey;
        }
        
    }
    
    private ConnectionParams connectionParams;
    
    private final ServerCaps serverCaps = new ServerCaps();
    
    private ExecutorService executorService;
    
    private Date hostTime;
    
    private int id;
    
    private int userId;
    
    private byte netSequence;
    
    private volatile Socket socket;
    
    private final List<IHostEventHandler> hostEventHandlers = new ArrayList<IHostEventHandler>();
    
    private PollingThread pollingThread;
    
    private final List<String> preLoginMessage = new ArrayList<String>();
    
    private final List<String> postLoginMessage = new ArrayList<String>();
    
    private final Map<Integer, IAsyncRPCEvent> callbacks = new HashMap<Integer, IAsyncRPCEvent>();
    
    public BrokerSession() {
        
    }
    
    public BrokerSession(ConnectionParams params) {
        setConnectionParams(params);
    }
    
    public AuthResult connect(boolean authenticate) {
        ServerSocket listener = null;
        AuthResult authResult = null;
        
        try {
            close();
            socket = new Socket(connectionParams.getServer(), connectionParams.getPort());
            listener = new ServerSocket(0, 0, socket.getLocalAddress());
            
            if (connectionParams.isDebug()) {
                System.out.println("Start M process:  D DEBUG^CIANBLIS");
                System.out.println("Addr = " + listener.getInetAddress().getHostAddress());
                System.out.println("Port = " + listener.getLocalPort());
            }
            
            Request request = new Request(Action.CONNECT);
            request.addParameter("IP", socket.getLocalAddress());
            request.addParameter("LP", socket.getLocalPort());
            request.addParameter("UCI", connectionParams.getNamespace());
            request.addParameter("DBG", connectionParams.isDebug());
            request.addParameter("VER", Constants.BROKER_VERSION);
            serverCaps.init(netCall(request, connectionParams.getTimeout()).getData());
            
            if (!serverCaps.concurrentMode) {
                close();
                listener.setSoTimeout(60000);
                socket = listener.accept();
            }
            
            authResult = authenticate ? Security.authenticate(this) : null;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(listener);
        }
        
        polling(true);
        return authResult;
    }
    
    public void disconnect() {
        callbacks.clear();
        polling(false);
        preLoginMessage.clear();
        postLoginMessage.clear();
        
        if (socket != null) {
            Request request = new Request(Action.DISCONNECT);
            request.addParameter("UID", id);
            
            try {
                netCall(request, 5000);
            } catch (Exception e) {}
            
            close();
        }
    }
    
    private void close() {
        polling(false);
        
        if (socket != null) {
            close(socket);
            socket = null;
            reset();
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
    
    private void polling(boolean start) {
        if (start) {
            if (pollingThread == null) {
                pollingThread = new PollingThread(this);
            }
            pollingThread.setEnabled(true);
        } else {
            if (pollingThread != null) {
                pollingThread.terminate();
                pollingThread = null;
            }
        }
    }
    
    private void reset() {
        serverCaps.clear();
        id = 0;
        userId = 0;
        netSequence = 0;
    }
    
    private void close(Socket socket) {
        try {
            socket.close();
        } catch (Exception e) {}
    }
    
    private void close(ServerSocket socket) {
        try {
            socket.close();
        } catch (Exception e) {}
    }
    
    public void ensureConnection() {
        if (!isConnected()) {
            connect(false);
        }
    }
    
    public boolean callRPCAbort(int handle) {
        if (getCallback(handle) != null) {
            return callRPCBool("CIANBASY STOP", handle);
        } else {
            return false;
        }
    }
    
    public int callRPCAsync(String name, IAsyncRPCEvent callback, Object... args) {
        int handle = StrUtil.toInt(callRPC(name, true, connectionParams.getTimeout(), args));
        callbacks.put(handle, callback);
        return handle;
    }
    
    public int callRPCInt(String name, Object... args) {
        return StrUtil.toInt(callRPC(name, args));
    }
    
    public boolean callRPCBool(String name, Object... args) {
        return StrUtil.toBoolean(callRPC(name, args));
    }
    
    public double callRPCFloat(String name, Object... args) {
        return StrUtil.toDouble(callRPC(name, args));
    }
    
    public long callRPCLong(String name, Object... args) {
        return StrUtil.toLong(callRPC(name, args));
    }
    
    public List<String> callRPCList(String name, List<String> list, Object... args) {
        List<String> result = list == null ? new ArrayList<String>() : list;
        StrUtil.toList(callRPC(name, args), result, Constants.LINE_SEPARATOR);
        return result;
    }
    
    public String callRPC(String name, Object... args) {
        return callRPC(name, false, connectionParams.getTimeout(), args);
    }
    
    public String callRPC(String name, boolean async, int timeout) {
        return callRPC(name, async, timeout, (RPCParameters) null);
    }
    
    public String callRPC(String name, boolean async, int timeout, Object... params) {
        return callRPC(name, async, timeout, packageParams(params));
    }
    
    /**
     * Performs a remote procedure call.
     *
     * @param name Name of the remote procedure. This has the format:
     *            <p>
     *
     *            <pre>
     * &lt;remote procedure name&gt;[:&lt;remote procedure version&gt;][:&lt;calling context&gt;]
     * </pre>
     *            <p>
     *            where only the remote procedure name is required. If the server supports multiple
     *            versions of a remote procedure, an explicit version specifier may be added. If a
     *            different calling context is desired, this may be specified to override the
     *            default. For example:
     *            <p>
     *
     *            <pre>
     * GET LAB RESULTS:2.4:LR CONTEXT
     * </pre>
     * @param async If true, the remote procedure call will be executed asynchronously. In this
     *            case, the value returned by the method will be the unique handle for the
     *            asynchronous request.
     * @param timeout The timeout, in milliseconds, to wait for remote procedure completion.
     * @param params Parameters to be passed to the remote procedure. This may be null.
     * @return The data returned by the remote procedure called if called synchronously, or the
     *         unique handle of the request, if call asynchronously.
     */
    public String callRPC(String name, boolean async, int timeout, RPCParameters params) {
        ensureConnection();
        String version = "";
        String context = connectionParams.getAppid();
        
        if (name.contains(":")) {
            String pcs[] = StrUtil.split(name, ":", 3, true);
            name = pcs[0];
            version = pcs[1];
            context = pcs[2].isEmpty() ? context : pcs[2];
        }
        
        Request request = new Request(Action.RPC);
        request.addParameter("UID", id);
        request.addParameter("CTX", context);
        request.addParameter("VER", version);
        request.addParameter("RPC", name);
        request.addParameter("ASY", async);
        
        if (params != null) {
            request.addParameters(params);
        }
        
        Response response = netCall(request, timeout);
        
        if (response.getResponseType() == ResponseType.ERROR) {
            throw new RPCException(response.getData());
        }
        
        return response.getData();
    }
    
    /**
     * Package parameters for RPC call. If parameters already packaged, simply return the package.
     *
     * @param params Parameters to be packaged.
     * @return Packaged parameters.
     */
    private RPCParameters packageParams(Object... params) {
        if (params == null) {
            return null;
        }
        
        if (params.length == 1 && params[0] instanceof RPCParameters) {
            return (RPCParameters) params[0];
        }
        
        return new RPCParameters(params);
    }
    
    public boolean eventSubscribe(String eventName, boolean subscribe) {
        if (!isConnected()) {
            return false;
        }
        
        Request request = new Request(subscribe ? Action.SUBSCRIBE : Action.UNSUBSCRIBE);
        request.addParameter("UID", id);
        request.addParameter("EVT", eventName);
        return StrUtil.toBoolean(netCall(request).getData());
    }
    
    public void fireRemoteEvent(String eventName, Serializable eventData, String recipients) {
        fireRemoteEvent(eventName, eventData, StrUtil.split(recipients, ","));
    }
    
    public void fireRemoteEvent(String eventName, Serializable eventData, String[] recipients) {
        RPCParameters params = new RPCParameters();
        params.get(0).setValue(eventName);
        
        RPCParameter param = params.get(1);
        String data = eventData == null ? "" : eventData instanceof String ? eventData.toString() : Constants.JSON_PREFIX
                + JSONUtil.serialize(eventData);
        int index = 0;
        
        for (int i = 0; i < data.length(); i += 255) {
            param.put(Integer.toString(++index), StringUtils.substring(data, i, i + 255));
        }
        
        param = params.get(2);
        
        for (String recip : recipients) {
            if (!recip.isEmpty()) {
                if (processRecipient(param, "#", "UID", recip) || processRecipient(param, "", "DUZ", recip)) {}
            }
        }
        
        callRPC("CIANBEVT BCAST", params);
    }
    
    private boolean processRecipient(RPCParameter param, String prefix, String subscript, String recipient) {
        if (recipient.startsWith(prefix)) {
            param.put(new String[] { subscript, recipient.substring(prefix.length()) }, "");
            return true;
        }
        
        return false;
    }
    
    /**
     * Returns the client's host name. The session must have an active connection.
     *
     * @return Client's host name.
     */
    public String getLocalName() {
        return socket == null ? "" : socket.getLocalAddress().getHostName();
    }
    
    /**
     * Returns the client's ip address. The session must have an active connection.
     *
     * @return Client's ip address.
     */
    public String getLocalAddress() {
        return socket == null ? "" : socket.getLocalAddress().getHostAddress();
    }
    
    /**
     * Returns true if the session is currently connected.
     *
     * @return True if the session is currently connected.
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }
    
    /**
     * Returns true if the session has been authenticated.
     *
     * @return True if the session has been authenticated.
     */
    public boolean isAuthenticated() {
        return userId != 0;
    }
    
    /**
     * Returns the supported polling action. This is based on the current session state. Possible
     * return values are:
     * <ul>
     * <li>null - No polling is possible (session in disconnected state).</li>
     * <li>Ping - Only ping is supported (updated server params only).</li>
     * <li>Query - Full query is supported (async events and RPCs)</li>
     * </ul>
     *
     * @return Supported polling action.
     */
    public Action pollingAction() {
        return !isConnected() ? null : !isAuthenticated() || hostEventHandlers.isEmpty() ? Action.PING : Action.QUERY;
    }
    
    /**
     * Returns server capabilities descriptor.
     *
     * @return Server capabilities.
     */
    public ServerCaps getServerCaps() {
        return serverCaps;
    }
    
    /**
     * Returns current connection parameters.
     *
     * @return Connection parameters.
     */
    public ConnectionParams getConnectionParams() {
        return connectionParams;
    }
    
    /**
     * Sets the connection parameters. Does not affect an existing connection.
     *
     * @param params New connection parameters.
     */
    public void setConnectionParams(ConnectionParams params) {
        this.connectionParams = new ConnectionParams(params);
    }
    
    /**
     * Flushes the socket's input stream, discarding any pending input.
     */
    private void netFlush() {
        try {
            socket.getInputStream().skip(Long.MAX_VALUE);
        } catch (Exception e) {}
    }
    
    /**
     * Initializes the broker session with information returned by the server.
     *
     * @param init Initialization data.
     */
    protected void init(String init) {
        String[] pcs = StrUtil.split(init, StrUtil.U, 4);
        id = StrUtil.toInt(pcs[0]);
        serverCaps.domainName = pcs[1];
        serverCaps.siteName = pcs[2];
        userId = StrUtil.toInt(pcs[3]);
    }
    
    /**
     * Issues a request to the server, returning the response. Uses the default timeout set in the
     * connection parameters.
     *
     * @param request Request to be sent.
     * @return Response returned by the server.
     */
    protected synchronized Response netCall(Request request) {
        return netCall(request, connectionParams.getTimeout());
    }
    
    /**
     * Issues a request to the server, returning the response.
     *
     * @param request Request to be sent.
     * @param timeout The timeout, in milliseconds, to await a response.
     * @return Response returned by the server.
     */
    protected synchronized Response netCall(Request request, int timeout) {
        Response response = null;
        try {
            socket.setSoTimeout(timeout);
            DataOutputStream requestPacket = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            request.write(requestPacket, nextSequenceId());
            requestPacket.flush();
            DataInputStream responsePacket = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            response = new Response(responsePacket);
            
            if (response.getSequenceId() != request.getSequenceId()) {
                throw new IOException("Response is not for current request.");
            }
        } catch (Exception e) {
            netFlush();
            throw new RuntimeException(e);
        }
        
        return response;
    }
    
    /**
     * Returns the next valid sequence #.
     * 
     * @return Sequence #.
     */
    private byte nextSequenceId() {
        while (++netSequence == Constants.EOD) {}
        return netSequence;
    }
    
    /**
     * Returns the current session id.
     *
     * @return The session id.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Returns the authenticated user id (DUZ).
     *
     * @return The user id.
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Adds an event handler for background polling events.
     *
     * @param hostEventHandler An event handler.
     */
    public void addHostEventHandler(IHostEventHandler hostEventHandler) {
        synchronized (hostEventHandlers) {
            if (!hostEventHandlers.contains(hostEventHandler)) {
                hostEventHandlers.add(hostEventHandler);
            }
        }
    }
    
    /**
     * Removes an event handler for background polling events.
     *
     * @param hostEventHandler An event handler.
     */
    public void removeHostEventHandler(IHostEventHandler hostEventHandler) {
        synchronized (hostEventHandlers) {
            hostEventHandlers.remove(hostEventHandler);
        }
    }
    
    /**
     * Returns a list of registered event handlers for background polling events.
     *
     * @return List of registered event handlers.
     */
    protected List<IHostEventHandler> getHostEventHandlers() {
        synchronized (hostEventHandlers) {
            return hostEventHandlers.isEmpty() ? null : new ArrayList<IHostEventHandler>(hostEventHandlers);
        }
    }
    
    /**
     * Returns the pre-login message text.
     *
     * @return Pre-login message text.
     */
    public List<String> getPreLoginMessage() {
        return preLoginMessage;
    }
    
    /**
     * Sets the pre-login message text.
     *
     * @param message Pre-login message text.
     */
    protected void setPreLoginMessage(List<String> message) {
        preLoginMessage.clear();
        preLoginMessage.addAll(message);
    }
    
    /**
     * Returns the post-login message text.
     *
     * @return Post-login message text.
     */
    public List<String> getPostLoginMessage() {
        return postLoginMessage;
    }
    
    /**
     * Sets the post-login message text.
     *
     * @param message Post-login message text.
     */
    protected void setPostLoginMessage(List<String> message) {
        this.postLoginMessage.clear();
        this.postLoginMessage.addAll(message);
    }
    
    /**
     * Invokes the callback for the specified handle when an error is encountered during an
     * asynchronous RPC call.
     *
     * @param asyncHandle The unique handle for the asynchronous RPC call.
     * @param asyncError The error code.
     * @param text The error text.
     */
    protected void onRPCError(int asyncHandle, int asyncError, String text) {
        IAsyncRPCEvent callback = getCallback(asyncHandle);
        
        if (callback != null) {
            callback.onRPCError(asyncHandle, asyncError, text);
        }
    }
    
    /**
     * Invokes the callback for the specified handle upon successful completion of an asynchronous
     * RPC call.
     *
     * @param asyncHandle The unique handle for the asynchronous RPC call.
     * @param data The data returned by the RPC.
     */
    protected void onRPCComplete(int asyncHandle, String data) {
        IAsyncRPCEvent callback = getCallback(asyncHandle);
        
        if (callback != null) {
            callback.onRPCComplete(asyncHandle, data);
        }
    }
    
    /**
     * Returns the asynchronous RPC callback for the specified handle.
     *
     * @param asyncHandle The unique handle for the asynchronous RPC call.
     * @return The callback handler, or null if none found.
     */
    private IAsyncRPCEvent getCallback(int asyncHandle) {
        return callbacks.remove(asyncHandle);
    }
    
    /**
     * Returns the time as reported by the server.
     *
     * @return The server time.
     */
    public Date getHostTime() {
        return hostTime;
    }
    
    /**
     * Sets the time reported by the server.
     *
     * @param hostTime The server time.
     */
    protected void setHostTime(Date hostTime) {
        this.hostTime = hostTime;
    }
    
    /**
     * Returns the executor service used by the session for handling background pollings.
     *
     * @return Executor service.
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }
    
    /**
     * Sets the executor service used by the session for handling background pollings.
     *
     * @param executorService Executor service.
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
    
}
