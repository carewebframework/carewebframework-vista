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

import org.hl7.fhir.dstu3.model.Encounter;
import org.hspconsortium.cwf.api.encounter.EncounterSearchCriteria;
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
        criteria.setType("H");
        List<Encounter> encounters = encounterSearch.search(criteria);
        return populateListbox(lstInpatient, encounters);
    }

    @Override
    protected boolean isComplete() {
        return getEncounterInternal() != null;
    }

}
