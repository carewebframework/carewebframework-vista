/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.plugin.encounter;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.carewebframework.common.DateUtil;
import org.carewebframework.ui.FrameworkController;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterLocationComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Period;
import org.hspconsortium.cwf.api.ClientUtil;
import org.hspconsortium.cwf.api.encounter.EncounterContext;
import org.hspconsortium.cwf.api.encounter.EncounterParticipantContext;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;

/**
 * Controller for encounter header component.
 */
public class EncounterHeader extends FrameworkController implements EncounterContext.IEncounterContextEvent {

    private final PatientContext.IPatientContextEvent patientContextListener = new PatientContext.IPatientContextEvent() {

        @Override
        public String pending(boolean silent) {
            return null;
        }

        @Override
        public void committed() {
            select.setDisabled(PatientContext.getActivePatient() == null);
        }

        @Override
        public void canceled() {
        }

    };

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(EncounterHeader.class);

    private Label lblLocation;

    private Label lblDate;

    private Label lblServiceCategory;

    private Label lblParticipant;

    private String noSelectionMessage;

    private Component imgLocked;

    private A select;

    /**
     * Initialize controller.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        getAppFramework().registerObject(patientContextListener);
        noSelectionMessage = lblLocation.getValue();
        patientContextListener.committed();
        committed();
    }

    /**
     * Invoke encounter selection dialog when select button is clicked.
     */
    public void onClick$select() {
        if (PatientContext.getActivePatient() != null) {
            MainController.execute();
        }
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
            StringBuilder sb = new StringBuilder();

            for (EncounterLocationComponent location : encounter.getLocation()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(ClientUtil.getResource(location.getLocation(), Location.class).getName());
            }
            lblLocation.setValue(sb.toString());
            Period period = encounter.getPeriod();
            Date date = period.isEmpty() ? null : period.getStart();
            lblDate.setValue(DateUtil.formatDate(date));
            EncounterParticipantComponent participant = EncounterParticipantContext.getActiveParticipant();
            String name = participant == null ? null : FhirUtil.formatName(EncounterUtil.getName(participant));
            lblParticipant.setValue(name);
            lblServiceCategory.setValue(encounter.getTypeFirstRep().getCodingFirstRep().getDisplay());
            imgLocked.setVisible(EncounterUtil.isLocked(encounter));
        }

        Clients.resize(root);
    }

    @Override
    public String pending(boolean silent) {
        return null;
    }

}
