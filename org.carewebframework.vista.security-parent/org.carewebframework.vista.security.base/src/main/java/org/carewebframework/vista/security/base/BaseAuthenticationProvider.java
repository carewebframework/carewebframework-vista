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

import java.util.List;

import ca.uhn.fhir.model.dstu.resource.User;

import org.apache.commons.lang.StringUtils;

import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.api.domain.IUser;
import org.carewebframework.api.security.ISecurityDomain;
import org.carewebframework.cal.api.user.UserProxy;
import org.carewebframework.security.spring.AbstractAuthenticationProvider;
import org.carewebframework.security.spring.AuthenticationCancelledException;
import org.carewebframework.security.spring.CWFAuthenticationDetails;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.Security.AuthResult;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;

/**
 * Provides authentication support for the framework. Takes provided authentication credentials and
 * authenticates them against the database.
 */
public class BaseAuthenticationProvider extends AbstractAuthenticationProvider {
    
    private BrokerSession brokerSession;
    
    public BaseAuthenticationProvider() {
        super(false);
    }
    
    protected BaseAuthenticationProvider(boolean debugRole) {
        super(debugRole);
    }
    
    protected BaseAuthenticationProvider(List<String> grantedAuthorities) {
        super(grantedAuthorities);
    }
    
    /**
     * Performs user authentication.
     *
     * @param username Username for the login.
     * @param password Password for the login (ignored if the user is pre-authenticated).
     * @param domain Domain for which the login is requested.
     * @param details Details object associated with authentication.
     * @return Authorization result
     */
    @Override
    protected IUser authenticate(String username, String password, ISecurityDomain domain, CWFAuthenticationDetails details) {
        AuthResult authResult = brokerSession.authenticate(username, password, domain.getLogicalId());
        IUser user = getAuthenticatedUser();
        checkAuthResult(authResult, user);
        return user;
    }
    
    @Override
    protected List<String> getAuthorities(IUser user) {
        return user == null ? null : brokerSession.callRPCList("RGCWFUSR GETPRIV", null, user.getLogicalId());
    }
    
    private IUser getAuthenticatedUser() {
        try {
            User user = brokerSession.isAuthenticated() ? DomainFactoryRegistry.fetchObject(User.class,
                Integer.toString(brokerSession.getUserId())) : null;
            return user == null ? null : new UserProxy(user);
        } catch (Exception e) {
            return null;
        }
    }
    
    @SuppressWarnings("deprecation")
    private void checkAuthResult(AuthResult result, IUser user) throws AuthenticationException {
        switch (result.status) {
            case SUCCESS:
                return;
                
            case CANCELED:
                throw new AuthenticationCancelledException(StringUtils.defaultIfEmpty(result.reason,
                    "Authentication attempt was cancelled."));
                
            case EXPIRED:
                throw new CredentialsExpiredException(
                        StringUtils.defaultIfEmpty(result.reason, "Your password has expired."), user);
                
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
    
    public BrokerSession getBrokerSession() {
        return brokerSession;
    }
    
    public void setBrokerSession(BrokerSession brokerSession) {
        this.brokerSession = brokerSession;
    }
    
}
