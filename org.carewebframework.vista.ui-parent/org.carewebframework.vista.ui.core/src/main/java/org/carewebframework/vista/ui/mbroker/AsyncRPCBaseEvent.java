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
