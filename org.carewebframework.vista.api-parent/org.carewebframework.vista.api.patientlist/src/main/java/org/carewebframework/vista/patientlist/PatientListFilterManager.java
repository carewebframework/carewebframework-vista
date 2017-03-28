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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.api.util.VistAUtil;
import org.hspconsortium.cwf.api.patientlist.AbstractPatientListFilter;
import org.hspconsortium.cwf.api.patientlist.AbstractPatientListFilterManager;
import org.hspconsortium.cwf.api.patientlist.PatientListUtil;

/**
 * Filter manager for appointment-based lists.
 */
public class PatientListFilterManager extends AbstractPatientListFilterManager {
    
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(PatientListFilterManager.class);
    
    public PatientListFilterManager(PatientList patientList, Set<FilterCapability> capabilities) {
        super(patientList, capabilities);
    }
    
    @Override
    protected List<AbstractPatientListFilter> initFilters() {
        if (filters == null) {
            filters = new ArrayList<>();
            
            PatientList patientList = (PatientList) getPatientList();
            int id = patientList.getListId();
            boolean sortList = patientList.getSortList();
            boolean useMixedCase = patientList.getUseMixedCase();
            String range = patientList.formatDateRange();
            List<String> tempList = VistAUtil.getBrokerSession().callRPCList("RGCWPTPL LISTSEL", null, id, "", 1, 999,
                range);
            
            for (String item : tempList) {
                if (useMixedCase) {
                    String pcs[] = PatientListUtil.split(item, 2);
                    item = pcs[0] + StrUtil.U + PatientListUtil.formatName(pcs[1]);
                }
                PatientListFilter filter = new PatientListFilter(item);
                filters.add(filter);
            }
            
            if (sortList) {
                Collections.sort(filters);
            }
            
        }
        return filters;
    }
    
    @Override
    protected void refreshFilters() {
        filters = null;
        super.refreshFilters();
    }
    
    @Override
    protected AbstractPatientListFilter createFilter(Object entity) {
        return new PatientListFilter((PatientListFilterEntity) entity);
    }
    
    @Override
    protected AbstractPatientListFilter deserializeFilter(String serializedEntity) {
        return new PatientListFilter(serializedEntity);
    }
}
