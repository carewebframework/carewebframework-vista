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

import org.carewebframework.shell.plugins.PluginStatus;
import org.carewebframework.vista.api.documents.DocumentService;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.api.patient.PatientContext.IPatientContextEvent;

/**
 * Updates the enabled status of the plugin.
 */
public class ActionStatus extends PluginStatus implements IPatientContextEvent {

    /**
     * Returns true if there is no current patient or the current patient has no documents.
     */
    @Override
    public boolean checkDisabled() {
        return !DocumentService.getInstance().hasDocuments(PatientContext.getActivePatient());
    }

    /**
     * @see org.carewebframework.api.context.IContextEvent#canceled()
     */
    @Override
    public void canceled() {
    }

    /**
     * Update the plugin enabled status when the patient selection changes.
     */
    @Override
    public void committed() {
        updateDisabled();
    }

    /**
     * @see org.carewebframework.api.context.IContextEvent#pending(boolean)
     */
    @Override
    public String pending(boolean silent) {
        return null;
    }

}
