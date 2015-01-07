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
import ca.uhn.fhir.model.dstu.resource.Location;

import org.carewebframework.cal.api.ClientUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.zk.AbstractListitemRenderer;
import org.carewebframework.vista.api.encounter.EncounterUtil;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Span;

public class EncounterRenderer extends AbstractListitemRenderer<Object, Object> {
    
    @Override
    public void renderItem(Listitem item, Object data) {
        Encounter encounter = data instanceof Encounter ? (Encounter) data : parse((String) data);
        item.setValue(encounter);
        item.addForward(Events.ON_DOUBLE_CLICK, item.getListbox(), null);
        Span span = new Span();
        span.setSclass(EncounterUtil.isLocked(encounter) ? Constants.SCLASS_LOCKED : null);
        createCell(item, span);
        Location location = ClientUtil.getResource(encounter.getLocationFirstRep().getLocation(), Location.class);
        createCell(item, location == null ? null : location.getName());
        createCell(item, encounter.getPeriod().getStart().getValue());
        createCell(item, encounter.getTypeFirstRep().getCodingFirstRep().getDisplay());
    }
    
    private Encounter parse(String value) {
        return EncounterUtil.decode(StrUtil.piece(value, StrUtil.U));
    }
}
