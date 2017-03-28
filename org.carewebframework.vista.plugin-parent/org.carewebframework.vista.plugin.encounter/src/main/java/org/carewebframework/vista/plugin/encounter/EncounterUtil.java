/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.plugin.encounter;

import java.util.Set;

import org.carewebframework.common.DateUtil;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.vista.api.encounter.EncounterFlag;
import org.carewebframework.vista.api.util.VistAUtil;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent;
import org.hspconsortium.cwf.api.encounter.EncounterContext;
import org.hspconsortium.cwf.api.encounter.EncounterParticipantContext;

/**
 * Encounter-related utilities.
 */
public class EncounterUtil extends org.carewebframework.vista.api.encounter.EncounterUtil {

    private static final String TC_NO_ACTIVE_VISIT = "Active Visit Not Selected";

    private static final String TX_NO_ACTIVE_VISIT = "An active visit has not been selected.";

    private static final Set<EncounterFlag> EF1 = EncounterFlag.flags(EncounterFlag.NOT_LOCKED, EncounterFlag.FORCE,
        EncounterFlag.VALIDATE_ONLY);

    public static boolean validEncounter() {
        return validEncounter(EncounterContext.getActiveEncounter());
    }

    public static boolean validEncounter(Encounter encounter) {
        return encounter != null && EncounterUtil.isPrepared(encounter);
    }

    public static String validEncounter(Encounter encounter, Set<EncounterFlag> flags) {
        if (EncounterUtil.isLocked(encounter) && EncounterFlag.hasFlag(flags, EncounterFlag.NOT_LOCKED)) {
            return Constants.TX_NO_LCK;
        }

        StringBuilder sb = new StringBuilder();

        if (encounter.getLocation() == null) {
            appendItem(sb, Constants.TX_NO_LOC);
        }

        if (encounter.getType() == null) {
            appendItem(sb, Constants.TX_NO_CAT);
        }

        if (encounter.getPeriod() == null) {
            appendItem(sb, Constants.TX_NO_DAT);
        }

        if (EncounterParticipantContext.getActivePractitioner() == null) {
            appendItem(sb, Constants.TX_NO_PRV);
        }

        if (EncounterUtil.getPrimaryParticipant(encounter) == null && !EncounterUtil.isLocked(encounter)) {
            appendItem(sb, Constants.TX_NO_PRI);
        }

        if (sb.length() > 0) {
            return Constants.TX_MISSING + sb.toString();
        }

        if (EncounterFlag.hasFlag(flags, EncounterFlag.FORCE) && VistAUtil.parseIEN(encounter) <= 0
                && DateUtil.stripTime(encounter.getPeriod().getStart()).after(DateUtil.today())) {
            return Constants.TX_NO_FUT;
        }

        if (EncounterFlag.hasFlag(flags, EncounterFlag.PROVIDER)
                && !isProvider(EncounterParticipantContext.getActivePractitioner())) {
            return Constants.TX_NO_KEY;
        }

        if (EncounterFlag.hasFlag(flags, EncounterFlag.FORCE) && VistAUtil.parseIEN(encounter) == 0
                && !EncounterUtil.forceCreate(encounter)) {
            return "Failed to create the visit.";
        }

        return null;
    }

    private static void appendItem(StringBuilder sb, String item) {
        sb.append("   ").append(item).append('\n');
    }

    private static boolean isProvider(EncounterParticipantComponent participant) {
        return participant != null && VistAUtil.getBrokerSession().callRPCBool("RGCWFUSR HASKEYS", "PROVIDER",
            participant.getIndividual().getReferenceElement().getIdPart());
    }

    public static boolean ensureEncounter() {
        return ensureEncounter(null);
    }

    public static boolean ensureEncounter(Set<EncounterFlag> flags) {
        Encounter encounter = EncounterContext.getActiveEncounter();
        boolean isValid = validEncounter(encounter);

        if (isValid && org.carewebframework.vista.api.encounter.EncounterUtil.isLocked(encounter)
                && EncounterFlag.hasFlag(flags, EncounterFlag.NOT_LOCKED)) {
            return false;
        }

        if (isValid && VistAUtil.parseIEN(encounter) == 0 && EncounterFlag.hasFlag(flags, EncounterFlag.FORCE)) {
            return org.carewebframework.vista.api.encounter.EncounterUtil.forceCreate(encounter);
        }

        if (isValid || EncounterFlag.hasFlag(flags, EncounterFlag.VALIDATE_ONLY)) {
            return isValid;
        }

        MainController.execute();
        return validEncounter();
    }

    public static boolean checkActiveVisit() {
        if (!ensureEncounter(EF1)) {
            PromptDialog.showError(TX_NO_ACTIVE_VISIT, TC_NO_ACTIVE_VISIT);
            return false;
        }

        return true;
    }

    /**
     * Enforces static class.
     */
    private EncounterUtil() {

    }

}
