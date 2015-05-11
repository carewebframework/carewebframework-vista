/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.encounter;

import java.util.Date;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Participant;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.resource.Patient;

import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.cal.api.ClientUtil;
import org.carewebframework.cal.api.encounter.EncounterParticipantContext;
import org.carewebframework.cal.api.patient.PatientContext;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.vista.mbroker.FMDate;

/**
 * Encounter-related utility functions.
 */
public class EncounterUtil extends org.carewebframework.cal.api.encounter.EncounterUtil {
    
    private static final String VSTR_DELIM = ";";
    
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
        
        Participant participant = EncounterParticipantContext.getActiveParticipant();
        String partId = participant == null ? null : participant.getElementSpecificId();
        String s = VistAUtil.getBrokerSession().callRPC("RGCWENCX FETCH", patient.getId().getIdPart(), encode(encounter),
            partId, true);
        String id = StrUtil.piece(s, StrUtil.U, 6);
        
        if (!VistAUtil.validateIEN(id)) {
            return false;
        }
        
        encounter.setId(id);
        return true;
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
        String[] pcs = StrUtil.split(vstr, VSTR_DELIM, 4);
        long encIEN = NumberUtils.toLong(pcs[3]);
        
        if (encIEN > 0) {
            return DomainFactoryRegistry.fetchObject(Encounter.class, pcs[3]);
        }
        
        long locIEN = NumberUtils.toLong(pcs[0]);
        Location location = locIEN == 0 ? null : DomainFactoryRegistry.fetchObject(Location.class, pcs[0]);
        Date date = FMDate.fromString(pcs[1]);
        return create(PatientContext.getActivePatient(), date, location, pcs[2]);
    }
    
    public static String encode(Encounter encounter) {
        Location location = ClientUtil.getResource(encounter.getLocationFirstRep().getLocation(), Location.class);
        String locIEN = location.isEmpty() ? "" : location.getId().getIdPart();
        Date date = encounter.getPeriod().getStart();
        String sc = getServiceCategory(encounter);
        String ien = encounter.getId().isEmpty() ? "" : encounter.getId().getIdPart();
        return locIEN + VSTR_DELIM + new FMDate(date).getFMDate() + VSTR_DELIM + sc + VSTR_DELIM + ien;
    }
    
    /**
     * Enforces static class.
     */
    protected EncounterUtil() {
    }
    
}
