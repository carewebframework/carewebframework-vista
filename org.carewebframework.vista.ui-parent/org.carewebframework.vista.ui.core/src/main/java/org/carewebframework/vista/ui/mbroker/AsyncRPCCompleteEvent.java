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

/**
 * Event generated when an asynchronous RPC has completed.
 */
public class AsyncRPCCompleteEvent extends AsyncRPCBaseEvent {
    
    private static final long serialVersionUID = 1L;
    
    public static final String ON_ASYNC_RPC_COMPLETE = "onAsyncRPCComplete";
    
    public AsyncRPCCompleteEvent(String rpcName, Component target, String data, int handle) {
        super(ON_ASYNC_RPC_COMPLETE, target, data, rpcName, handle);
    }
}
