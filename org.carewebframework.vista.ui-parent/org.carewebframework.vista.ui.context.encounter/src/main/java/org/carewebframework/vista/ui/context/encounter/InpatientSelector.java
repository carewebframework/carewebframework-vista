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

import java.util.List;

import ca.uhn.fhir.model.dstu.resource.Encounter;

import org.carewebframework.cal.api.encounter.EncounterSearchCriteria;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;

/**
 * Selector for inpatient encounters.
 */
public class InpatientSelector extends EncounterSelector {
    
    private static final long serialVersionUID = 1L;
    
    private Listbox lstInpatient;
    
    /**
     * Wire variables and events.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lstInpatient.setItemRenderer(new EncounterRenderer());
    }
    
    public void onSelect$lstInpatient() {
        loadEncounterParticipants(getSelectedEncounter(lstInpatient));
        statusChanged();
    }
    
    @Override
    protected Encounter getEncounterInternal() {
        return getSelectedEncounter(lstInpatient);
    }
    
    @Override
    protected boolean init(MainController mainController) {
        super.init(mainController);
        EncounterSearchCriteria criteria = new EncounterSearchCriteria();
        criteria.setPatient(mainController.patient);
        criteria.setType("I");
        List<Encounter> encounters = encounterSearch.search(criteria);
        return populateListbox(lstInpatient, encounters);
    }
    
    @Override
    protected boolean isComplete() {
        return getEncounterInternal() != null;
    }
    
}
