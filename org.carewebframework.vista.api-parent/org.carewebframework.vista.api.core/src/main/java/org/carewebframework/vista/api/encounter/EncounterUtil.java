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
package org.carewebframework.vista.api.encounter;

import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;
import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.vista.mbroker.FMDate;
import org.carewebframework.vista.mbroker.RPCParameter;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.api.ClientUtil;
import org.hspconsortium.cwf.api.encounter.EncounterParticipantContext;
import org.hspconsortium.cwf.api.patient.PatientContext;

/**
 * Encounter-related utility functions.
 */
public class EncounterUtil extends org.hspconsortium.cwf.api.encounter.EncounterUtil {

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

        EncounterParticipantComponent participant = EncounterParticipantContext.getActiveParticipant();
        String partId = getParticipantId(participant);
        String s = VistAUtil.getBrokerSession().callRPC("RGCWENCX FETCH", patient.getIdElement().getIdPart(),
            encode(encounter), partId, true);
        String id = StrUtil.piece(s, StrUtil.U, 6);

        if (!VistAUtil.validateIEN(id)) {
            return false;
        }

        encounter.setId(id);
        return true;
    }

    public static String getParticipantId(EncounterParticipantComponent participant) {
        return participant == null ? null : participant.getIndividual().getReferenceElement().getIdPart();
    }

    /**
     * Updates the participants associated with an encounter.
     *
     * @param encounter The encounter.
     */
    public static void updateParticipants(Encounter encounter) {
        if (encounter == null || encounter.getParticipant().isEmpty()) {
            return;
        }

        RPCParameter params = new RPCParameter();

        for (EncounterParticipantComponent participant : encounter.getParticipant()) {
            String partId = getParticipantId(participant);
            params.put(partId, "+");
        }

        String dfn = PatientContext.getActivePatient().getIdElement().getIdPart();
        VistAUtil.getBrokerSession().callRPC("RGCWENCX UPDPRV", dfn, encode(encounter), params, true);
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

    /**
     * Encode an encounter to a visit string.
     *
     * @param encounter The encounter.
     * @return The encoded encounter (visit string).
     */
    public static String encode(Encounter encounter) {
        Location location = ClientUtil.getResource(encounter.getLocationFirstRep().getLocation(), Location.class);
        String locIEN = location.isEmpty() ? "" : location.getIdElement().getIdPart();
        Date date = encounter.getPeriod().getStart();
        String sc = getServiceCategory(encounter);
        String ien = encounter.getId().isEmpty() ? "" : encounter.getIdElement().getIdPart();
        return locIEN + VSTR_DELIM + new FMDate(date).getFMDate() + VSTR_DELIM + sc + VSTR_DELIM + ien;
    }

    /**
     * Enforces static class.
     */
    protected EncounterUtil() {
    }

}
