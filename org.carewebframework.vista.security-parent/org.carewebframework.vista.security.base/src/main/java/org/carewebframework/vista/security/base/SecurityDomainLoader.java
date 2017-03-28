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
        String defaultDomain = null;
        
        for (String result : results) {
            String[] pcs = StrUtil.split(result, StrUtil.U, 4);
            
            if (defaultDomain == null) {
                defaultDomain = pcs[0];
            } else {
                SecurityDomain securityDomain = new SecurityDomain(pcs[1], pcs[0]);
                securityDomain.setAttribute(Constants.PROP_LOGIN_INFO, preLoginMessage);
                SecurityDomainRegistry.registerSecurityDomain(securityDomain);
                
                if (pcs[0].equals(defaultDomain)) {
                    securityDomain.setAttribute("default", "true");
                }
            }
        }
        
        brokerSession.disconnect();
    }
    
}
