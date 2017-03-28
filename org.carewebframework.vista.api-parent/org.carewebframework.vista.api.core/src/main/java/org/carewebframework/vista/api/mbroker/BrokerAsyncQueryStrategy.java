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
package org.carewebframework.vista.api.mbroker;

import org.carewebframework.api.query.IAsyncQueryStrategy;
import org.carewebframework.api.query.IQueryCallback;
import org.carewebframework.api.query.IQueryContext;
import org.carewebframework.api.query.IQueryService;
import org.carewebframework.api.query.QueryUtil;
import org.carewebframework.api.thread.IAbortable;
import org.carewebframework.vista.mbroker.BrokerSession.IAsyncRPCEvent;
import org.carewebframework.vista.mbroker.RPCException;

public class BrokerAsyncQueryStrategy<T> implements IAsyncQueryStrategy<T> {
    
    public static class AsyncRequest<T> implements IAbortable, IAsyncRPCEvent {
        
        private final AbstractBrokerQueryService<T> service;
        
        private int asyncHandle;
        
        private final IQueryContext context;
        
        private final IQueryCallback<T> callback;
        
        public AsyncRequest(AbstractBrokerQueryService<T> service, IQueryContext context, IQueryCallback<T> callback) {
            this.service = service;
            this.context = context;
            this.callback = callback;
            asyncHandle = service.getBroker().callRPCAsync(service.getRPCName(), this, service.getArguments(context));
        }
        
        @Override
        public void abort() {
            if (asyncHandle != 0) {
                service.getBroker().callRPCAbort(asyncHandle);
                asyncHandle = 0;
                callback.onQueryFinish(this, QueryUtil.<T> abortResult(null));
            }
        }
        
        @Override
        public void onRPCComplete(int handle, String data) {
            if (asyncHandle == handle) {
                asyncHandle = 0;
                callback.onQueryFinish(this, QueryUtil.<T> packageResult(service.processData(context, data)));
            }
        }
        
        @Override
        public void onRPCError(int handle, int code, String text) {
            if (asyncHandle == handle) {
                asyncHandle = 0;
                Exception e = new RPCException(code, text);
                callback.onQueryFinish(this, QueryUtil.<T> errorResult(e));
            }
        }
    }
    
    public BrokerAsyncQueryStrategy() {
    }
    
    @Override
    public IAbortable fetch(IQueryService<T> service, IQueryContext context, IQueryCallback<T> callback) {
        return new AsyncRequest<T>((AbstractBrokerQueryService<T>) service, context, callback);
    }
    
}
