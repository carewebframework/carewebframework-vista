/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.security.impl;

import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.vista.mbroker.Security;
import org.carewebframework.vista.mbroker.Security.AuthResult;
import org.carewebframework.vista.mbroker.Security.AuthStatus;
import org.carewebframework.vista.security.base.BaseSecurityService;
import org.carewebframework.vista.security.base.Constants;
import org.carewebframework.ui.zk.PromptDialog;

import org.zkoss.util.resource.Labels;

/**
 * Security service implementation.
 */
public class SecurityServiceImpl extends BaseSecurityService {
    
    private boolean canChangePassword = true;
    
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
     * @see org.carewebframework.api.security.ISecurityService#changePassword()
     */
    @Override
    public void changePassword() {
        if (canChangePassword()) {
            ChangePasswordController.show();
        } else {
            PromptDialog.showWarning(Labels.getLabel(Constants.LBL_CHANGE_PASSWORD_UNAVAILABLE));
        }
    }
    
    /**
     * @see org.carewebframework.api.security.ISecurityService#canChangePassword()
     */
    @Override
    public boolean canChangePassword() {
        return canChangePassword;
    }
    
    public void setCanChangePassword(boolean canChangePassword) {
        this.canChangePassword = canChangePassword;
    }
    
    /**
     * Return login disabled message.
     */
    @Override
    public String loginDisabled() {
        AuthResult result = Security.authenticate(VistAUtil.getBrokerSession(), "dummy", "dummy", null);
        return result.status == AuthStatus.NOLOGINS ? result.reason : null;
    }
    
}
