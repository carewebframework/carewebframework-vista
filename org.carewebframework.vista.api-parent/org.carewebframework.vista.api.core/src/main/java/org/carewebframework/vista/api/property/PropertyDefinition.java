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
package org.carewebframework.vista.api.property;

import java.util.HashMap;
import java.util.Map;

import org.carewebframework.common.JSONUtil;
import org.hspconsortium.cwf.api.DomainObject;

public class PropertyDefinition extends DomainObject {

    static {
        JSONUtil.registerAlias("PropDefinition", PropertyDefinition.class);
    }

    private static final Map<String, PropertyDefinition> cache = new HashMap<>();

    private String name;

    private String displayName;

    private boolean multiValued;

    private String dataType;

    private boolean readOnly;

    private String description;

    private String hint;

    public static synchronized PropertyDefinition get(String name) {
        PropertyDefinition def = cache.get(name);

        if (def == null) {
            def = PropertyUtil.getPropertyDAO().getDefinition(name);
            cache.put(name, def);
        }

        return def;
    }

    public static synchronized void clearCache() {
        cache.clear();
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    protected void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    protected void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isMultiValued() {
        return multiValued;
    }

    protected void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
    }

    public String getDataType() {
        return dataType;
    }

    protected void setDataType(String dataType) {
        this.dataType = dataType;
    }

}
