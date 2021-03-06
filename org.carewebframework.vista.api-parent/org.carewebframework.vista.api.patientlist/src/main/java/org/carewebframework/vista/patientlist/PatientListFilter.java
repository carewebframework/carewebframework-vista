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
package org.carewebframework.vista.patientlist;

import org.hspconsortium.cwf.api.patientlist.AbstractPatientListFilter;

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
