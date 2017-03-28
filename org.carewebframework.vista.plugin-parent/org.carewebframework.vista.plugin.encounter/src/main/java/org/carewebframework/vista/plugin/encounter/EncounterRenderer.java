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

import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.zk.AbstractListitemRenderer;
import org.carewebframework.vista.api.encounter.EncounterUtil;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hspconsortium.cwf.api.ClientUtil;
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
        createCell(item, encounter.getPeriod().getStart());
        createCell(item, encounter.getTypeFirstRep().getCodingFirstRep().getDisplay());
    }

    private Encounter parse(String value) {
        return EncounterUtil.decode(StrUtil.piece(value, StrUtil.U));
    }
}
