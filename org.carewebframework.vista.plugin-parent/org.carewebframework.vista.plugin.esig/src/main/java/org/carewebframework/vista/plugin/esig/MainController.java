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
package org.carewebframework.vista.plugin.esig;

import org.carewebframework.vista.esig.IESigService;

import org.carewebframework.api.event.EventManager;
import org.carewebframework.api.event.IGenericEvent;
import org.carewebframework.ui.FrameworkController;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;

/**
 * This is the controller for the esignature plugin.
 * 
 * 
 */
public class MainController extends FrameworkController implements IGenericEvent<Object> {
    
    private static final long serialVersionUID = 1L;
    
    private IESigService eSigService;
    
    private Button button;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        EventManager.getInstance().subscribe("ESIG", this);
        update();
    }
    
    private void update() {
        int i = eSigService.getCount();
        button.setDisabled(i == 0);
        button.setTooltiptext(String.format("There %s %s item%s awaiting review.", i == 1 ? "is" : "are", i == 0 ? "no" : i,
            i != 1 ? "s" : ""));
    }
    
    public void onClick$button() throws Exception {
        ESigViewer.execute(eSigService, null);
    }
    
    @Override
    public void eventCallback(String eventName, Object eventData) {
        update();
    }
    
    public void seteSigService(IESigService eSigService) {
        this.eSigService = eSigService;
    }
    
    public IESigService geteSigService() {
        return eSigService;
    }
    
}
