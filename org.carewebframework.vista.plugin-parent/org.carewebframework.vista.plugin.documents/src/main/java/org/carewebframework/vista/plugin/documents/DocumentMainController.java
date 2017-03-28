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
package org.carewebframework.vista.plugin.documents;

import org.carewebframework.shell.plugins.PluginController;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Include;

/**
 * This is the main controller for the clinical document display component. It doesn't do much other
 * than to control which of the two views (document list vs document display) is visible.
 */
public class DocumentMainController extends PluginController {
    
    private static final long serialVersionUID = 1L;
    
    private DocumentListController listController;
    
    private DocumentDisplayController displayController;
    
    private Include documentList;
    
    private Include documentDisplay;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        documentDisplay.setVisible(false);
        attachController(documentList, listController);
        attachController(documentDisplay, displayController);
    }
    
    @Override
    public void attachController(Component comp, Composer<Component> controller) throws Exception {
        super.attachController(comp, controller);
        comp.addForward("onViewOpen", root, null);
    }
    
    public void onViewOpen(Event event) {
        boolean open = (Boolean) ZKUtil.getEventOrigin(event).getData();
        displayController.setDocuments(!open ? null : listController.getSelectedDocuments());
        documentList.setVisible(!open);
        documentDisplay.setVisible(open);
    }
    
    public DocumentDisplayController getDisplayController() {
        return displayController;
    }
    
    public void setDisplayController(DocumentDisplayController displayController) {
        this.displayController = displayController;
    }
    
    public DocumentListController getListController() {
        return listController;
    }
    
    public void setListController(DocumentListController listController) {
        this.listController = listController;
    }
    
}
