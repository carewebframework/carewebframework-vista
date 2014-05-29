/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.context;

import java.util.List;

import org.carewebframework.api.context.UserContext;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.api.domain.DomainObjectFactory;
import org.carewebframework.vista.api.domain.Encounter;
import org.carewebframework.vista.api.domain.EncounterProvider;
import org.carewebframework.vista.api.domain.Patient;
import org.carewebframework.vista.api.domain.Provider;
import org.carewebframework.vista.api.domain.User;
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
        if (encounter == null || !encounter.isPrepared()) {
            return false;
        }
        
        if (VistAUtil.validateIEN(encounter)) {
            return true;
        }
        
        Patient patient = PatientContext.getCurrentPatient();
        
        if (patient == null) {
            return false;
        }
        
        String s = VistAUtil.getBrokerSession().callRPC("RGCWENCX FETCH", patient.getDomainId(), encounter.getEncoded(),
            encounter.getEncounterProvider().getCurrentProvider().getDomainId(), true);
        String id = StrUtil.piece(s, StrUtil.U, 6);
        
        if (!VistAUtil.validateIEN(id)) {
            return false;
        }
        
        encounter.setDomainId(id);
        return true;
    }
    
    public static EncounterProvider getEncounterProvider(Patient patient, Encounter encounter) {
        EncounterProvider encounterProvider = encounter.getEncounterProvider();
        Provider currentProvider = encounterProvider.getCurrentProvider();
        User user = (User) UserContext.getActiveUser();
        encounterProvider.clear();
        List<String> data = VistAUtil.getBrokerSession().callRPCList("RGCWENCX GETPRV", null, patient.getDomainId(),
            encounter.getEncoded());
        Provider primaryProvider = null;
        // IEN^Name^Primary^EncDT
        for (String prv : data) {
            String[] pcs = StrUtil.split(prv, StrUtil.U, 4);
            Provider provider = ProviderUtil.fetchProvider(pcs[0]);
            encounterProvider.add(provider);
            
            if (primaryProvider == null && StrUtil.toBoolean(pcs[2])) {
                primaryProvider = provider;
                encounterProvider.setPrimaryProvider(provider);
            }
            
            if (currentProvider == null && provider.equals(user)) {
                currentProvider = provider;
            }
        }
        
        encounterProvider.setCurrentProvider(currentProvider != null ? currentProvider
                : primaryProvider != null ? primaryProvider : DomainObjectFactory.get(Provider.class, user.getDomainId()));
        return encounterProvider;
    }
    
    /**
     * Enforces static class.
     */
    private EncounterUtil() {
    };
    
}
