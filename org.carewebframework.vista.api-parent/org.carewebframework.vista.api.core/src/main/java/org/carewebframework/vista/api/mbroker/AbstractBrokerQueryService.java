/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
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
        return QueryUtil.<T> packageResult(processData(context, service.callRPC(rpcName, getArguments(context))));
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
