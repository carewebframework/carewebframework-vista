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
package org.carewebframework.vista.plugin.allergies;

import static org.carewebframework.common.StrUtil.U;
import static org.carewebframework.common.StrUtil.piece;
import static org.carewebframework.common.StrUtil.split;

import java.util.List;

import org.carewebframework.ui.zk.ZKUtil;
import org.carewebframework.vista.ui.common.CoverSheetBase;

import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;

/**
 * Controller for patient allergies cover sheet.
 */
public class MainController extends CoverSheetBase<String> {
    
    private static final long serialVersionUID = 1L;
    
    private Menuitem mnuDeleteADR;
    
    private Menuitem mnuEditADR;
    
    private Menuitem mnuNewADR;
    
    private Menuitem mnuSignADR;
    
    private String statusADR;
    
    @Override
    protected void init() {
        setup("Adverse Reactions", "Adverse Reaction Detail", "RGCWARCV LIST", "RGCWARCV DETAIL", 1, "Agent", "Reaction",
            "Status");
        // TODO: Allergy service access
        super.init();
    }
    
    @Override
    protected void render(String dao, List<Object> columns) {
        String[] pcs = split(dao, U, 6);
        
        if (pcs[0].isEmpty()) {
            statusADR = pcs[1];
            return;
        }
        String stat = pcs[5];
        boolean canSign = "1".equals(pcs[4]);
        
        if ("U".equals(stat) && !canSign) {
            return;
        }
        
        columns.add(pcs[1]);
        columns.add(pcs[3]);
        columns.add((canSign ? "*" : "") + formatStatus(stat));
    }
    
    private String formatStatus(String stat) {
        return "U".equals(stat) ? "Unsigned" : "S".equals(stat) ? "Nonverified" : "V".equals(stat) ? "Verified" : "Unknown";
    }
    
    @Override
    protected void renderItem(Listitem item, String dao) {
        super.renderItem(item, dao);
        
        if ("1".equals(piece(dao, U, 5))) {
            ZKUtil.updateStyle(item, "font-color", "blue");
        }
    }
    
    public void onClick$mnuDeleteADR() {
        
    }
    
    public void onClick$mnuEditADR() {
        
    }
    
    public void onClick$mnuNewADR() {
        
    }
    
    public void onClick$mnuSignADR() {
        
    }
    
}
