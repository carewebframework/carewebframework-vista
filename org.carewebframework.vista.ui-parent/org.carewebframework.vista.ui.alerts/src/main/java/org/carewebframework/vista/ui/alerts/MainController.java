/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.alerts;

import static org.carewebframework.common.StrUtil.U;
import static org.carewebframework.common.StrUtil.split;

import java.util.List;

import org.carewebframework.vista.ui.common.CoverSheetBase;

/**
 * Controller for user alerts cover sheet.
 */
public class MainController extends CoverSheetBase<String> {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void init() {
        setup("Alerts", "Alert Detail", "RGCWXQCV LIST", "RGCWXQCV DETAIL", 1, "Alert");
        super.init();
    }
    
    @Override
    protected void render(String dao, List<Object> columns) {
        String pcs[] = split(dao, U, 2);
        
        if (!pcs[0].isEmpty()) {
            columns.add(pcs[1]);
        }
    }
    
}
