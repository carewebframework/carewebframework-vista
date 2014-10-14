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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uhn.fhir.model.dstu.resource.Organization;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.security.ISecurityDomain;
import org.carewebframework.cal.api.domain.SecurityDomainProxy;
import org.carewebframework.common.StrUtil;
import org.carewebframework.security.spring.AbstractSecurityService;
import org.carewebframework.security.spring.Constants;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.Security;
import org.carewebframework.vista.mbroker.Security.AuthResult;
import org.carewebframework.vista.mbroker.Security.AuthStatus;

import org.springframework.util.StringUtils;

import org.zkoss.util.resource.Labels;

/**
 * Security service implementation.
 */
public class BaseSecurityService extends AbstractSecurityService {
    
    private static final Log log = LogFactory.getLog(BaseSecurityService.class);
    
    private static boolean initialized;
    
    /**
     * Cached list of available login domains
     */
    private static final List<ISecurityDomain> securityDomains = new ArrayList<ISecurityDomain>();
    
    private BrokerSession brokerSession;
    
    /**
     * Initialize security domain list.
     */
    private static synchronized void init(BrokerSession brokerSession) {
        if (initialized) {
            return;
        }
        
        log.trace("Retrieving Security Domains");
        List<String> results = brokerSession.callRPCList("CIANBRPC DIVGET", null);
        String preLoginMessage = StringUtils.collectionToDelimitedString(brokerSession.getPreLoginMessage(), "\n");
        
        for (String result : results) {
            String[] pcs = StrUtil.split(result, StrUtil.U, 4);
            
            if (!pcs[2].isEmpty()) {
                Organization organization = new Organization();
                organization.setId(pcs[0]);
                organization.setName(pcs[1]);
                Map<String, String> attributes = new HashMap<String, String>();
                attributes.put(Constants.PROP_LOGIN_INFO, preLoginMessage);
                securityDomains.add(new SecurityDomainProxy(organization, attributes));
            }
        }
        
        initialized = true;
    }
    
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
     * Generates a new random password Length of password dictated by
     *
     * @return String The generated password
     */
    @Override
    public String generateRandomPassword() {
        int len = NumberUtils.toInt(Labels.getLabel(Constants.LBL_PASSWORD_RANDOM_CHARACTER_LENGTH), 12);
        return RandomStringUtils.random(len);
    }
    
    /**
     * Returns the list of know security domains.
     */
    @Override
    public List<ISecurityDomain> getSecurityDomains() {
        initialize();
        return securityDomains;
    }
    
    /**
     * Return login disabled message.
     */
    @Override
    public String loginDisabled() {
        AuthResult result = Security.authenticate(brokerSession, "dummy", "dummy", null);
        return result.status == AuthStatus.NOLOGINS ? result.reason : null;
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
     * Initialize cached data if not already done.
     */
    private void initialize() {
        if (!initialized) {
            init(brokerSession);
        }
    }
}
