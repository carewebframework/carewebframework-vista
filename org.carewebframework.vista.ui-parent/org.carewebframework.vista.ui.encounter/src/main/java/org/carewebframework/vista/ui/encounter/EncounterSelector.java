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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Participant;

import org.apache.commons.lang.ObjectUtils;

import org.carewebframework.api.context.UserContext;
import org.carewebframework.cal.api.encounter.EncounterContext;
import org.carewebframework.cal.api.encounter.EncounterParticipantContext;
import org.carewebframework.cal.api.encounter.EncounterSearch;
import org.carewebframework.cal.api.practitioner.PractitionerSearch;
import org.carewebframework.cal.api.practitioner.PractitionerSearchCriteria;
import org.carewebframework.fhir.common.FhirUtil;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.ui.zk.ListUtil;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.ui.zk.ZKUtil;
import org.carewebframework.vista.api.encounter.EncounterUtil;
import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.vista.mbroker.BrokerSession;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public abstract class EncounterSelector extends FrameworkController {
    
    /**
     * Used to prevent duplicates in participant model set and to sort alphabetically.
     */
    private static final Comparator<Participant> participantComparator = new Comparator<Participant>() {
        
        @Override
        public int compare(Participant p1, Participant p2) {
            if (p1.getIndividual().getReference().equals(p2.getIndividual().getReference())) {
                return 0;
            }
            
            String n1 = FhirUtil.formatName(EncounterUtil.getName(p1));
            String n2 = FhirUtil.formatName(EncounterUtil.getName(p2));
            int i = n1.compareToIgnoreCase(n2);
            return i == 0 ? 1 : i;
        }
        
    };
    
    private static final long serialVersionUID = 1L;
    
    private static final String PARTICIPANT_SELECTOR = Constants.RESOURCE_PREFIX + "participantSelector.zul";
    
    private Textbox edtParticipant;
    
    private Listbox lstAllParticipants;
    
    private Listbox lstEncounterParticipants;
    
    private boolean primaryModified;
    
    private boolean participantsModified;
    
    private final ParticipantRenderer encounterParticipantRenderer = new ParticipantRenderer();
    
    private final ListModelSet<Object> allParticipantsModel = new ListModelSet<Object>();
    
    private final ListModelSet<Participant> encounterParticipantsModel = new ListModelSet<Participant>(
            new TreeSet<Participant>(participantComparator), true);
    
    private Participant currentParticipant;
    
    protected BrokerSession broker;
    
    protected PractitionerSearch practitionerSearch;
    
    protected EncounterSearch encounterSearch;
    
    protected MainController mainController;
    
    protected abstract Encounter getEncounterInternal();
    
    protected abstract boolean isComplete();
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        ZKUtil.wireController(ZKUtil.loadZulPage(PARTICIPANT_SELECTOR, comp), this);
        broker = VistAUtil.getBrokerSession();
        lstAllParticipants.setItemRenderer(new ParticipantRenderer());
        lstAllParticipants.setModel(allParticipantsModel);
        lstEncounterParticipants.setItemRenderer(encounterParticipantRenderer);
        lstEncounterParticipants.setModel(encounterParticipantsModel);
    }
    
    protected boolean init(MainController mainController) {
        this.mainController = mainController;
        return true;
    }
    
    protected void activate(boolean activate) {
        
    }
    
    protected void statusChanged() {
        mainController.btnOK.setDisabled(!isComplete());
    }
    
    public Encounter getEncounter() {
        Encounter encounter = getEncounterInternal();
        
        if (encounter != null) {
            if (participantsModified) {
                List<Participant> participants = encounter.getParticipant();
                participants.clear();
                participants.addAll(encounterParticipantsModel);
            }
            
            if (primaryModified) {
                Participant participant = EncounterUtil.getPrimaryParticipant(encounter);
                
                if (participant != null) {
                    EncounterUtil.removeType(participant, EncounterUtil.primaryType);
                }
                
                participant = encounterParticipantRenderer.getPrimaryParticipant();
                
                if (participant != null) {
                    EncounterUtil.addType(participant, EncounterUtil.primaryType);
                }
            }
            
            EncounterContext.changeEncounter(encounter);
            
            if (encounter == EncounterContext.getActiveEncounter()) {
                EncounterParticipantContext.changeParticipant(getSelectedParticipant(lstEncounterParticipants));
            }
        }
        
        return encounter;
    }
    
    protected Encounter getSelectedEncounter(Listbox lb) {
        Listitem item = lb.getSelectedItem();
        return item == null ? null : (Encounter) item.getValue();
    }
    
    private Participant getSelectedParticipant(Listbox lb) {
        Listitem item = lb.getSelectedItem();
        return item == null ? null : (Participant) item.getValue();
    }
    
    public Participant getPrimaryParticipant() {
        return encounterParticipantRenderer.getPrimaryParticipant();
    }
    
    private void setPrimaryParticipant(Participant participant) {
        if (!ObjectUtils.equals(participant, getPrimaryParticipant())) {
            encounterParticipantRenderer.setPrimaryParticipant(participant);
            primaryModified = true;
            lstEncounterParticipants.setModel(encounterParticipantsModel);
        }
    }
    
    protected boolean populateListbox(Listbox lb, List<?> data) {
        lb.setModel((ListModel<?>) null);
        lb.setModel(new ListModelList<Object>(data));
        return data.size() > 0;
    }
    
    public void loadEncounterParticipants(Encounter encounter) {
        encounterParticipantsModel.clear();
        encounterParticipantsModel.addAll(encounter.getParticipant());
        currentParticipant = EncounterParticipantContext.getActiveParticipant();
        encounterParticipantRenderer.setPrimaryParticipant(EncounterUtil.getPrimaryParticipant(encounter));
        
        if (encounterParticipantsModel.getSize() == 1) {
            lstEncounterParticipants.setSelectedIndex(0);
        } else {
            selectFirstParticipant(UserContext.getActiveUser(), currentParticipant, getPrimaryParticipant());
        }
        
        participantsModified = false;
        primaryModified = false;
    }
    
    private void selectFirstParticipant(Object... participants) {
        for (Object participant : participants) {
            if (findParticipant(participant)) {
                break;
            }
        }
    }
    
    private boolean findParticipant(Object participant) {
        if (participant != null) {
            int i = ListUtil.findListboxData(lstEncounterParticipants, participant);
            
            if (i >= 0) {
                lstEncounterParticipants.setSelectedIndex(i);
                return true;
            }
        }
        
        return false;
    }
    
    public void onClick$btnParticipant() {
        String search = edtParticipant.getText().trim();
        
        if (!search.isEmpty()) {
            try {
                PractitionerSearchCriteria criteria = new PractitionerSearchCriteria(search);
                allParticipantsModel.clear();
                allParticipantsModel.addAll(practitionerSearch.search(criteria));
            } catch (Exception e) {
                PromptDialog.showError(e);
            }
        }
    }
    
    public void onClick$btnPrimary() {
        Participant participant = getSelectedParticipant(lstEncounterParticipants);
        
        if (participant != null) {
            setPrimaryParticipant(participant);
        }
    }
    
    public void onClick$btnParticipantAdd() {
        Participant participant = getSelectedParticipant(lstAllParticipants);
        
        if (encounterParticipantsModel.add(participant)) {
            participantsModified = true;
        }
        
        encounterParticipantsModel.setSelection(Collections.singleton(participant));
    }
    
    public void onClick$btnParticipantRemove() {
        Participant participant = getSelectedParticipant(lstEncounterParticipants);
        
        if (encounterParticipantsModel.remove(participant)) {
            participantsModified = true;
            
            if (participant == getPrimaryParticipant()) {
                encounterParticipantRenderer.setPrimaryParticipant(null);
            }
        }
    }
    
    public void setEncounterSearch(EncounterSearch encounterSearch) {
        this.encounterSearch = encounterSearch;
    }
    
    public void setPractitionerSearch(PractitionerSearch practitionerSearch) {
        this.practitionerSearch = practitionerSearch;
    }
    
}
