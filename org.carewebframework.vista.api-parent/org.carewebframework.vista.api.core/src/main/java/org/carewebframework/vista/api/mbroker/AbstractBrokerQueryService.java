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

import java.util.ArrayList;
import java.util.List;

import org.carewebframework.api.query.AbstractQueryServiceEx;
import org.carewebframework.api.query.IQueryContext;
import org.carewebframework.api.query.IQueryResult;
import org.carewebframework.api.query.QueryUtil;
import org.carewebframework.vista.mbroker.BrokerSession;

/**
 * Base class for wrapping a data service around an RPC call.
 *
 * @param <T> Class of query result.
 */
public abstract class AbstractBrokerQueryService<T> extends AbstractQueryServiceEx<BrokerSession, T> {
    
    private final String rpcName;
    
    public AbstractBrokerQueryService(BrokerSession broker, String rpcName) {
        super(broker, new BrokerAsyncQueryStrategy<T>());
        this.rpcName = rpcName;
    }
    
    /**
     * Implement to convert raw data returned by RPC to a query result.
     * 
     * @param context The query context.
     * @param data Raw RPC data.
     * @return A list of results.
     */
    protected abstract List<T> processData(IQueryContext context, String data);
    
    /**
     * Override to extract arguments from query context and place into argument list.
     * 
     * @param args List to receive argument.
     * @param context The query context.
     */
    protected void createArgumentList(List<Object> args, IQueryContext context) {
    }
    
    /**
     * Prepares an argument list from the query context.
     * 
     * @param context The query context.
     * @return An argument list.
     */
    protected Object[] getArguments(IQueryContext context) {
        List<Object> args = new ArrayList<>();
        createArgumentList(args, context);
        return args.toArray();
    }
    
    /**
     * Override to introduce additional context requirements.
     */
    @Override
    public boolean hasRequired(IQueryContext context) {
        return true;
    }
    
    /**
     * Fetches data in a foreground thread.
     */
    @Override
    public IQueryResult<T> fetch(IQueryContext context) {
        try {
            return QueryUtil.packageResult(processData(context, service.callRPC(rpcName, getArguments(context))));
        } catch (Exception e) {
            return QueryUtil.errorResult(e);
        }
    }
    
    /**
     * Returns the broker servicing this desktop.
     * 
     * @return A broker session.
     */
    public BrokerSession getBroker() {
        return service;
    }
    
    /**
     * Returns the RPC name associated with the service.
     * 
     * @return An RPC name.
     */
    public String getRPCName() {
        return rpcName;
    }
    
}
