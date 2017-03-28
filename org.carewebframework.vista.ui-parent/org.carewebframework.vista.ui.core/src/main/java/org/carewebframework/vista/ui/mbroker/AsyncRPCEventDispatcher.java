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
     * @return The async handle.
     */
    public int callRPCAsync(String rpcName, Object... args) {
        abort();
        this.rpcName = rpcName;
        asyncHandle = broker.callRPCAsync(rpcName, this, args);
        return asyncHandle;
    }
    
    /**
     * Abort any asynchronous RPC in progress.
     */
    public void abort() {
        if (asyncHandle != 0) {
            int handle = asyncHandle;
            asyncHandle = 0;
            broker.callRPCAbort(handle);
            ZKUtil.fireEvent(new AsyncRPCAbortEvent(rpcName, target, handle));
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
            ZKUtil.fireEvent(new AsyncRPCCompleteEvent(rpcName, target, data, handle));
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
            ZKUtil.fireEvent(new AsyncRPCErrorEvent(rpcName, target, code, text, handle));
            rpcName = null;
        }
    }
    
}
