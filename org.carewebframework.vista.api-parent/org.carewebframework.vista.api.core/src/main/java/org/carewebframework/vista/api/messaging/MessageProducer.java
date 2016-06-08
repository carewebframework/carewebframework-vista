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

import java.util.ArrayList;
import java.util.List;

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
        brokerSession.fireRemoteEvent(channel, message,
            formatRecipients((Recipient[]) message.getMetadata("cwf.pub.recipients")));
        return true;
    }
}
