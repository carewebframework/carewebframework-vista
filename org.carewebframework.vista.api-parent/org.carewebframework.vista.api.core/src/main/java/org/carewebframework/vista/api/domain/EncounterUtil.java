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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import ca.uhn.fhir.model.dstu.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu.composite.CodingDt;
import ca.uhn.fhir.model.dstu.composite.PeriodDt;
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.Encounter;
import ca.uhn.fhir.model.dstu.resource.Location;
import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.dstu.resource.Practitioner;
import ca.uhn.fhir.model.dstu.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.primitive.BoundCodeDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;

import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.cal.api.context.PatientContext;
import org.carewebframework.common.StrUtil;
import org.carewebframework.fhir.common.FhirUtil;
import org.carewebframework.vista.api.property.Property;
import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.vista.mbroker.FMDate;

/**
 * Encounter-related utility functions.
 */
public class EncounterUtil {
    
    private static volatile Map<String, CodeableConceptDt> serviceCategories;
    
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
        
        String s = VistAUtil.getBrokerSession().callRPC("RGCWENCX FETCH", patient.getId().getIdPart(), encode(encounter),
            getCurrentProvider(encounter).getId().getIdPart(), true);
        String id = StrUtil.piece(s, StrUtil.U, 6);
        
        if (!VistAUtil.validateIEN(id)) {
            return false;
        }
        
        encounter.setId(id);
        return true;
    }
    
    public static Encounter create(Date date, Location location, String sc) {
        Encounter encounter = new Encounter();
        PeriodDt period = new PeriodDt();
        period.setStart(new DateTimeDt(date));
        encounter.setPeriod(period);
        ResourceReferenceDt loc = new ResourceReferenceDt();
        loc.setResource(location);
        Encounter.Location encloc = encounter.addLocation();
        encloc.setPeriod(period);
        encloc.setLocation(loc);
        CodeableConceptDt type = encounter.addType();
        CodeableConceptDt cat = getServiceCategory(sc);
        type.setText(cat.getText());
        type.setCoding(cat.getCoding());
        return encounter;
    }
    
    public static CodeableConceptDt createServiceCategory(String sc, String shortDx, String longDx) {
        CodeableConceptDt cpt = new CodeableConceptDt();
        cpt.setText(longDx);
        CodingDt coding = new CodingDt();
        coding.setCode(sc);
        coding.setDisplay(shortDx);
        cpt.getCoding().add(coding);
        return cpt;
    }
    
    public static CodeableConceptDt getServiceCategory(String category) {
        initServiceCategories();
        
        if (category == null) {
            return null;
        }
        
        CodeableConceptDt cat = serviceCategories.get(category);
        
        if (cat == null) {
            cat = createServiceCategory(category, "Unknown", "Unknown service category");
        }
        
        return cat;
    }
    
    public static Collection<CodeableConceptDt> getServiceCategories() {
        initServiceCategories();
        return serviceCategories.values();
    }
    
    private static void initServiceCategories() {
        if (serviceCategories == null) {
            loadServiceCategories();
        }
    }
    
    private static synchronized void loadServiceCategories() {
        if (serviceCategories == null) {
            Map<String, CodeableConceptDt> map = new LinkedHashMap<String, CodeableConceptDt>();
            Property property = new Property("RGCWENCX VISIT TYPES", "*", null, "I");
            
            for (String sc : property.getValues()) {
                String pcs[] = StrUtil.split(sc, "~", 3);
                map.put(pcs[0], createServiceCategory(pcs[0], pcs[1], pcs[2]));
            }
            
            serviceCategories = map;
        }
        
        return;
    }
    
    public static String getServiceCategory(Encounter encounter) {
        CodeableConceptDt cpt = encounter == null ? null : FhirUtil.getFirst(encounter.getType());
        CodingDt coding = cpt == null ? null : cpt.getCodingFirstRep();
        return coding == null ? null : coding.getCode().getValue();
    }
    
    /**
     * Decode encounter from visit string.
     * 
     * @param vstr Format is:
     *            <p>
     *            location ien^visit date^category^visit ien
     * @return The decoded encounter.
     */
    public static Encounter decode(String vstr) {
        String[] pcs = StrUtil.split(vstr, ";", 4);
        long encIEN = NumberUtils.toLong(pcs[3]);
        
        if (encIEN > 0) {
            return DomainFactoryRegistry.fetchObject(Encounter.class, pcs[3]);
        }
        
        long locIEN = NumberUtils.toLong(pcs[0]);
        Location location = locIEN == 0 ? null : DomainFactoryRegistry.fetchObject(Location.class, pcs[0]);
        Date date = FMDate.fromString(pcs[1]);
        return create(date, location, pcs[2]);
    }
    
    public static String encode(Encounter encounter) {
        Encounter.Location location = encounter.getLocationFirstRep();
        String locIEN = location == null ? "" : location.getLocation().getElementSpecificId();
        DateTimeDt date = encounter.getPeriod().getStart();
        String sc = getServiceCategory(encounter);
        String ien = encounter.getId().getIdPart();
        return locIEN + StrUtil.U + new FMDate(date.getValue()).getFMDate() + StrUtil.U + sc + StrUtil.U + ien;
    }
    
    public static boolean isLocked(Encounter encounter) {
        BoundCodeDt<EncounterStateEnum> status = encounter.getStatus();
        return status != null && status.getValueAsEnum() == EncounterStateEnum.FINISHED;
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
