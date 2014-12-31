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

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu.composite.CodingDt;
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.Encounter;
import ca.uhn.fhir.model.dstu.resource.Encounter.Participant;
import ca.uhn.fhir.model.dstu.resource.Practitioner;

/**
 * Abstract base class for encounter-associated domain objects.
 */
public class EncounterProvider extends EncounterRelated {
    
    private static final CodeableConceptDt primaryType = new CodeableConceptDt();
    
    static {
        primaryType.setText("Primary");
        CodingDt coding = primaryType.addCoding();
        coding.setCode("P");
    }
    
    private Practitioner currentProvider;
    
    private Practitioner primaryProvider;
    
    private final List<Participant> participants;
    
    public EncounterProvider() {
        this(new Encounter());
    }
    
    public EncounterProvider(Encounter encounter) {
        super(encounter);
        participants = encounter.getParticipant();
        currentProvider = null;
        primaryProvider = getPractitioner(findPrimary());
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
        if (provider == null) {
            return false;
        }
        
        boolean result = find(provider, false) == null;
        
        if (result) {
            Participant participant = new Participant();
            ResourceReferenceDt resource = new ResourceReferenceDt();
            resource.setReference(provider.getId());
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
    
    public Participant find(Practitioner provider, boolean create) {
        if (provider == null) {
            return null;
        }
        
        for (Participant participant : participants) {
            if (participant.getIndividual().getReference().equals(provider.getId())) {
                return participant;
            }
        }
        
        if (create) {
            Participant participant = new Participant();
            ResourceReferenceDt resource = new ResourceReferenceDt();
            resource.setReference(provider.getId());
            resource.setDisplay(provider.getName().toString());
            participant.setIndividual(resource);
            return participant;
        }
        
        return null;
    }
    
    public Participant findPrimary() {
        for (Participant participant : participants) {
            if (participant.getType().contains(primaryType)) {
                return participant;
            }
        }
        
        return null;
    }
    
    public Practitioner getPractitioner(Participant participant) {
        if (participant == null) {
            return null;
        }
        
        ResourceReferenceDt resource = participant.getIndividual();
        IResource ele = resource.getResource();
        return ele instanceof Practitioner ? (Practitioner) ele : null;
    }
    
    public List<Practitioner> getProviders() {
        List<Practitioner> list = new ArrayList<Practitioner>();
        
        for (Participant participant : participants) {
            Practitioner practitioner = getPractitioner(participant);
            
            if (practitioner != null) {
                list.add(practitioner);
            }
        }
        return list;
    }
}
