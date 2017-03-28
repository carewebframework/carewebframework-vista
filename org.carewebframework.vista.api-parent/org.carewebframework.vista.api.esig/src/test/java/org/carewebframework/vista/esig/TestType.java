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
package org.carewebframework.vista.esig;

import java.util.List;

import org.carewebframework.vista.esig.ESigItem.ESigItemIssueSeverity;
import org.hspconsortium.cwf.api.patient.PatientContext;

public abstract class TestType extends ESigType {

    private int id = 0;

    private int severityIndex = -1;

    private final int severityMax = ESigItemIssueSeverity.values().length;

    protected TestType(String id, String groupHeader) {
        super(id, groupHeader);
    }

    private String uniqueId() {
        return getESigTypeId() + "-" + ++id;
    }

    public ESigItem newItem() {
        String text = getText();
        ESigItem item = new ESigItem(this, uniqueId());
        item.setSession("Test Session");
        item.setText(text);
        return item;
    }

    private ESigItem newItem(List<ESigItem> items) {
        ESigItem item = newItem();
        items.add(item);
        return item;
    }

    abstract protected String getText();

    @Override
    public void loadESigItems(List<ESigItem> items) {
        if (PatientContext.getActivePatient() != null) {
            for (int i = 0; i < 5; i++) {
                newItem(items);
            }
        }
    }

    @Override
    public void validateESigItems(List<ESigItem> items) {
        super.validateESigItems(items);

        for (ESigItem item : items) {
            if (++severityIndex >= severityMax) {
                severityIndex = -1;
            } else {
                ESigItemIssueSeverity severity = ESigItemIssueSeverity.values()[severityIndex];
                item.addIssue(severity, "This is a test issue with a severity level of " + severity.name());
            }
        }
    }

    @Override
    public void signESigItems(List<ESigItem> items, String esig) {
        super.signESigItems(items, esig);
    }

}
