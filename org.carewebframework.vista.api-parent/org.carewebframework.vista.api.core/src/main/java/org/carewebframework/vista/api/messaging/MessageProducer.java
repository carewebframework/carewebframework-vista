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
package org.carewebframework.vista.api.messaging;

import java.util.ArrayList;
import java.util.List;

import org.carewebframework.api.event.EventUtil;
import org.carewebframework.api.messaging.IMessageProducer;
import org.carewebframework.api.messaging.Message;
import org.carewebframework.api.messaging.Recipient;
import org.carewebframework.vista.mbroker.BrokerSession;

/**
 * RPC broker-based message producer
 */
public class MessageProducer implements IMessageProducer {

    private final BrokerSession brokerSession;

    /**
     * Producer is backed by RPC broker.
     */
    public MessageProducer(BrokerSession brokerSession) {
        this.brokerSession = brokerSession;
    }

    public String[] formatRecipients(Recipient[] recipients) {
        if (recipients == null) {
            return null;
        }

        List<String> result = new ArrayList<>();

        for (Recipient recipient : recipients) {
            switch (recipient.getType()) {
                case USER:
                    result.add(recipient.getValue());
                    break;

                case SESSION:
                    result.add("#" + recipient.getValue());
            }
        }

        return result.isEmpty() ? null : (String[]) result.toArray();
    }

    @Override
    public boolean publish(String channel, Message message) {
        String eventName = EventUtil.getEventName(channel);

        brokerSession.fireRemoteEvent(eventName, message,
            formatRecipients((Recipient[]) message.getMetadata("cwf.pub.recipients")));
        return true;
    }
}
