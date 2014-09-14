/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.query;

import java.util.Date;
import java.util.List;

import ca.uhn.fhir.model.dstu.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.primitive.StringDt;

import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.cal.api.query.patient.IPatientSearch;
import org.carewebframework.cal.api.query.patient.PatientSearchCriteria;
import org.carewebframework.cal.api.query.patient.PatientSearchException;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.FMDate;

/**
 * Patient search services.
 */
public class PatientSearchEngine implements IPatientSearch {
    
    /**
     * Perform search, using the specified criteria.
     *
     * @param criteria The search criteria.
     * @return A list of patients matching the specified search criteria. The return value will be
     *         null if no search criteria are provided or the search exceeds the maximum allowable
     *         matches and the user chooses to cancel the search.
     */
    @Override
    public List<Patient> search(PatientSearchCriteria criteria) {
        BrokerSession broker = VistAUtil.getBrokerSession();
        
        HumanNameDt name = criteria.getName();
        String familyName = name == null ? null : concatNames(name.getFamily());
        String givenName = name == null ? null : concatNames(name.getGiven());
        IdentifierDt mrn = criteria.getMRN();
        IdentifierDt ssn = criteria.getSSN();
        String gender = criteria.getGender();
        Date date = criteria.getBirth();
        String dob = date == null ? "" : new FMDate(date).getFMDate();
        String dfn = criteria.getId();
        List<String> hits = broker.callRPCList("RGCWPTPS SEARCH", null, 200, familyName, givenName, mrn, ssn, dfn, gender,
            dob);
        
        if (hits == null || hits.size() == 0) {
            return null;
        }
        
        String[] ids = new String[hits.size()];
        int i = 0;
        
        for (String hit : hits) {
            String[] pcs = StrUtil.split(hit, StrUtil.U, 2);
            String patientId = pcs[0];
            
            if (!VistAUtil.validateIEN(patientId)) {
                throw new PatientSearchException(pcs[1]);
            }
            
            ids[i++] = patientId;
            
        }
        
        return DomainFactoryRegistry.fetchObjects(Patient.class, ids);
    }
    
    /**
     * Concatenate all names in list, separating with spaces.
     * 
     * @param names List of names.
     * @return Concatenated list.
     */
    private String concatNames(List<StringDt> names) {
        StringBuilder sb = null;
        
        for (StringDt name : names) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(' ');
            }
            
            sb.append(name.getValue());
        }
        
        return sb == null ? null : sb.toString();
    }
}
