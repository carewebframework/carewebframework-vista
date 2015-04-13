/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.security.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uhn.fhir.model.dstu.resource.Organization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.cal.api.SecurityDomainProxy;
import org.carewebframework.common.StrUtil;
import org.carewebframework.security.spring.AbstractSecurityService;
import org.carewebframework.security.spring.Constants;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.Security;
import org.carewebframework.vista.mbroker.Security.AuthResult;
import org.carewebframework.vista.mbroker.Security.AuthStatus;

import org.springframework.util.StringUtils;

/**
 * Security service implementation.
 */
public class BaseSecurityService extends AbstractSecurityService {
    
    private static final Log log = LogFactory.getLog(BaseSecurityService.class);
    
    private BrokerSession brokerSession;
    
    /**
     * Validates the current user's password.
     *
     * @param password The password
     * @return True if the password is valid.
     */
    @Override
    public boolean validatePassword(final String password) {
        return Security.validatePassword(brokerSession, password);
    }
    
    /**
     * Changes the user's password.
     *
     * @param oldPassword Current password.
     * @param newPassword New password.
     * @return Null or empty if succeeded. Otherwise, displayable reason why change failed.
     */
    @Override
    public String changePassword(final String oldPassword, final String newPassword) {
        return Security.changePassword(brokerSession, oldPassword, newPassword);
    }
    
    /**
     * Return login disabled message.
     */
    @Override
    public String loginDisabled() {
        AuthResult result = brokerSession.authenticate("dummy", "dummy", null);
        return result.status == AuthStatus.NOLOGINS ? result.reason : null;
    }
    
    /**
     * Override to disconnect broker.
     */
    @Override
    public boolean logout(boolean force, String target, String message) {
        boolean result = super.logout(force, target, message);
        
        if (result) {
            brokerSession.disconnect();
        }
        
        return result;
    }
    
    /**
     * Injection point for broker session.
     * 
     * @param brokerSession The broker session.
     */
    public void setBrokerSession(BrokerSession brokerSession) {
        this.brokerSession = brokerSession;
    }
    
    /**
     * Initialize security domain list.
     */
    @Override
    protected void initSecurityDomains() {
        log.trace("Retrieving Security Domains");
        List<String> results = brokerSession.callRPCList("RGNETBRP DIVGET", null);
        String preLoginMessage = StringUtils.collectionToDelimitedString(brokerSession.getPreLoginMessage(), "\n");
        
        for (String result : results) {
            String[] pcs = StrUtil.split(result, StrUtil.U, 4);
            
            if (!pcs[2].isEmpty()) {
                Organization organization = new Organization();
                organization.setId(pcs[0]);
                organization.setName(pcs[1]);
                Map<String, String> attributes = new HashMap<String, String>();
                attributes.put(Constants.PROP_LOGIN_INFO, preLoginMessage);
                registerSecurityDomain(new SecurityDomainProxy(organization, attributes));
            }
        }
    }
    
}
