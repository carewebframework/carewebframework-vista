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

/**
 * Event generated when an asynchronous RPC has aborted.
 */
public class AsyncRPCErrorEvent extends AsyncRPCBaseEvent {
    
    private static final long serialVersionUID = 1L;
    
    public static final String ON_ASYNC_RPC_ERROR = "onAsyncRPCError";
    
    private final int errorCode;
    
    public AsyncRPCErrorEvent(String rpcName, Component target, int errorCode, String errorText, int handle) {
        super(ON_ASYNC_RPC_ERROR, target, errorText, rpcName, handle);
        this.errorCode = errorCode;
    }
    
    /**
     * Returns the numeric code associated with the error.
     * 
     * @return Error code;
     */
    public int getErrorCode() {
        return errorCode;
    }
}
