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

import java.util.Date;

import org.carewebframework.cal.api.context.PatientContext;
import org.carewebframework.common.StrUtil;
import org.carewebframework.fhir.common.FhirUtil;
import org.carewebframework.fhir.model.core.DateAndTime;
import org.carewebframework.fhir.model.resource.Encounter;
import org.carewebframework.fhir.model.resource.Encounter.EncounterStatus;
import org.carewebframework.fhir.model.resource.Encounter_Location;
import org.carewebframework.fhir.model.resource.Location;
import org.carewebframework.fhir.model.resource.Patient;
import org.carewebframework.fhir.model.resource.Practitioner;
import org.carewebframework.fhir.model.type.CodeableConcept;
import org.carewebframework.fhir.model.type.Coding;
import org.carewebframework.fhir.model.type.Period;
import org.carewebframework.fhir.model.type.Resource_;
import org.carewebframework.vista.api.util.VistAUtil;

/**
 * Encounter-related utility functions.
 */
public class EncounterUtil {
    
    /**
     * Returns the default encounter for the current institution for the specified patient. Search
     * is restricted to encounters belonging to the current institution, with care setting codes of
     * 'O', 'E', or 'I'. For inpatient encounters, the discharge date must be null and the admission
     * date must precede the current date (there are anomalous entries where the admission date is
     * in the future). For non-inpatient encounters, the admission date must fall on the same day as
     * the current date. If more than one encounter meets these criteria, further filtering is
     * applied. An encounter whose location matches the current location is selected preferentially.
     * Failing a match on location, non-inpatient encounters are given weight over inpatient
     * encounters. Failing all that, the first matching encounter is returned.
     *
     * @param patient Patient whose default encounter is sought.
     * @return The default encounter or null if one was not found.
     */
    public static Encounter getDefaultEncounter(Patient patient) {
        if (patient == null) {
            return null;
        }
        
        return null;
    }
    
    public static boolean forceCreate(Encounter encounter) {
        if (encounter == null || !isPrepared(encounter)) {
            return false;
        }
        
        if (VistAUtil.validateIEN(encounter)) {
            return true;
        }
        
        Patient patient = PatientContext.getActivePatient();
        
        if (patient == null) {
            return false;
        }
        
        String s = VistAUtil.getBrokerSession().callRPC("RGCWENCX FETCH", patient.getDomainId(), encode(encounter),
            getCurrentProvider(encounter).getDomainId(), true);
        String id = StrUtil.piece(s, StrUtil.U, 6);
        
        if (!VistAUtil.validateIEN(id)) {
            return false;
        }
        
        encounter.setDomainId(id);
        return true;
    }
    
    public static Encounter create(Date date, Location location, CodeableConcept sc) {
        Encounter encounter = new Encounter();
        Period period = new Period();
        period.setStartSimple(new DateAndTime(date));
        encounter.setPeriod(period);
        Resource_ loc = new Resource_();
        loc.setReferenceSimple(location.getDomainId());
        encounter.addLocation(new Encounter_Location(loc, period));
        encounter.addType(sc);
        return encounter;
    }
    
    public static CodeableConcept createServiceCategory(String sc, String shortDx, String longDx) {
        CodeableConcept cpt = new CodeableConcept();
        cpt.setTextSimple(longDx);
        Coding coding = new Coding();
        coding.setCodeSimple(sc);
        coding.setDisplaySimple(shortDx);
        cpt.addCoding(coding);
        return cpt;
    }
    
    public static String getServiceCategory(Encounter encounter) {
        CodeableConcept cpt = encounter == null ? null : FhirUtil.getFirst(encounter.getType());
        Coding coding = cpt == null ? null : FhirUtil.getFirst(cpt.getCoding());
        return coding == null ? null : coding.getCodeSimple();
    }
    
    public static Encounter decode(String piece) {
        // TODO Auto-generated method stub
        return null;
    };
    
    public static Encounter encode(Encounter encounter) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static boolean isLocked(Encounter encounter) {
        return encounter.getStatusSimple() == EncounterStatus.finished;
    }
    
    public static boolean isPrepared(Encounter encounter) {
        // TODO Auto-generated method stub
        return true;
    }
    
    public static Practitioner getEncounterProvider(Encounter encounter) {
        return null;
    }
    
    public static Practitioner getCurrentProvider(Encounter encounter) {
        return null;
    }
    
    public static Practitioner getPrimaryProvider(Encounter encounter) {
        return null;
    };
    
    /**
     * Enforces static class.
     */
    private EncounterUtil() {
    }
    
}
