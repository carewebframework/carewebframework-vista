/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.domain;

import java.util.ArrayList;
import java.util.List;

import org.carewebframework.fhir.common.FhirUtil;
import org.carewebframework.fhir.model.core.Element;
import org.carewebframework.fhir.model.resource.Encounter;
import org.carewebframework.fhir.model.resource.Encounter_Participant;
import org.carewebframework.fhir.model.resource.Practitioner;
import org.carewebframework.fhir.model.type.CodeableConcept;
import org.carewebframework.fhir.model.type.Coding;
import org.carewebframework.fhir.model.type.Resource_;

/**
 * Abstract base class for encounter-associated domain objects.
 */
public class EncounterProvider extends EncounterRelated {
    
    private static final long serialVersionUID = 1L;
    
    private static final CodeableConcept primaryType = new CodeableConcept();
    
    static {
        primaryType.setTextSimple("Primary");
        Coding coding = new Coding();
        coding.setCodeSimple("P");
        primaryType.addCoding(coding);
    }
    
    private Practitioner currentProvider;
    
    private Practitioner primaryProvider;
    
    private final List<Encounter_Participant> participants;
    
    public EncounterProvider(Encounter encounter) {
        super(encounter);
        participants = encounter.getParticipant();
        currentProvider = null;
        primaryProvider = getPractitioner(findPrimary());
    }
    
    public void assign(EncounterProvider encounterProvider) {
        if (this == encounterProvider) {
            throw new IllegalArgumentException("Cannot assign to self.");
        }
        
        clear();
        FhirUtil.clone(encounterProvider.participants, participants);
        currentProvider = FhirUtil.clone(encounterProvider.currentProvider);
        primaryProvider = FhirUtil.clone(encounterProvider.primaryProvider);
    }
    
    public Practitioner getCurrentProvider() {
        return currentProvider;
    }
    
    public void setCurrentProvider(Practitioner provider) {
        add(provider);
        this.currentProvider = provider;
    }
    
    public Practitioner getPrimaryProvider() {
        return primaryProvider;
    }
    
    public void setPrimaryProvider(Practitioner provider) {
        add(provider);
        this.primaryProvider = provider;
    }
    
    public int size() {
        return participants.size();
    }
    
    public void clear() {
        participants.clear();
        currentProvider = null;
        primaryProvider = null;
    }
    
    public boolean add(Practitioner provider) {
        boolean result = find(provider, false) == null;
        
        if (result) {
            Encounter_Participant participant = new Encounter_Participant();
            Resource_ resource = new Resource_();
            resource.setReferenceSimple(provider.getDomainId());
            participant.setIndividual(resource);
        }
        
        return result;
    }
    
    public boolean remove(Practitioner provider) {
        boolean result = provider != null && participants.remove(provider);
        
        if (result) {
            if (provider.equals(primaryProvider)) {
                primaryProvider = null;
            }
            
            if (provider.equals(currentProvider)) {
                currentProvider = null;
            }
        }
        
        return result;
    }
    
    public Encounter_Participant find(Practitioner provider, boolean create) {
        for (Encounter_Participant participant : participants) {
            if (participant.getIndividual().getReference().equals(provider.getDomainId())) {
                return participant;
            }
        }
        
        if (create) {
            Encounter_Participant participant = new Encounter_Participant();
            Resource_ resource = new Resource_();
            resource.setReferenceSimple(provider.getDomainId());
            resource.setDisplaySimple(provider.getName().toString());
            participant.setIndividual(resource);
            return participant;
        }
        
        return null;
    }
    
    public Encounter_Participant findPrimary() {
        for (Encounter_Participant participant : participants) {
            if (participant.getType().contains(primaryType)) {
                return participant;
            }
        }
        
        return null;
    }
    
    public Practitioner getPractitioner(Encounter_Participant participant) {
        if (participant == null) {
            return null;
        }
        
        Resource_ resource = participant.getIndividual();
        Element ele = resource.getReferenceTarget();
        return ele instanceof Practitioner ? (Practitioner) ele : null;
    }
    
    public List<Practitioner> getProviders() {
        List<Practitioner> list = new ArrayList<Practitioner>();
        
        for (Encounter_Participant participant : participants) {
            Practitioner practitioner = getPractitioner(participant);
            
            if (practitioner != null) {
                list.add(practitioner);
            }
        }
        return list;
    }
}
