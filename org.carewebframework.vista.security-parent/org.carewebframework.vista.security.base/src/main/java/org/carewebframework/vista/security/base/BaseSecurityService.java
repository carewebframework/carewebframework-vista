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
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.common.StrUtil;
import org.carewebframework.security.spring.AbstractSecurityService;
import org.carewebframework.vista.api.domain.Institution;
import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.vista.mbroker.Security;

import org.zkoss.util.resource.Labels;

/**
 * Security service implementation.
 */
public abstract class BaseSecurityService extends AbstractSecurityService {
    
    private static final Log log = LogFactory.getLog(BaseSecurityService.class);
    
    /**
     * Cached list of available login domains
     */
    private static List<Institution> domains;
    
    /**
     * Get domains (institutions) that a user may log into.
     * 
     * @return A list of institution objects.
     */
    public List<Institution> getDomains() {
        if (domains == null) {
            initDomains();
        }
        
        return domains;
    }
    
    /**
     * Initialize domain list.
     */
    private static synchronized void initDomains() {
        if (domains != null) {
            return;
        }
        
        log.trace("Retrieving Security Authorities");
        List<String> results = VistAUtil.getBrokerSession().callRPCList("CIANBRPC DIVGET", null);
        List<Institution> institutions = new ArrayList<Institution>();
        
        for (String result : results) {
            String[] pcs = StrUtil.split(result, StrUtil.U, 4);
            
            if (!pcs[2].isEmpty()) {
                Institution inst = new Institution(NumberUtils.toLong(pcs[0]));
                inst.setName(pcs[1]);
                inst.setAbbreviation(StringUtils.isEmpty(pcs[3]) ? pcs[1] : pcs[3]);
                institutions.add(inst);
            }
        }
        
        domains = institutions;
    }
    
    /**
     * Validates the current user's password.
     * 
     * @param password The password
     * @return True if the password is valid.
     */
    @Override
    public boolean validatePassword(final String password) {
        return Security.validatePassword(VistAUtil.getBrokerSession(), password);
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
        return Security.changePassword(VistAUtil.getBrokerSession(), oldPassword, newPassword);
    }
    
    /**
     * Generates a new random password Length of password dictated by
     * {@link org.carewebframework.vista.security.base.Constants#LBL_PASSWORD_RANDOM_CHARACTER_LENGTH}
     * 
     * @return String The generated password
     */
    @Override
    public String generateRandomPassword() {
        int len = NumberUtils.toInt(Labels.getLabel(Constants.LBL_PASSWORD_RANDOM_CHARACTER_LENGTH), 12);
        return RandomStringUtils.random(len);
    }
    
}
