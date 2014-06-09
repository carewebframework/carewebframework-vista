/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.event;

import java.io.Serializable;

import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.PollingThread.IHostEventHandler;

import org.carewebframework.api.event.AbstractGlobalEventDispatcher;
import org.carewebframework.common.StrUtil;

/**
 * This class is responsible for communicating with the global messaging server. It interacts with
 * the local event manager and is responsible for dispatching (publishing) events to be distributing
 * globally to the messaging server and receiving subscribed events from the same and passing them
 * on to the local event dispatcher for local distribution.
 */
public class GlobalEventDispatcher extends AbstractGlobalEventDispatcher implements IHostEventHandler {
    
    private BrokerSession brokerSession;
    
    /**
     * Create the global event dispatcher.
     */
    public GlobalEventDispatcher() {
        super();
    }
    
    /**
     * Initialize after setting all requisite properties.
     */
    @Override
    public void init() {
        super.init();
        brokerSession.addHostEventHandler(this);
    }
    
    @Override
    public void destroy() {
        brokerSession.removeHostEventHandler(this);
        super.destroy();
    }
    
    /**
     * Process a host event subscribe/unsubscribe request.
     */
    @Override
    public void subscribeRemoteEvent(String eventName, boolean subscribe) {
        brokerSession.eventSubscribe(eventName, subscribe);
    }
    
    /**
     * Fires a host event.
     * 
     * @param eventName Name of the event.
     * @param eventData Data object associated with the event.
     * @param recipients List of recipients for the event (null or empty string means all
     *            subscribers).
     */
    @Override
    public void fireRemoteEvent(String eventName, Serializable eventData, String recipients) {
        String[] recips = StrUtil.split(recipients, ",");
        
        for (int i = 0; i < recips.length; i++) {
            String recip = recips[i];
            
            if (recip.startsWith("u-")) {
                recip = recip.substring(2);
            } else {
                recip = "#" + recip;
            }
            
            recips[i] = recip;
        }
        
        brokerSession.fireRemoteEvent(eventName, eventData, recips);
    }
    
    public BrokerSession getBrokerSession() {
        return brokerSession;
    }
    
    public void setBrokerSession(BrokerSession brokerSession) {
        this.brokerSession = brokerSession;
    }
    
    @Override
    public void onHostEvent(String name, Object data) {
        if (beginMessageProcessing()) {
            localEventDelivery(name, data);
            endMessageProcessing();
        }
    }
    
    @Override
    protected String getEndpointId() {
        return Integer.toString(brokerSession.getId());
    }
    
    @Override
    public String getAppName() {
        return brokerSession.getConnectionParams().getAppid();
    }
}
