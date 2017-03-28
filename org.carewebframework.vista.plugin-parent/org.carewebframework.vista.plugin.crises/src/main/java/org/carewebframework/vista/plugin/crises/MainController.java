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
package org.carewebframework.vista.plugin.crises;

import static org.carewebframework.common.StrUtil.U;
import static org.carewebframework.common.StrUtil.fromList;
import static org.carewebframework.common.StrUtil.split;

import java.util.ArrayList;
import java.util.List;

import org.carewebframework.vista.api.util.VistAUtil;
import org.carewebframework.vista.ui.common.CoverSheetBase;

/**
 * Controller for crisis alert cover sheet. Displays summary and detail views of crisis alerts.
 */
public class MainController extends CoverSheetBase<String> {

    private static final long serialVersionUID = 1L;

    @Override
    protected void init() {
        setup("Crisis Alerts", "Crisis Detail", "RGCWCACV LIST", null, 1, "Crisis Alert", "Date");
        super.init();
    }

    /**
     * Logic to return detail information for specified item.
     *
     * @param data The item data.
     * @return The detail information.
     */
    @Override
    protected String getDetail(String data) {
        String pcs[] = split(data, U, 2);
        char type = pcs[1].isEmpty() ? 0 : pcs[1].charAt(0);
        List<String> result = new ArrayList<>();

        switch (type) {
            case 'A':
                getBroker().callRPCList("RGCWCACV DETAIL", result, patient.getIdElement().getIdPart());
                break;

            case 'F':
                getBroker().callRPCList("RGCWCACV PRF", result, patient.getIdElement().getIdPart(), pcs[0]);
                break;

            default:
                getBroker().callRPCList("TIU GET RECORD TEXT", result, pcs[0]);
                break;
        }

        return result.isEmpty() ? null : fromList(result);
    }

    @Override
    protected String getError(List<String> list) {
        return null;
    }

    @Override
    protected void render(String dao, List<Object> columns) {
        String pcs[] = split(dao, U, 5);
        columns.add(pcs[2]);
        columns.add(VistAUtil.normalizeDate(pcs[4]));
    }

}
