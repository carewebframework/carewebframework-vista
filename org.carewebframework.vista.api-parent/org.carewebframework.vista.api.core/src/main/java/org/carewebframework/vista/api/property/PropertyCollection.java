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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Holds a map of property values, given one or more prefixes. Properties are loaded and indexed by
 * the property name.
 */
public class PropertyCollection {
    
    private static final Log log = LogFactory.getLog(PropertyCollection.class);
    
    private final Map<String, Property> properties = new HashMap<String, Property>();
    
    /**
     * Instantiates an empty collection Reference {@link #loadProperties(String)} to populate
     * collection
     */
    public PropertyCollection() {
    }
    
    /**
     * Load property objects with the given array of prefixes
     *
     * @param propertyPrefixes List of property prefixes.
     */
    public PropertyCollection(String... propertyPrefixes) {
        for (String prefix : propertyPrefixes) {
            loadProperties(prefix);
        }
    }
    
    /**
     * Loads all property objects beginning with the specified prefix. The property objects are
     * stored in a map indexed by the property name for easy retrieval.
     *
     * @param prefix The property prefix.
     */
    public void loadProperties(String prefix) {
        List<Property> props = null;
        
        try {
            props = getListByPrefix(prefix);
        } catch (Exception e) {
            log.error("Error retrieving properties with prefix: " + prefix, e);
            return;
        }
        
        for (Property prop : props) {
            if (prop.getName() != null) {
                this.properties.put(prop.getName(), prop);
            }
        }
    }
    
    private List<Property> getListByPrefix(String prefix) {
        List<Property> result = new ArrayList<Property>();
        List<String> props = PropertyUtil.getPropertyDAO().getMatching(prefix);
        
        for (String prop : props) {
            result.add(new Property(prop));
        }
        
        return result;
    }
    
    /**
     * Clears current Map of properties
     */
    public void clear() {
        this.properties.clear();
    }
    
    /**
     * Returns the property with the specified name, or null if not found.
     *
     * @param name Name of property to find.
     * @return Property object with the specified name, or null if not found.
     */
    public Property getProperty(String name) {
        return this.properties.get(name);
    }
    
    /**
     * Checks to see if current Map contains this propertyName as a key
     *
     * @param propertyName Property name to look for
     * @return boolean
     */
    public boolean containsProperty(String propertyName) {
        return this.properties.containsKey(propertyName);
    }
    
    /**
     * Returns the index of a property value as it occurs in the choice list.
     *
     * @param name Property name.
     * @param choices Array of possible choice values. The first entry is assumed to be the default.
     * @return Index of the property value in the choices array. Returns 0 if not found.
     */
    public int getValue(String name, String[] choices) {
        String val = getValue(name, "");
        int index = Arrays.asList(choices).indexOf(val);
        return index == -1 ? 0 : index;
    }
    
    /**
     * Returns a boolean property value.
     *
     * @param name Property name.
     * @param dflt Default value if a property value is not found.
     * @return Property value or default value if property value not found.
     */
    public boolean getValue(String name, boolean dflt) {
        try {
            String val = getValue(name, Boolean.toString(dflt)).toLowerCase();
            return val.startsWith("y") ? true : Boolean.parseBoolean(val);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Returns an integer property value.
     *
     * @param name Property name.
     * @param dflt Default value if a property value is not found.
     * @return Property value or default value if property value not found.
     */
    public int getValue(String name, int dflt) {
        try {
            return Integer.parseInt(getValue(name, Integer.toString(dflt)));
        } catch (Exception e) {
            return dflt;
        }
    }
    
    /**
     * Returns a string property value.
     *
     * @param name Property name.
     * @param dflt Default value if a property value is not found.
     * @return Property value or default value if property value not found.
     */
    public String getValue(String name, String dflt) {
        Property prop = getProperty(name);
        return prop == null ? dflt : prop.getValue();
    }
    
    /**
     * Returns the property map.
     *
     * @return The property map.
     */
    public Map<String, Property> getProperties() {
        return this.properties;
    }
}
