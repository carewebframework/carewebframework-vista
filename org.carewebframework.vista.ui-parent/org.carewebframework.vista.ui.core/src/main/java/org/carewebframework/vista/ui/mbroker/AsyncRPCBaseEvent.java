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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Abstract base class for asynchronous RPC events.
 */
public abstract class AsyncRPCBaseEvent extends Event {
    
    private static final long serialVersionUID = 1L;
    
    public static final String ON_ASYNC_RPC_ABORT = "onAsyncRPCAbort";
    
    private final String rpcName;
    
    private final int asyncHandle;
    
    protected AsyncRPCBaseEvent(String eventName, Component target, String eventData, String rpcName, int handle) {
        super(eventName, target, eventData);
        this.rpcName = rpcName;
        this.asyncHandle = handle;
    }
    
    /**
     * Returns the name of the RPC that caused the event.
     * 
     * @return RPC name.
     */
    public String getRPCName() {
        return rpcName;
    }
    
    /**
     * Returns the async handle associated with the async request.
     * 
     * @return The async handle.
     */
    public int getHandle() {
        return asyncHandle;
    }
    
    @Override
    public String getData() {
        return (String) super.getData();
    }
}
