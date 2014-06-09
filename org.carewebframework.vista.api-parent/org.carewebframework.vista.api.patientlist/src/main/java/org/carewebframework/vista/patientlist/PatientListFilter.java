/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.patientlist;

import org.carewebframework.cal.api.patientlist.AbstractPatientListFilter;

public class PatientListFilter extends AbstractPatientListFilter {
    
    public PatientListFilter(PatientListFilterEntity entity) {
        super(entity);
    }
    
    public PatientListFilter(String value) {
        super(value);
    }
    
    /**
     * Return the serialized form of the associated entity.
     */
    @Override
    protected String serialize() {
        return getEntity().toString();
    }
    
    /**
     * Deserialize an entity from its serialized form.
     */
    @Override
    protected PatientListFilterEntity deserialize(String value) {
        return new PatientListFilterEntity(value);
    }
    
    /**
     * Returns the display name of the associated entity (the name of the service location).
     */
    @Override
    protected String initName() {
        return getEntity() == null ? "" : ((PatientListFilterEntity) getEntity()).getName();
    }
}
