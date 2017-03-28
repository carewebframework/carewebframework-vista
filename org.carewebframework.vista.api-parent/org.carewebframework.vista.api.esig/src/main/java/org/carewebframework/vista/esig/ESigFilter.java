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

/**
 * Filter used to subselect items returned from an esig list.
 */
public class ESigFilter {
    
    private final List<String> ids;
    
    private final IESigType eSigType;
    
    private final String session;
    
    private final Boolean selected;
    
    /**
     * Creates a filter. Pass null for those criteria to be ignored.
     * 
     * @param eSigType ESig type to match.
     * @param session Session id to match.
     * @param ids Unique item id's to match (should always be used with eSigType specified).
     * @param selected Selection state to match.
     */
    public ESigFilter(IESigType eSigType, String session, List<String> ids, Boolean selected) {
        this.eSigType = eSigType;
        this.session = session;
        this.ids = ids;
        this.selected = selected;
    }
    
    /**
     * Returns true if the item matches the selection filter.
     * 
     * @param item The esignature item.
     * @return True if the item matches the selection filter.
     */
    public boolean matches(ESigItem item) {
        if (selected != null && item.isSelected() != selected) {
            return false;
        }
        
        if (eSigType != null && !item.getESigType().equals(eSigType)) {
            return false;
        }
        
        if (session != null && !item.getSession().equals(session)) {
            return false;
        }
        
        if (ids != null && !ids.contains(item.getId())) {
            return false;
        }
        
        return true;
    }
    
}
