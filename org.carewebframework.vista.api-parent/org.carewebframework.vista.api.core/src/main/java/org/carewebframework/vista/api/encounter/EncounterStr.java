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

import org.apache.commons.lang.StringUtils;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.mbroker.FMDate;
import org.hl7.fhir.dstu3.model.Encounter;

/**
 * Convenience class for the representation of an encounter (visit) string.
 */
public class EncounterStr {

    private String location;

    private FMDate visitDate;

    private String serviceCat;

    private String id;

    public EncounterStr() {
        this((String) null);
    }

    public EncounterStr(Encounter encounter) {
        this(EncounterUtil.encode(encounter));
    }

    public EncounterStr(String value) {
        setString(value);
    }

    public void setString(String value) {
        String[] pcs = StrUtil.split(value, ";", 4);
        location = pcs[0];
        visitDate = FMDate.fromString(pcs[1]);
        serviceCat = pcs[2];
        id = pcs[3];
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public FMDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(FMDate visitDate) {
        this.visitDate = visitDate;
    }

    public String getServiceCat() {
        return serviceCat;
    }

    public void setServiceCat(String serviceCat) {
        this.serviceCat = serviceCat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(serviceCat) && StringUtils.isEmpty(location) && visitDate == null;
    }

    /**
     * Returns encounter as an encounter string.
     *
     * @return Encounter string.
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(location == null ? "" : location).append(';');
        sb.append(visitDate == null ? "" : visitDate.getFMDate()).append(';');
        sb.append(serviceCat == null ? "" : serviceCat).append(';');
        sb.append(id == null ? "" : id);
        return sb.toString();
    }

}
