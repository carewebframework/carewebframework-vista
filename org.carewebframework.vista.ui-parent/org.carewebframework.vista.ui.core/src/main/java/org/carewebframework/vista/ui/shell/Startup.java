/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.shell;

import java.util.List;

import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.shell.CareWebUtil;
import org.carewebframework.shell.ICareWebStartup;

/**
 * Shows the login greeting message at application startup.
 */
public class Startup implements ICareWebStartup {
    
    @Override
    public boolean execute() {
        List<String> greeting = VistAUtil.getBrokerSession().getPostLoginMessage();
        
        if (!greeting.isEmpty()) {
            CareWebUtil.showMessage(StrUtil.fromList(greeting));
        }
        
        return true;
    }
    
}
