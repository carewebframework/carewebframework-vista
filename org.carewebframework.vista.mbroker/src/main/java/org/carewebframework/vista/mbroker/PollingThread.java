/*
 * #%L
 * carewebframework
 * %%
 * Copyright (C) 2008 - 2017 Regenstrief Institute, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.carewebframework.org/licensing/disclaimer.
 *
 * #L%
 */
package org.carewebframework.vista.mbroker;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.mbroker.Request.Action;

/**
 * Background thread for polling host.
 */
public class PollingThread extends Thread {
    
    private static final Log log = LogFactory.getLog(PollingThread.class);
    
    /**
     * Callback interface for handling background polling events.
     */
    public interface IHostEventHandler {
        
        /**
         * Called when the server signals an event to the client.
         *
         * @param name Name of the event.
         * @param data Data associated with the event.
         */
        void onHostEvent(String name, Object data);
        
    }
    
    private boolean enabled;
    
    private boolean terminated;
    
    private int pollingInterval = 3000;
    
    private final WeakReference<BrokerSession> sessionRef;
    
    private final Object monitor = new Object();
    
    private Request query;
    
    private Request ping;
    
    /**
     * Creates and starts a polling thread for the specified session. If an executor service has
     * been configured, thread execution will be delegated to that service. Otherwise, it will be
     * started directly.
     *
     * @param session Broker session associated with the thread.
     */
    public PollingThread(BrokerSession session) {
        super();
        setName("MBrokerPollingDaemon-" + getId());
        sessionRef = new WeakReference<BrokerSession>(session);
        ExecutorService executor = session.getExecutorService();
        
        if (executor != null) {
            executor.execute(this);
        } else {
            start();
        }
    }
    
    /**
     * Requests the background thread to terminate.
     */
    public void terminate() {
        enabled = false;
        terminated = true;
        wakeup();
    }
    
    /**
     * Returns the enabled state of the background thread.
     *
     * @return True if the background thread is actively polling. False if background polling has
     *         been suspended.
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Sets the enabled state of the background thread.
     *
     * @param enabled True if the background thread is actively polling. False if background polling
     *            has been suspended.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Creates a request packet for the specified action.
     *
     * @param action Action to perform.
     * @param session Session receiving the request.
     * @return The fully formed request.
     */
    private Request getRequest(Action action, BrokerSession session) {
        switch (action) {
            case QUERY:
                if (query == null) {
                    query = new Request(action);
                    query.addParameter("UID", session.getId());
                }
                
                return query;
            
            case PING:
                if (ping == null) {
                    ping = new Request(action);
                }
                
                return ping;
            
            default:
                return null;
        }
    }
    
    /**
     * Polls the host at the specified interval for asynchronous activity.
     *
     * @param session Session whose host is to be polled.
     */
    private void pollHost(BrokerSession session) {
        Request request = !enabled ? null : getRequest(session.pollingAction(), session);
        
        if (request == null) {
            return;
        }
        
        try {
            Response response = session.netCall(request, 1000);
            String results[] = response.getData().split(Constants.LINE_SEPARATOR, 2);
            String params[] = StrUtil.split(results[0], StrUtil.U, 2);
            
            switch (response.getResponseType()) {
                case ACK: // A simple server acknowledgement
                    int i = StrUtil.toInt(params[0]);
                    
                    if (i > 0) {
                        pollingInterval = i * 1000;
                    }
                    
                    FMDate hostTime = new FMDate(params[1]);
                    session.setHostTime(hostTime);
                    break;
                
                case ASYNC: // Completed asynchronous RPC
                    int asyncHandle = StrUtil.toInt(params[0]);
                    int asyncError = StrUtil.toInt(params[1]);
                    
                    if (asyncHandle > 0) {
                        if (asyncError != 0) {
                            session.onRPCError(asyncHandle, asyncError, results[1]);
                        } else {
                            session.onRPCComplete(asyncHandle, results[1]);
                        }
                    }
                    break;
                
                case EVENT: // Global event for delivery
                    List<IHostEventHandler> hostEventHandlers = session.getHostEventHandlers();
                    
                    if (hostEventHandlers != null) {
                        try {
                            String eventName = results[0];
                            Object eventData = SerializationMethod.deserialize(results[1]);
                            
                            for (IHostEventHandler hostEventHandler : hostEventHandlers) {
                                try {
                                    hostEventHandler.onHostEvent(eventName, eventData);
                                } catch (Throwable e) {
                                    log.error("Host event subscriber threw an exception", e);
                                }
                            }
                        } catch (Throwable e) {
                            log.error("Error processing host event", e);
                        }
                    }
                    break;
            }
        } catch (Throwable e) {
            log.error("Error processing polling response.", e);
            terminate();
        }
    }
    
    /**
     * Wakes up the background thread.
     *
     * @return True if request was successful.
     */
    private synchronized boolean wakeup() {
        try {
            synchronized (monitor) {
                monitor.notify();
            }
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
    
    /**
     * Main polling loop.
     */
    @Override
    public void run() {
        synchronized (monitor) {
            while (!terminated) {
                try {
                    BrokerSession session = this.sessionRef.get();
                    
                    if (session == null) {
                        break;
                    } else {
                        pollHost(session);
                    }
                    session = null;
                    monitor.wait(pollingInterval);
                } catch (InterruptedException e) {}
            }
        }
        
        log.debug(getName() + " has exited.");
    }
}
