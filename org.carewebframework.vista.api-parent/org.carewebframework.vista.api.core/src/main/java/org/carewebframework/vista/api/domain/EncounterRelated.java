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

import org.carewebframework.cal.api.domain.DomainObject;
import org.carewebframework.fhir.model.resource.Encounter;

/**
 * Abstract base class for encounter-associated domain objects.
 */
public abstract class EncounterRelated extends DomainObject {
    
    private Encounter encounter;
    
    public EncounterRelated() {
        super();
    }
    
    public EncounterRelated(Encounter encounter) {
        super();
        this.encounter = encounter;
    }
    
    public Encounter getEncounter() {
        return encounter;
    }
    
    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }
    
}
