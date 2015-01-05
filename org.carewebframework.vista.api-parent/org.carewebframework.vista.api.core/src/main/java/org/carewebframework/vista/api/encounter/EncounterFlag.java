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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum EncounterFlag {
    NOT_LOCKED, FORCE, VALIDATE_ONLY, PROVIDER;
    
    /**
     * Returns a set of flags
     *
     * @param flags The encounter flags.
     * @return Set of encounter flags.
     */
    public static Set<EncounterFlag> flags(EncounterFlag... flags) {
        return flags == null || flags.length == 0 ? EnumSet.noneOf(EncounterFlag.class) : EnumSet.copyOf(Arrays
                .asList(flags));
    }
    
    public static boolean hasFlag(Set<EncounterFlag> flags, EncounterFlag flag) {
        return flags == null ? false : flags.contains(flag);
    }
}
