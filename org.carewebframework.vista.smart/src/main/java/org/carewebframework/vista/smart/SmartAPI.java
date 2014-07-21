/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.smart;

import java.util.List;
import java.util.Map;

import org.carewebframework.cal.api.context.PatientContext;
import org.carewebframework.common.StrUtil;
import org.carewebframework.fhir.model.resource.Patient;
import org.carewebframework.smart.SmartAPIBase;
import org.carewebframework.vista.api.util.VistAUtil;

/**
 * Adapter for VISTA SMART CONTAINER.
 */
public class SmartAPI extends SmartAPIBase {
    
    private final String ztyp;
    
    /**
     * API entry point. If a record id is specified, verifies that it is the same as the currently
     * selected patient.
     *
     * @param params
     * @return
     */
    public static boolean isValid(Map<String, String> params) {
        String patientId = params.get("record_id");
        
        if (patientId != null) {
            Patient patient = PatientContext.getActivePatient();
            
            if (!patientId.equals(patient.getDomainId())) {
                return false;
            }
        }
        
        return true;
    }
    
    public SmartAPI(String pattern, String capability, String ztyp) {
        super(pattern, ContentType.RDF, capability);
        this.ztyp = ztyp;
    }
    
    /**
     * Validate the request.
     *
     * @param params The associated request parameters.
     * @return True if the request is valid.
     */
    @Override
    public boolean validateRequest(Map<String, String> params) {
        return isValid(params);
    }
    
    @Override
    public Object handleAPI(Map<String, String> params) {
        List<String> data = VistAUtil.getBrokerSession().callRPCList("RGCWSMRT GET", null, params.get("record_id"), ztyp,
            "rdf");
        return StrUtil.fromList(data);
    }
}
