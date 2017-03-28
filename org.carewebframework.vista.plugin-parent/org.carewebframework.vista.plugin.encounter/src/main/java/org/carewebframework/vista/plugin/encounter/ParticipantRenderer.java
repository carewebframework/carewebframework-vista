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

import org.carewebframework.ui.zk.AbstractListitemRenderer;
import org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;

import ca.uhn.fhir.model.api.IResource;

public class ParticipantRenderer extends AbstractListitemRenderer<Object, Object> {

    private EncounterParticipantComponent primaryParticipant;

    @Override
    public void renderItem(Listitem item, Object data) {
        EncounterParticipantComponent participant;

        if (data instanceof EncounterParticipantComponent) {
            participant = (EncounterParticipantComponent) data;
        } else if (data instanceof IResource) {
            participant = new EncounterParticipantComponent();
            participant.setIndividual(new Reference((IAnyResource) data));
        } else {
            return;
        }

        item.setValue(participant);
        createCell(item, FhirUtil.formatName(EncounterUtil.getName(participant)));
        item.setSclass(isPrimary(participant) ? Constants.SCLASS_PRIMARY : null);
        item.addForward(Events.ON_DOUBLE_CLICK, item.getListbox(), null);
    }

    private boolean isPrimary(EncounterParticipantComponent participant) {
        return primaryParticipant != null && primaryParticipant.equals(participant);
    }

    public EncounterParticipantComponent getPrimaryParticipant() {
        return primaryParticipant;
    }

    public void setPrimaryParticipant(EncounterParticipantComponent primaryParticipant) {
        this.primaryParticipant = primaryParticipant;
    }

}
