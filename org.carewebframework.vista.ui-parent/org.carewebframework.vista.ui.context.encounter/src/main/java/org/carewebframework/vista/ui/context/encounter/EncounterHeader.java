/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.context.encounter;

import ca.uhn.fhir.model.dstu.resource.Encounter;
import ca.uhn.fhir.model.dstu.resource.Practitioner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.cal.api.context.EncounterContext;
import org.carewebframework.cal.api.context.PatientContext;
import org.carewebframework.fhir.common.FhirUtil;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.vista.api.domain.EncounterUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

/**
 * Controller for encounter header component. Note that this controller has two context change
 * listeners, one implemented by the controller itself (encounter context listener) and one inner
 * (patient context listener). This is done because Java does not permit disambiguation of
 * conflicting interface method names.
 */
public class EncounterHeader extends FrameworkController implements EncounterContext.IEncounterContextEvent {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(EncounterHeader.class);
    
    private Label lblLocation;
    
    private Label lblDate;
    
    private Label lblServiceCategory;
    
    private Label lblProvider;
    
    private String noSelectionMessage;
    
    private Image imgLocked;
    
    private Component root;
    
    /**
     * Invoke encounter selection dialog when select button is clicked.
     */
    public void onClick$root() {
        if (PatientContext.getActivePatient() != null) {
            EncounterSelection.execute();
        }
    }
    
    /**
     * Initialize controller.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        noSelectionMessage = lblLocation.getValue();
        imgLocked.setSrc(Constants.ICON_LOCKED);
        committed();
    }
    
    @Override
    public void canceled() {
    }
    
    /**
     * Update the display when the encounter context changes.
     */
    @Override
    public void committed() {
        Encounter encounter = EncounterContext.getActiveEncounter();
        
        if (log.isDebugEnabled()) {
            log.debug("encounter: " + encounter);
        }
        
        if (encounter == null) {
            lblLocation.setValue(noSelectionMessage);
            lblDate.setValue(null);
            lblProvider.setValue(null);
            lblServiceCategory.setValue(null);
            imgLocked.setVisible(false);
        } else {
            String text = encounter.getLocation().isEmpty() ? "" : FhirUtil.getLast(encounter.getLocation()).toString();
            /*
            if (!StringUtils.isEmpty(encounter.getBed())) {
                text += " (" + encounter.getBed() + ")";
            } */
            
            lblLocation.setValue(text);
            lblDate.setValue(encounter.getPeriod() == null ? null : encounter.getPeriod().getStart().toString());
            Practitioner provider = EncounterUtil.getCurrentProvider(encounter);
            lblProvider.setValue(provider == null ? null : provider.getName().toString());
            lblServiceCategory.setValue(encounter.getType().toString());
            imgLocked.setVisible(EncounterUtil.isLocked(encounter));
        }
        
        Clients.resize(root);
    }
    
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
}
