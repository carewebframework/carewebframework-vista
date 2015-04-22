/**
 * The contents of this file are subject to the Regenstrief Public License
 * Version 1.0 (the "License"); you may not use this file except in compliance with the License.
 * Please contact Regenstrief Institute if you would like to obtain a copy of the license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) Regenstrief Institute.  All Rights Reserved.
 */
package org.carewebframework.vista.security.base;

import java.util.List;

import org.carewebframework.api.security.SecurityDomainRegistry;
import org.carewebframework.common.StrUtil;
import org.carewebframework.security.spring.Constants;
import org.carewebframework.vista.mbroker.BrokerSession;

import org.springframework.util.StringUtils;

/**
 * Security Domain Loader.
 */
public class SecurityDomainLoader {
    
    public SecurityDomainLoader(BrokerSession brokerSession) {
        List<String> results = brokerSession.callRPCList("RGNETBRP DIVGET", null);
        String preLoginMessage = StringUtils.collectionToDelimitedString(brokerSession.getPreLoginMessage(), "\n");
        
        for (String result : results) {
            String[] pcs = StrUtil.split(result, StrUtil.U, 4);
            
            if (!pcs[2].isEmpty()) {
                SecurityDomain securityDomain = new SecurityDomain(pcs[1], pcs[0]);
                securityDomain.setAttribute(Constants.PROP_LOGIN_INFO, preLoginMessage);
                SecurityDomainRegistry.registerSecurityDomain(securityDomain);
            }
        }
        
        brokerSession.disconnect();
    }
    
}
