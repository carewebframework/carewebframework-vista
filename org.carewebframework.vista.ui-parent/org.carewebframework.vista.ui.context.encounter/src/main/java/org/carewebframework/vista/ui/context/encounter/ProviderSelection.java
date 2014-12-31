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

import org.carewebframework.api.context.UserContext;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.ui.zk.ListUtil;
import org.carewebframework.vista.api.domain.EncounterProvider;
import org.carewebframework.vista.api.domain.ProviderUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class ProviderSelection extends FrameworkController {
    
    private static final long serialVersionUID = 1L;
    
    private Textbox edtProvider;
    
    private Listbox lstAllProviders;
    
    private Listbox lstEncounterProviders;
    
    private boolean modified = false;
    
    private EncounterProvider encounterProvider;
    
    private final ProviderRenderer providerRenderer = new ProviderRenderer();
    
    private final ListModelList<Practitioner> modelProviders = new ListModelList<Practitioner>();
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        lstAllProviders.setItemRenderer(providerRenderer);
        lstAllProviders.setModel(modelProviders);
        lstEncounterProviders.setItemRenderer(providerRenderer);
    }
    
    public EncounterProvider getEncounterProvider() {
        return encounterProvider;
    }
    
    public void updateCurrentProvider() {
        encounterProvider.setCurrentProvider(getSelectedProvider(lstEncounterProviders));
    }
    
    private Practitioner getSelectedProvider(Listbox lb) {
        Listitem item = lb.getSelectedItem();
        return item == null ? null : (Practitioner) item.getValue();
    }
    
    private void setPrimaryProvider(Practitioner provider) {
        encounterProvider.setPrimaryProvider(provider);
        refreshProviders();
        modified = true;
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public void loadProviders(Encounter encounter) {
        encounterProvider = new EncounterProvider(encounter);
        providerRenderer.setEncounterProvider(encounterProvider);
        modified = false;
        refreshProviders();
    }
    
    public void refreshProviders() {
        Practitioner provider = getSelectedProvider(lstEncounterProviders);
        ListModel<Practitioner> model = new ListModelList<Practitioner>(encounterProvider.getProviders());
        lstEncounterProviders.setModel((ListModel<?>) null);
        lstEncounterProviders.setModel(model);
        
        if (model.getSize() == 1) {
            lstEncounterProviders.setSelectedIndex(0);
        } else {
            selectFirstProvider(provider, UserContext.getActiveUser(), encounterProvider.getCurrentProvider(),
                encounterProvider.getPrimaryProvider());
        }
        
    }
    
    private void selectFirstProvider(Object... providers) {
        for (Object provider : providers) {
            if (findProvider(provider)) {
                break;
            }
        }
    }
    
    private boolean findProvider(Object provider) {
        if (provider != null) {
            int i = ListUtil.findListboxData(lstEncounterProviders, provider);
            
            if (i >= 0) {
                lstEncounterProviders.setSelectedIndex(i);
                return true;
            }
        }
        
        return false;
    }
    
    public void onClick$btnProvider() {
        ProviderUtil.search(edtProvider.getText(), 40, modelProviders);
    }
    
    public void onClick$btnPrimary() {
        Practitioner provider = getSelectedProvider(lstEncounterProviders);
        
        if (provider != null) {
            setPrimaryProvider(provider);
        }
    }
    
    public void onClick$btnProviderAdd() {
        Practitioner provider = getSelectedProvider(lstAllProviders);
        
        if (encounterProvider.add(provider)) {
            refreshProviders();
            modified = true;
        }
    }
    
    public void onClick$btnProviderRemove() {
        Practitioner provider = getSelectedProvider(lstEncounterProviders);
        
        if (encounterProvider.remove(provider)) {
            refreshProviders();
            modified = true;
        }
    }
    
}
