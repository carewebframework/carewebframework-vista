/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.security.testharness;

import org.carewebframework.vista.security.base.BaseSecurityService;

/**
 * Security service implementation.
 */
public class SecurityServiceImpl extends BaseSecurityService {
    
    /**
     * Changes the user's password.
     * 
     * @param oldPassword Current password.
     * @param newPassword New password.
     * @return Null or empty if succeeded. Otherwise, displayable reason why change failed.
     */
    @Override
    public String changePassword(String oldPassword, String newPassword) {
        return "Not allowed in test harness.";
    }
    
    @Override
    public boolean canChangePassword() {
        return false;
    }
    
}
