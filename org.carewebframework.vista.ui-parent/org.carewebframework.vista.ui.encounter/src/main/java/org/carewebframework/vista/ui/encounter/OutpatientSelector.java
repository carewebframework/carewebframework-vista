/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.encounter;

import java.util.List;

import ca.uhn.fhir.model.dstu2.resource.Encounter;

import org.carewebframework.ui.zk.DateRangePicker;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;

/**
 * Encounter selector for outpatient encounters.
 */
public class OutpatientSelector extends EncounterSelector {
    
    private static final long serialVersionUID = 1L;
    
    private Listbox lstOutpatient;
    
    private DateRangePicker rngDateRange;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lstOutpatient.setItemRenderer(new EncounterRenderer());
        rngDateRange.getItemAtIndex(0).setLabel("Default Date Range");
        rngDateRange.setSelectedIndex(0);
    }
    
    public void onSelect$lstOutpatient() {
        loadEncounterParticipants(getSelectedEncounter(lstOutpatient));
        statusChanged();
    }
    
    public void onSelectRange$rngDateRange() {
        loadEncounters();
        statusChanged();
    }
    
    private boolean loadEncounters() {
        List<String> data = broker.callRPCList("RGCWENCX VISITLST", null, mainController.patient.getId().getIdPart(),
            rngDateRange.getStartDate(), rngDateRange.getEndDate());
        return populateListbox(lstOutpatient, data);
    }
    
    @Override
    protected Encounter getEncounterInternal() {
        return getSelectedEncounter(lstOutpatient);
    }
    
    /**
     * Initialize the dialog. Performs a query to return all existing encounters with the set time
     * window and populates the outpatient list from this.
     */
    @Override
    protected boolean init(MainController mainController) {
        super.init(mainController);
        rngDateRange.setParent(mainController.toolbar);
        return loadEncounters();
    }
    
    @Override
    protected void activate(boolean activate) {
        rngDateRange.setVisible(activate);
    }
    
    @Override
    protected boolean isComplete() {
        return getEncounterInternal() != null;
    }
    
}
