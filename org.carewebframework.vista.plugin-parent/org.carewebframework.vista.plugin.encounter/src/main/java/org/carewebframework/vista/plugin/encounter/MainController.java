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

import java.util.EnumSet;
import java.util.Set;

import org.carewebframework.api.FrameworkUtil;
import org.carewebframework.common.MiscUtil;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.ui.zk.PopupDialog;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.vista.api.encounter.EncounterFlag;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.api.encounter.EncounterContext;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

/**
 * Main encounter selection controller.
 */
public class MainController extends FrameworkController implements PatientContext.IPatientContextEvent {

    private static final long serialVersionUID = 1L;

    private static final String SELECTION_DIALOG = Constants.RESOURCE_PREFIX + "encounterSelection.zul";

    private Tabbox tabbox;

    protected Button btnOK;

    protected Component toolbar;

    protected Patient patient;

    protected Set<EncounterFlag> flags = EnumSet.noneOf(EncounterFlag.class);

    private EncounterSelector selector;

    private boolean needsInit = true;

    /**
     * Displays the encounter selection dialog.
     *
     * @param flags The encounter flags.
     */
    public static void execute(EncounterFlag... flags) {
        try {
            Window dlg = (Window) FrameworkUtil.getAttribute(SELECTION_DIALOG);

            if (dlg == null || dlg.getPage() == null) {
                dlg = PopupDialog.popup(SELECTION_DIALOG, true, true, false);
                FrameworkUtil.setAttribute(SELECTION_DIALOG, dlg);
            }

            MainController sel = (MainController) getController(dlg);
            sel.setEncounterFlags(EncounterFlag.flags(flags));

            if (sel.needsInit) {
                sel.init();
            }

            dlg.doModal();
        } catch (Exception e) {
            FrameworkUtil.setAttribute(SELECTION_DIALOG, null);
            throw MiscUtil.toUnchecked(e);
        }
    }

    /**
     * Returns the encounter selector associated with the specified tab panel.
     *
     * @param panel A tab panel.
     * @return The associated encounter selector.
     */
    private EncounterSelector getSelector(Tabpanel panel) {
        return panel == null ? null : (EncounterSelector) EncounterSelector.getController(panel);
    }

    /**
     * Set the flags that affect encounter selection.
     *
     * @param flags Encounter flags.
     */
    private void setEncounterFlags(Set<EncounterFlag> flags) {
        if (!flags.equals(this.flags)) {
            this.flags = flags;
            needsInit = true;
        }
    }

    /**
     * Update state when tab selection changes.
     */
    public void onSelect$tabbox() {
        selectionChanged();
    }

    /**
     * Hide the dialog when cancelled.
     */
    public void onClick$btnCancel() {
        close();
    }

    /**
     * Updated the active encounter selector when the tab selection has changed. Sends an activation
     * signal to the previous and new selector.
     */
    private void selectionChanged() {
        if (selector != null) {
            selector.activate(false);
            btnOK.setDisabled(true);
        }

        selector = getSelector(tabbox.getSelectedPanel());

        if (selector != null) {
            selector.activate(true);
            btnOK.setDisabled(!selector.isComplete());
        }
    }

    /**
     * Change the encounter context to the selected encounter and close the dialog.
     */
    public void onClick$btnOK() {
        Encounter encounter = selector.getEncounter();

        if (encounter != null) {
            String s = EncounterUtil.validEncounter(encounter, flags);

            if (s != null) {
                PromptDialog.showWarning(s);
                return;
            }
            EncounterContext.changeEncounter(encounter);
            close();
        }
    }

    /**
     * Close the main dialog.
     */
    private void close() {
        root.setVisible(false);
    }

    /**
     * Initialize the selector controllers and determines which tab is selected by default.
     */
    private void init() {
        needsInit = false;
        patient = PatientContext.getActivePatient();
        boolean selected = false;

        for (Component child : tabbox.getTabpanels().getChildren()) {
            Tabpanel panel = (Tabpanel) child;

            if (getSelector(panel).init(this) && !selected) {
                selected = true;
                panel.getLinkedTab().setSelected(true);
            }
        }

        selectionChanged();
    }

    @Override
    public void canceled() {
    }

    @Override
    public void committed() {
        needsInit = true;
    }

    @Override
    public String pending(boolean silent) {
        return null;
    }

}
