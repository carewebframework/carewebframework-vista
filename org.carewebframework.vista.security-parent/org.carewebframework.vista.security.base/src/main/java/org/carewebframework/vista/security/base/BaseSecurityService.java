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

import org.carewebframework.security.spring.AbstractSecurityService;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.Security;
import org.carewebframework.vista.mbroker.Security.AuthResult;
import org.carewebframework.vista.mbroker.Security.AuthStatus;

/**
 * Security service implementation.
 */
public class BaseSecurityService extends AbstractSecurityService {
    
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
    
}
