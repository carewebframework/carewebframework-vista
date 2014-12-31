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

import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import ca.uhn.fhir.model.dstu.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu.composite.CodingDt;
import ca.uhn.fhir.model.dstu.resource.Encounter;
import ca.uhn.fhir.model.dstu.resource.Location;
import ca.uhn.fhir.model.dstu.resource.Patient;

import org.carewebframework.api.FrameworkUtil;
import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.cal.api.context.EncounterContext;
import org.carewebframework.cal.api.context.LocationContext;
import org.carewebframework.cal.api.context.PatientContext;
import org.carewebframework.common.DateUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.ui.zk.DateRangePicker;
import org.carewebframework.ui.zk.DateTimebox;
import org.carewebframework.ui.zk.PopupDialog;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.vista.api.domain.EncounterUtil;
import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.ui.context.location.LocationSelection;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * Encounter selection controller. Supports selecting an existing encounter from inpatient or
 * outpatient lists or creating an ad hoc encounter.
 */
public class EncounterSelection extends FrameworkController implements PatientContext.IPatientContextEvent {
    
    private static final long serialVersionUID = 1L;
    
    public static enum EncounterFlag {
        NOT_LOCKED, FORCE, VALIDATE_ONLY, PROVIDER;
        
        /**
         * Returns a set of flags
         *
         * @param flags The encounter flags.
         * @return Set of encounter flags.
         */
        public static Set<EncounterFlag> flags(EncounterFlag... flags) {
            return flags == null || flags.length == 0 ? EnumSet.noneOf(EncounterFlag.class) : EnumSet.copyOf(Arrays
                    .asList(flags));
        }
    };
    
    private Listbox lstInpatient;
    
    private Listbox lstOutpatient;
    
    private Listbox lstLocation;
    
    private Textbox txtLocation;
    
    private Combobox cboServiceCategory;
    
    private DateTimebox datEncounter;
    
    private Checkbox chkForceCreate;
    
    private Tabbox tabbox;
    
    private Tab tabInpatient;
    
    private Tab tabOutpatient;
    
    private Tab tabNew;
    
    private Button btnOK;
    
    private DateRangePicker rngDateRange;
    
    private BrokerSession broker;
    
    private Patient patient;
    
    private Include incInpatient;
    
    private Include incOutpatient;
    
    private Include incNew;
    
    private Set<EncounterFlag> flags;
    
    private final EncounterRenderer encounterRenderer = new EncounterRenderer();
    
    /**
     * Displays the encounter selection dialog.
     *
     * @param flags The encounter flags.
     */
    public static void execute(EncounterFlag... flags) {
        String resource = Constants.RESOURCE_PREFIX + "encounterSelection.zul";
        Window dlg = (Window) FrameworkUtil.getAttribute(resource);
        
        if (dlg == null || dlg.getPage() == null) {
            dlg = PopupDialog.popup(resource, true, true, false);
            FrameworkUtil.setAttribute(resource, dlg);
        }
        
        try {
            EncounterSelection sel = (EncounterSelection) FrameworkController.getController(dlg);
            sel.setEncounterFlags(EncounterFlag.flags(flags));
            dlg.doModal();
        } catch (Exception e) {
            FrameworkUtil.setAttribute(resource, null);
            throw new RuntimeException(e);
        }
    }
    
    public static boolean validEncounter() {
        return validEncounter(EncounterContext.getActiveEncounter());
    }
    
    public static boolean validEncounter(Encounter encounter) {
        return encounter != null && EncounterUtil.isPrepared(encounter);
    }
    
    public static boolean ensureEncounter() {
        return ensureEncounter(null);
    }
    
    public static boolean ensureEncounter(Set<EncounterFlag> flags) {
        Encounter encounter = EncounterContext.getActiveEncounter();
        boolean isValid = validEncounter(encounter);
        
        if (isValid && EncounterUtil.isLocked(encounter) && hasFlag(flags, EncounterFlag.NOT_LOCKED)) {
            return false;
        }
        
        if (isValid && VistAUtil.parseIEN(encounter) == 0 && hasFlag(flags, EncounterFlag.FORCE)) {
            return EncounterUtil.forceCreate(encounter);
        }
        
        if (isValid || hasFlag(flags, EncounterFlag.VALIDATE_ONLY)) {
            return isValid;
        }
        
        execute();
        return validEncounter();
    }
    
