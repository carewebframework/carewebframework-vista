/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.documents;

import org.carewebframework.cal.api.context.PatientContext;
import org.carewebframework.cal.api.context.PatientContext.IPatientContextEvent;
import org.carewebframework.shell.plugins.PluginStatus;
import org.carewebframework.vista.api.documents.DocumentService;

/**
 * Updates the enabled status of the plugin.
 *
 * @author dmartin
 */
public class ActionStatus extends PluginStatus implements IPatientContextEvent {
    
    /**
     * Returns true if there is no current patient or the current patient has no documents.
     */
    @Override
    public boolean checkDisabled() {
        return DocumentService.getInstance().hasDocuments(PatientContext.getActivePatient());
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
