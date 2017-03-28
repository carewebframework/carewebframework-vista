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
import java.util.List;

import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.zk.DateTimebox;
import org.carewebframework.vista.api.encounter.EncounterFlag;
import org.carewebframework.vista.api.encounter.EncounterUtil;
import org.carewebframework.vista.plugin.location.LocationSelection;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hspconsortium.cwf.api.location.LocationContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

/**
 * Encounter selector for creating ad hoc encounters.
 */
public class NewSelector extends EncounterSelector {

    private static final long serialVersionUID = 1L;

    private Listbox lstLocation;

    private Textbox txtLocation;

    private Combobox cboServiceCategory;

    private DateTimebox datEncounter;

    private Checkbox chkForceCreate;

    /**
     * Wire variables and events.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        for (CodeableConcept cat : EncounterUtil.getServiceCategories()) {
            Coding coding = cat.getCodingFirstRep();
            Comboitem item = cboServiceCategory.appendItem(coding.getDisplay());
            item.setValue(coding.getCode());
            item.setTooltiptext(cat.getText());
        }

        List<String> data = broker.callRPCList("RGCWENCX CLINLOC", null, "", 1, 9999);

        for (String itm : data) {
            String[] pcs = StrUtil.split(itm, StrUtil.U, 3);
            Listitem item = lstLocation.appendItem(pcs[1], pcs[0]);
            item.setAttribute("sc", pcs[2]);
        }

    }

    public void onSelect$lstLocation() {
        Listitem item = lstLocation.getSelectedItem();
        String sc = (String) item.getAttribute("sc");

        if (sc.isEmpty()) {
            cboServiceCategory.setSelectedItem(null);
        } else {
            for (Comboitem ci : cboServiceCategory.getItems()) {
                if (sc.equals(ci.getValue())) {
                    cboServiceCategory.setSelectedItem(ci);
                    break;
                }
            }
        }

        statusChanged();
    }

    public void onSelect$cboServiceCategory() {
        statusChanged();
    }

    public void onChange$datEncounter() {
        statusChanged();
    }

    public void onClick$btnLocation() throws Exception {
        LocationSelection.locationLookup(txtLocation.getValue(), lstLocation, LocationContext.getActiveLocation());
    }

    @Override
    protected Encounter getEncounterInternal() {
        if (!isComplete()) {
            return null;
        }

        Listitem item = lstLocation.getSelectedItem();
        String locid = item == null ? null : (String) item.getValue();
        Location location = locid != null ? DomainFactoryRegistry.fetchObject(Location.class, locid) : null;
        Comboitem cboitem = cboServiceCategory.getSelectedItem();
        String sc = cboitem == null ? null : (String) cboitem.getValue();
        Date date = datEncounter.getDate();
        Encounter encounter = EncounterUtil.create(mainController.patient, date, location, sc);

        if (chkForceCreate.isChecked()) {
            mainController.flags.add(EncounterFlag.FORCE);
        } else {
            mainController.flags.remove(EncounterFlag.FORCE);
        }

        return encounter;
    }

    @Override
    protected boolean init(MainController mainController) {
        super.init(mainController);
        datEncounter.setDate(new Date());
        lstLocation.setSelectedItem(null);
        cboServiceCategory.setSelectedItem(null);
        loadEncounterParticipants(new Encounter());
        boolean forceVisit = EncounterFlag.hasFlag(mainController.flags, EncounterFlag.FORCE);
        chkForceCreate.setChecked(forceVisit);
        chkForceCreate.setDisabled(forceVisit);
        return true;
    }

    @Override
    protected boolean isComplete() {
        return lstLocation.getSelectedItem() != null && cboServiceCategory.getSelectedItem() != null
                && datEncounter.getDate() != null;
    }

}
