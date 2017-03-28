/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.messaging;

import org.carewebframework.api.event.EventUtil;
import org.carewebframework.api.messaging.IMessageConsumer;
import org.carewebframework.api.messaging.Message;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.PollingThread.IHostEventHandler;

/**
 * RPC broker-based message consumer.
 */
public class MessageConsumer implements IMessageConsumer {

    private final IHostEventHandler hostEventHandler = new IHostEventHandler() {

        @Override
        public void onHostEvent(String name, Object data) {
            if (callback != null) {
                Message message = data instanceof Message ? (Message) data : new Message(name, data);
                callback.onMessage(name, message);
            }
        }

    };

    private final BrokerSession brokerSession;

    private IMessageCallback callback;

    /**
     * Consumer is backed by the RPC broker.
     *
     * @param brokerSession Broker session.
     */
    public MessageConsumer(BrokerSession brokerSession) {
        super();
        this.brokerSession = brokerSession;
    }

    /**
     * Initialize after setting all requisite properties.
     */
    public void init() {
        brokerSession.addHostEventHandler(hostEventHandler);
    }

    public void destroy() {
        brokerSession.removeHostEventHandler(hostEventHandler);
    }

    @Override
    public void setCallback(IMessageCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean subscribe(String channel) {
        return brokerSession.eventSubscribe(EventUtil.getEventName(channel), true);
    }

    @Override
    public boolean unsubscribe(String channel) {
        return brokerSession.eventSubscribe(EventUtil.getEventName(channel), false);
    }

}
