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

import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.common.StrUtil;
import org.carewebframework.fhir.model.resource.Practitioner;
import org.carewebframework.fhir.model.type.HumanName;
import org.carewebframework.vista.api.util.VistAUtil;

/**
 * Practitioner-related utilities.
 */
public class ProviderUtil {
    
    public static Practitioner fetchProvider(String value) {
        String id = StrUtil.piece(value, StrUtil.U);
        return DomainFactoryRegistry.fetchObject(Practitioner.class, id);
    }
    
    public static List<Practitioner> search(String text, int maxHits, List<Practitioner> hits) {
        if (hits == null) {
            hits = new ArrayList<Practitioner>();
        } else {
            hits.clear();
        }
        
        text = text == null ? null : text.trim().toUpperCase();
        
        if (text == null || text.isEmpty()) {
            return hits;
        }
        
        List<String> list = VistAUtil.getBrokerSession().callRPCList("RGCWFUSR LOOKUP", null, text, 1, "@RGCWENCX PROVIDER",
            maxHits);
        
        for (String prv : list) {
            String[] pcs = prv.split("\\^");
            
            if (pcs[1].toUpperCase().startsWith(text)) {
                Practitioner provider = new Practitioner();
                provider.setDomainId(pcs[0]);
                provider.setName(new HumanName(pcs[1]));
                hits.add(provider);
            } else {
                break;
            }
        }
        
        return hits;
    }
    
    /**
     * Enforces static class.
     */
    private ProviderUtil() {
    };
}
