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
 * Represents a specific type of esignature items. Each type will have an associated handler that
 * will implement the business logic to apply electronic signature to its member items.
 * 
 * 
 */
public class ESigType implements IESigType {
    
    private String id;
    
    private boolean requiresReview;
    
    private String groupHeader;
    
    private IESigTypeRegistry typeRegistry;
    
    private IESigService eSigService;
    
    /**
     * Create a new esignature type.
     * 
     * @param id Unique mnemonic identifier.
     * @param groupHeader Group header for display purposes.
     */
    protected ESigType(String id, String groupHeader) {
        super();
        this.id = id;
        this.groupHeader = groupHeader;
    }
    
    @Override
    public String getESigTypeId() {
        return id;
    }
    
    @Override
    public boolean requiresReview() {
        return requiresReview;
    }
    
    @Override
    public String getESigTypeGroupHeader() {
        return groupHeader;
    }
    
    protected void setId(String id) {
        this.id = id;
    }
    
    protected void setRequiresReview(boolean requiresReview) {
        this.requiresReview = requiresReview;
    }
    
    protected void setGroupHeader(String groupHeader) {
        this.groupHeader = groupHeader;
    }
    
    @Override
    public void validateESigItems(List<ESigItem> items) {
        clearIssues(items);
    }
    
    protected void clearIssues(List<ESigItem> items) {
        for (ESigItem item : items) {
            item.clearIssues();
        }
    }
    
    @Override
    public void loadESigItems(List<ESigItem> items) {
    }
    
    @Override
    public void signESigItems(List<ESigItem> items, String esig) {
        for (ESigItem item : items) {
            geteSigService().remove(item);
        }
    }
    
    @Override
    public boolean equals(Object object) {
        return object instanceof ESigType && ((ESigType) object).id.equals(id);
    }
    
    public void setTypeRegistry(IESigTypeRegistry typeRegistry) throws Exception {
        this.typeRegistry = typeRegistry;
        typeRegistry.register(this);
    }
    
    public IESigTypeRegistry getTypeRegistry() {
        return typeRegistry;
    }
    
    public void seteSigService(IESigService eSigService) {
        this.eSigService = eSigService;
    }
    
    public IESigService geteSigService() {
        return eSigService;
    }
    
}
