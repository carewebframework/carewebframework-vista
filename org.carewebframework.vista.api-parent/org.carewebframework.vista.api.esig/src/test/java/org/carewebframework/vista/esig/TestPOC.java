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

import org.carewebframework.vista.esig.ESigItem;
import org.carewebframework.vista.esig.ESigItem.SignState;

public class TestPOC extends TestType {
    
    private static final String[] text = { "Blood Pressure: 120/82", "Heart Rate: 88", "Respiratory Rate: 12",
            "Weight: 183 lbs", "Fingerstick BS: 120" };
    
    private int index = 0;
    
    public TestPOC() throws Exception {
        super("POC", "Encounter Data");
    }
    
    @Override
    protected String getText() {
        if (index >= text.length) {
            index = 0;
        }
        
        return text[index++];
    }
    
    @Override
    public ESigItem newItem() {
        ESigItem item = super.newItem();
        item.setSignState(SignState.FORCED_YES);
        return item;
    }
    
}
