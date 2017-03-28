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
package org.carewebframework.vista.plugin.encounter;

import java.util.List;

import org.carewebframework.ui.zk.DateRangePicker;
import org.hl7.fhir.dstu3.model.Encounter;
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
        List<String> data = broker.callRPCList("RGCWENCX VISITLST", null, mainController.patient.getIdElement().getIdPart(),
            rngDateRange.getStartDate(), rngDateRange.getEndDate(), null, "HX");
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
