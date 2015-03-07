/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.mbroker;

import org.carewebframework.ui.zk.ZKUtil;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.BrokerSession.IAsyncRPCEvent;

import org.zkoss.zk.ui.Component;

/**
 * Handler to ensure that IAsyncRPC events are delivered within the main event thread. Only one
 * active asynchronous request per handler instance is allowed.
 */
public class AsyncRPCEventDispatcher implements IAsyncRPCEvent {
    
    private int asyncHandle;
    
    private String rpcName;
    
    private final Component target;
    
    private final BrokerSession broker;
    
    /**
     * Create handler
     * 
     * @param broker Broker session
     * @param target Component that will be the target of the async events.
     */
    public AsyncRPCEventDispatcher(BrokerSession broker, Component target) {
        this.broker = broker;
        this.target = target;
    }
    
    /**
     * Make an asynchronous call.
     * 
     * @param rpcName The RPC name.
     * @param args The RPC arguments.
     */
    public void callRPCAsync(String rpcName, Object... args) {
        abort();
        this.rpcName = rpcName;
        asyncHandle = broker.callRPCAsync(rpcName, this, args);
    }
    
    /**
     * Abort any asynchronous RPC in progress.
     */
    public void abort() {
        if (asyncHandle != 0) {
            int handle = asyncHandle;
            asyncHandle = 0;
            broker.callRPCAbort(handle);
            ZKUtil.fireEvent(new AsyncRPCAbortEvent(rpcName, target));
            rpcName = null;
        }
    }
    
    /**
     * RPC completion callback.
     */
    @Override
    public void onRPCComplete(int handle, String data) {
        if (handle == asyncHandle) {
            asyncHandle = 0;
            ZKUtil.fireEvent(new AsyncRPCCompleteEvent(rpcName, target, data));
            rpcName = null;
        }
    }
    
    /**
     * RPC error callback.
     */
    @Override
    public void onRPCError(int handle, int code, String text) {
        if (handle == asyncHandle) {
            asyncHandle = 0;
            ZKUtil.fireEvent(new AsyncRPCErrorEvent(rpcName, target, code, text));
            rpcName = null;
        }
    }
    
}
