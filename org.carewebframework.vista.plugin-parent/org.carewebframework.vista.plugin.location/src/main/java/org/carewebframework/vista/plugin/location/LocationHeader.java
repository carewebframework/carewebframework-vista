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
package org.carewebframework.vista.plugin.location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.carewebframework.ui.FrameworkController;
import org.hl7.fhir.dstu3.model.Location;
import org.hspconsortium.cwf.api.location.LocationContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;

/**
 * Controller for location header component.
 */
public class LocationHeader extends FrameworkController implements LocationContext.ILocationContextEvent {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(LocationHeader.class);
    
    private Label locationHeader;
    
    private String noSelectionMessage;
    
    private Component root;
    
    /**
     * Invoke location selection dialog when location button is clicked.
     */
    public void onClick$location() {
        LocationSelection.execute();
    }
    
    /**
     * Initialize controller.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        root = comp;
        noSelectionMessage = locationHeader.getValue();
        committed();
    }
    
    @Override
    public void canceled() {
    }
    
    /**
     * Update the display when the location context changes.
     */
    @Override
    public void committed() {
        Location location = LocationContext.getActiveLocation();
        
        if (log.isDebugEnabled()) {
            log.debug("location: " + location);
        }
        
        String text = location == null ? noSelectionMessage : location.getName();
        locationHeader.setValue(text);
        Clients.resize(root);
    }
    
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
}
