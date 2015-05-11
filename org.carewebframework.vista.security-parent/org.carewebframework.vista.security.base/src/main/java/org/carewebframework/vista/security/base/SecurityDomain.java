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
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.api.domain.IUser;
import org.carewebframework.api.domain.User;
import org.carewebframework.api.security.ISecurityDomain;
import org.carewebframework.security.spring.AuthenticationCancelledException;
import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.Security.AuthResult;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;

import org.zkoss.zk.ui.Sessions;

/**
 * Security Domain implementation.
 */
public class SecurityDomain implements ISecurityDomain {
    
    private static final long serialVersionUID = 1L;
    
    private final String name;
    
    private final String logicalId;
    
    private final Properties properties = new Properties();
    
    public SecurityDomain(String name, String logicalId) {
        this.name = name;
        this.logicalId = logicalId;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getLogicalId() {
        return logicalId;
    }
    
    @Override
    public String getAttribute(String name) {
        return properties.getProperty(name);
    }
    
    protected void setAttribute(String name, String value) {
        properties.setProperty(name, value);
    }
    
    @Override
    public IUser authenticate(String username, String password) {
        BrokerSession broker = VistAUtil.getBrokerSession();
        AuthResult authResult = broker.authenticate(username, password, getLogicalId());
        User user = getAuthenticatedUser(broker);
        user.setLoginName(username);
        user.setPassword(password);
        user.setSecurityDomain(this);
        checkAuthResult(authResult, user);
        return user;
    }
    
    @Override
    public List<String> getGrantedAuthorities(IUser user) {
        return user == null ? null : VistAUtil.getBrokerSession().callRPCList("RGCWFUSR GETPRIV", null, user.getLogicalId());
    }
    
    @Override
    public SecurityDomain getNativeSecurityDomain() {
        return this;
    }
    
    private User getAuthenticatedUser(BrokerSession broker) {
        return broker.isAuthenticated() ? DomainFactoryRegistry
                .fetchObject(User.class, Integer.toString(broker.getUserId())) : null;
    }
    
    private void checkAuthResult(AuthResult result, IUser user) throws AuthenticationException {
        switch (result.status) {
            case SUCCESS:
                return;
                
            case CANCELED:
                throw new AuthenticationCancelledException(StringUtils.defaultIfEmpty(result.reason,
                    "Authentication attempt was cancelled."));
                
            case EXPIRED:
                Sessions.getCurrent().setAttribute(org.carewebframework.security.spring.Constants.SAVED_USER, user);
                throw new CredentialsExpiredException(
                        StringUtils.defaultIfEmpty(result.reason, "Your password has expired."));
                
            case FAILURE:
                throw new BadCredentialsException(StringUtils.defaultIfEmpty(result.reason,
                    "Your username or password was not recognized."));
                
            case LOCKED:
                throw new LockedException(StringUtils.defaultIfEmpty(result.reason,
                    "Your user account has been locked and cannot be accessed."));
                
            case NOLOGINS:
                throw new DisabledException(StringUtils.defaultIfEmpty(result.reason, "Logins are currently disabled."));
        }
    }
    
}