    public static String validEncounter(Encounter encounter, Set<EncounterFlag> flags) {
        if (EncounterUtil.isLocked(encounter) && hasFlag(flags, EncounterFlag.NOT_LOCKED)) {
            return Constants.TX_NO_LCK;
        }
        
        StringBuilder sb = new StringBuilder();
        
        if (encounter.getLocation() == null) {
            appendItem(sb, Constants.TX_NO_LOC);
        }
        
        if (encounter.getType() == null) {
            appendItem(sb, Constants.TX_NO_CAT);
        }
        
        if (encounter.getPeriod() == null) {
            appendItem(sb, Constants.TX_NO_DAT);
        }
        
        if (EncounterUtil.getCurrentProvider(encounter) == null) {
            appendItem(sb, Constants.TX_NO_PRV);
        }
        
        if (EncounterUtil.getPrimaryProvider(encounter) == null && !EncounterUtil.isLocked(encounter)) {
            appendItem(sb, Constants.TX_NO_PRI);
        }
        
        if (sb.length() > 0) {
            return Constants.TX_MISSING + sb.toString();
        }
        
        if (hasFlag(flags, EncounterFlag.FORCE) && VistAUtil.parseIEN(encounter) <= 0
                && DateUtil.stripTime(encounter.getPeriod().getStart().getValue()).after(DateUtil.today())) {
            return Constants.TX_NO_FUT;
        }
        
        if (hasFlag(flags, EncounterFlag.PROVIDER)
                && !VistAUtil.getBrokerSession().callRPCBool("RGCWFUSR HASKEYS", "PROVIDER",
                    EncounterUtil.getEncounterProvider(encounter).getId().getIdPart())) {
            return Constants.TX_NO_KEY;
        }
        
        if (hasFlag(flags, EncounterFlag.FORCE) && VistAUtil.parseIEN(encounter) == 0
                && !EncounterUtil.forceCreate(encounter)) {
            return "Failed to create the visit.";
        }
        
        return null;
    }
    
    private static void appendItem(StringBuilder sb, String item) {
        sb.append("   ").append(item).append('\n');
    }
    
    private static boolean hasFlag(Set<EncounterFlag> flags, EncounterFlag flag) {
        return flags == null ? false : flags.contains(flag);
    }
    
    /**
     * Wire variables and events.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lstInpatient.setItemRenderer(encounterRenderer);
        lstOutpatient.setItemRenderer(encounterRenderer);
        rngDateRange.getItemAtIndex(0).setLabel("Default Date Range");
        rngDateRange.setSelectedIndex(0);
        broker = VistAUtil.getBrokerSession();
        setProviderSelectionDialog(tabInpatient, incInpatient);
        setProviderSelectionDialog(tabOutpatient, incOutpatient);
        setProviderSelectionDialog(tabNew, incNew);
        
        for (CodeableConceptDt cat : EncounterUtil.getServiceCategories()) {
            CodingDt coding = cat.getCodingFirstRep();
            Comboitem item = cboServiceCategory.appendItem(coding.getDisplay().getValue());
            item.setValue(coding.getCode().getValue());
            item.setTooltiptext(cat.getText().getValue());
        }
        
        List<String> data = broker.callRPCList("RGCWENCX CLINLOC", null, "", 1, 9999);
        
        for (String itm : data) {
            String[] pcs = StrUtil.split(itm, StrUtil.U, 3);
            Listitem item = lstLocation.appendItem(pcs[1], pcs[0]);
            item.setAttribute("sc", pcs[2]);
            item.addForward(Events.ON_DOUBLE_CLICK, btnOK, Events.ON_CLICK);
        }
        
        flagsChanged();
        committed();
    }
    
    protected void setEncounterFlags(Set<EncounterFlag> flags) {
        this.flags = flags;
        flagsChanged();
    }
    
    private void flagsChanged() {
        if (chkForceCreate != null) {
            boolean forceVisit = hasFlag(flags, EncounterFlag.FORCE);
            chkForceCreate.setChecked(forceVisit);
            chkForceCreate.setDisabled(forceVisit);
        }
    }
    
    public void onSelect$tabbox() {
        rngDateRange.setVisible(tabbox.getSelectedTab() == tabOutpatient);
    }
    
    public void onSelectRange$rngDateRange() {
        initOutpatient();
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
    }
    
    public void onSelect$lstInpatient() {
        loadProviders(getEncounter(lstInpatient));
    }
    
    public void onSelect$lstOutpatient() {
        loadProviders(getEncounter(lstOutpatient));
    }
    
    public void onClick$btnLocation() throws Exception {
        LocationSelection.locationLookup(txtLocation.getValue(), lstLocation, LocationContext.getActiveLocation());
    }
    
    public void onClick$btnCancel() {
        close();
    }
    
    /**
     * Change the encounter context to the selected encounter and close the dialog.
     *
     * @throws ClassNotFoundException If class not found.
     */
    public void onClick$btnOK() throws ClassNotFoundException {
        Encounter encounter = null;
        Tab activeTab = tabbox.getSelectedTab();
        ProviderSelection providerSelection = getProviderSelectionDialog(activeTab);
        
        if (activeTab == tabInpatient) { // Inpatient encounter
            encounter = getEncounter(lstInpatient);
        } else if (activeTab == tabOutpatient) { // Outpatient encounter
            encounter = getEncounter(lstOutpatient);
        } else { // Ad hoc encounter
            Listitem item = lstLocation.getSelectedItem();
            String locid = item == null ? null : (String) item.getValue();
            Location location = locid != null ? DomainFactoryRegistry.fetchObject(Location.class, locid) : null;
            Comboitem cboitem = cboServiceCategory.getSelectedItem();
            Date date = datEncounter.getDate();
            encounter = EncounterUtil.create(date, location, (String) cboitem.getValue());
            
            if (chkForceCreate.isChecked()) {
                flags.add(EncounterFlag.FORCE);
            } else {
                flags.remove(EncounterFlag.FORCE);
            }
        }
        
        if (encounter != null) {
            providerSelection.updateCurrentProvider();
            //encounter.setEncounterProvider(providerSelection.getEncounterProvider());
            String s = validEncounter(encounter, flags);
            
            if (s != null) {
                PromptDialog.showWarning(s);
                return;
            }
            EncounterContext.changeEncounter(encounter);
            close();
        }
    }
    
