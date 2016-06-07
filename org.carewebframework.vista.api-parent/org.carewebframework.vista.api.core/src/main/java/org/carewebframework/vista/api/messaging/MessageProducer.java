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

import org.carewebframework.api.messaging.IMessageProducer;
import org.carewebframework.api.messaging.Message;
import org.carewebframework.common.StrUtil;
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
    
    public String[] formatRecipients(String recipients) {
        if (recipients == null) {
            return null;
        }
        
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
        
        return recips;
    }
    
    @Override
    public boolean publish(String channel, Message message) {
        brokerSession.fireRemoteEvent(channel, message, formatRecipients((String) message.getMetadata("recipients")));
        return true;
    }
}
