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

import java.util.Date;

import ca.uhn.fhir.model.dstu.composite.PeriodDt;
import ca.uhn.fhir.model.dstu.resource.Encounter;
import ca.uhn.fhir.model.dstu.resource.Encounter.Hospitalization;
import ca.uhn.fhir.model.dstu.resource.Encounter.HospitalizationAccomodation;
import ca.uhn.fhir.model.dstu.resource.Encounter.Participant;
import ca.uhn.fhir.model.dstu.resource.Location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.cal.api.ClientUtil;
import org.carewebframework.cal.api.encounter.EncounterContext;
import org.carewebframework.cal.api.encounter.EncounterParticipantContext;
import org.carewebframework.cal.api.patient.PatientContext;
import org.carewebframework.common.DateUtil;
import org.carewebframework.fhir.common.FhirUtil;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.vista.api.encounter.EncounterUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
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
    
    private Label lblParticipant;
    
    private String noSelectionMessage;
    
    private Component imgLocked;
    
    private Component root;
    
    /**
     * Invoke encounter selection dialog when select button is clicked.
     */
    public void onClick$root() {
        if (PatientContext.getActivePatient() != null) {
            MainController.execute();
        }
    }
    
    /**
     * Initialize controller.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        noSelectionMessage = lblLocation.getValue();
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
            lblParticipant.setValue(null);
            lblServiceCategory.setValue(null);
            imgLocked.setVisible(false);
        } else {
            Location location = encounter.getLocation().isEmpty() ? null : ClientUtil.getResource(encounter
                    .getLocationFirstRep().getLocation(), Location.class);
            String text = location == null ? "" : location.getName().getValue();
            Hospitalization hospitalization = encounter.getHospitalization();
            
            if (!hospitalization.isEmpty()) {
                HospitalizationAccomodation accomodation = FhirUtil.getLast(hospitalization.getAccomodation());
                
                if (accomodation != null && !accomodation.isEmpty()) {
                    Location bed = ClientUtil.getResource(accomodation.getBed(), Location.class);
                    
                    if (bed != null) {
                        text += " (" + bed.getName().getValue() + ")";
                    }
                }
            }
            
            lblLocation.setValue(text);
            PeriodDt period = encounter.getPeriod();
            Date date = period.isEmpty() ? null : period.getStart().getValue();
            lblDate.setValue(DateUtil.formatDate(date));
            Participant participant = EncounterParticipantContext.getActiveParticipant();
            String name = participant == null ? null : FhirUtil.formatName(EncounterUtil.getName(participant));
            lblParticipant.setValue(name);
            lblServiceCategory.setValue(encounter.getTypeFirstRep().getCodingFirstRep().getDisplay().getValue());
            imgLocked.setVisible(EncounterUtil.isLocked(encounter));
        }
        
        Clients.resize(root);
    }
    
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
}
