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

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.Encounter.Participant;

import org.carewebframework.fhir.common.FhirUtil;
import org.carewebframework.ui.zk.AbstractListitemRenderer;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;

public class ParticipantRenderer extends AbstractListitemRenderer<Object, Object> {
    
    private Participant primaryParticipant;
    
    @Override
    public void renderItem(Listitem item, Object data) {
        Participant participant;
        
        if (data instanceof Participant) {
            participant = (Participant) data;
        } else if (data instanceof IResource) {
            participant = new Participant();
            participant.setIndividual(new ResourceReferenceDt((IResource) data));
        } else {
            return;
        }
        
        item.setValue(participant);
        createCell(item, FhirUtil.formatName(EncounterUtil.getName(participant)));
        item.setSclass(isPrimary(participant) ? Constants.SCLASS_PRIMARY : null);
        item.addForward(Events.ON_DOUBLE_CLICK, item.getListbox(), null);
    }
    
    private boolean isPrimary(Participant participant) {
        return primaryParticipant != null && primaryParticipant.equals(participant);
    }
    
    public Participant getPrimaryParticipant() {
        return primaryParticipant;
    }
    
    public void setPrimaryParticipant(Participant primaryParticipant) {
        this.primaryParticipant = primaryParticipant;
    }
    
}