    private Encounter getEncounter(Listbox lb) {
        Listitem item = lb.getSelectedItem();
        return item == null ? null : (Encounter) item.getValue();
    }
    
    /**
     * Close the main dialog.
     */
    private void close() {
        root.setVisible(false);
    }
    
    /**
     * Initialize the dialog. Performs a query to return all existing encounters with the set time
     * window and populates the inpatient/outpatient lists from this.
     */
    private void init() {
        boolean hasInpatientEncounter = initInpatient();
        boolean hasOutpatientEncounter = initOutpatient();
        initNewEncounter();
        Tab activeTab = hasOutpatientEncounter ? tabOutpatient : hasInpatientEncounter ? tabInpatient : tabNew;
        activeTab.setSelected(true);
        onSelect$tabbox();
    }
    
    private ProviderSelection getProviderSelectionDialog(Tab tab) {
        return (ProviderSelection) tab.getAttribute("psd");
    }
    
    private void setProviderSelectionDialog(Tab tab, Include inc) {
        Component dlg = inc.getFirstChild();
        tab.setAttribute("psd", FrameworkController.getController(dlg));
    }
    
    private void loadProviders(Encounter encounter) {
        getProviderSelectionDialog(tabbox.getSelectedTab()).loadProviders(encounter);
    }
    
    private void populateListbox(Listbox lb, List<String> data) {
        lb.setModel((ListModel<?>) null);
        lb.setModel(new ListModelList<String>(data));
    }
    
    private boolean initOutpatient() {
        List<String> data = broker.callRPCList("RGCWENCX VISITLST", null, patient.getId().getIdPart(),
            rngDateRange.getStartDate(), rngDateRange.getEndDate());
        populateListbox(lstOutpatient, data);
        return data.size() > 0;
    }
    
    private boolean initInpatient() {
        List<String> data = broker.callRPCList("RGCWENCX ADMITLST", null, patient.getId().getIdPart());
        populateListbox(lstInpatient, data);
        return data.size() > 0;
    }
    
    private void initNewEncounter() {
        datEncounter.setDate(new Date());
        lstLocation.setSelectedItem(null);
        cboServiceCategory.setSelectedItem(null);
        loadProviders(new Encounter());
    }
    
    @Override
    public void canceled() {
    }
    
    @Override
    public void committed() {
        patient = PatientContext.getActivePatient();
        init();
    }
    
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
}
