/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.smart;

import java.util.List;
import java.util.Map;

import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.api.util.VistAUtil;

/**
 * Adapter for VISTA SMART CONTAINER.
 */
public class SmartAPI extends org.carewebframework.cal.api.smart.SmartAPIBase {
    
    private final String ztyp;
    
    public SmartAPI(String pattern, String capability, String ztyp) {
        super(pattern, capability);
        this.ztyp = ztyp;
    }
    
    @Override
    public Object handleAPI(Map<String, String> params) {
        List<String> data = VistAUtil.getBrokerSession().callRPCList("RGCWSMRT GET", null, params.get("record_id"), ztyp,
            "rdf");
        
        if (data.isEmpty()) {
            data.add("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"/>");
        }
        
        return StrUtil.fromList(data);
    }
}
