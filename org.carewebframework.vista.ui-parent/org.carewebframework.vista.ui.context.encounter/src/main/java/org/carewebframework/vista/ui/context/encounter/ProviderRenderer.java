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

import ca.uhn.fhir.model.dstu.resource.Practitioner;

import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.zk.AbstractListitemRenderer;
import org.carewebframework.vista.api.domain.EncounterProvider;
import org.carewebframework.vista.api.domain.ProviderUtil;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;

public class ProviderRenderer extends AbstractListitemRenderer<Object, Object> {
    
    private EncounterProvider encounterProvider;
    
    @Override
    public void renderItem(Listitem item, Object data) {
        Practitioner provider = data instanceof Practitioner ? (Practitioner) data : ProviderUtil.fetchProvider(StrUtil
                .piece((String) data, StrUtil.U));
        item.setValue(provider);
        createCell(item, provider.getName());
        Practitioner primaryProvider = encounterProvider.getPrimaryProvider();
        item.setSclass(primaryProvider == null || !provider.equals(primaryProvider) ? null : Constants.SCLASS_PRIMARY);
        item.addForward(Events.ON_DOUBLE_CLICK, item.getListbox(), null);
    }
    
    public void setEncounterProvider(EncounterProvider encounterProvider) {
        this.encounterProvider = encounterProvider;
    }
    
}
