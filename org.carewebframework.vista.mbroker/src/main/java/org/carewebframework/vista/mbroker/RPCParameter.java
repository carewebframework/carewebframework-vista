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
package org.carewebframework.vista.mbroker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a single parameter passed to an RPC. A parameter may have a scalar and/or a vector
 * component, corresponding to an M variable and M array, respectively.
 */
public class RPCParameter implements Iterable<String> {
    
    /**
     * Type of data contained in the parameter (think $DATA).
     */
    public static enum HasData {
        NONE, SCALAR, VECTOR, BOTH
    };
    
    private final Map<String, Object> values = new HashMap<String, Object>();
    
    /**
     * Creates an empty parameter.
     */
    public RPCParameter() {
        
    }
    
    /**
     * Copy constructor.
     *
     * @param source RPC parameter to copy.
     */
    public RPCParameter(RPCParameter source) {
        assign(source);
    }
    
    /**
     * Returns number of elements (scalar + vector).
     * 
     * @return Number of elements.
     */
    public int getCount() {
        return values.size();
    }
    
    /**
     * Returns the type of data contained in the parameter.
     * 
     * @return Type of data.
     */
    public HasData hasData() {
        boolean scalar = values.containsKey("");
        boolean vector = scalar ? getCount() > 1 : getCount() > 0;
        return scalar && vector ? HasData.BOTH : scalar ? HasData.SCALAR : vector ? HasData.VECTOR : HasData.NONE;
    }
    
    /**
     * Returns the scalar value. A runtime exception is thrown if no scalar value exists.
     *
     * @return The scalar value.
     */
    public Object getValue() {
        return get("");
    }
    
    /**
     * Returns the subscripted vector value. A runtime exception is thrown if no vector value exists
     * at the specified subscript.
     *
     * @param subscript The subscript specifier.
     * @return The vector value.
     */
    public Object get(String subscript) {
        if (!values.containsKey(subscript)) {
            throw new RuntimeException("Subscript not found");
        }
        
        return values.get(subscript);
    }
    
    /**
     * Assigns values from a source parameter to this parameter.
     *
     * @param source Source parameter
     */
    public void assign(RPCParameter source) {
        clear();
        values.putAll(source.values);
    }
    
    /**
     * Copies source values as integer-indexed vector values.
     *
     * @param source Source values.
     */
    public void assign(Iterable<?> source) {
        clear();
        int i = 0;
        
        for (Object value : source) {
            values.put(Integer.toString(++i), value);
        }
    }
    
    /**
     * Copies source values from an input array as integer-indexed vector values.
     *
     * @param source Source values.
     */
    public void assignArray(Object source) {
        int len = Array.getLength(source);
        List<Object> list = new ArrayList<Object>(len);
        
        for (int i = 0; i < len; i++) {
            list.add(Array.get(source, i));
        }
        
        assign(list);
    }
    
    /**
     * Clears all values (scalar and vector).
     */
    public void clear() {
        values.clear();
    }
    
    /**
     * Deletes a vector value at the specified subscript.
     *
     * @param subscript Subscript specifier.
     */
    public void delete(String subscript) {
        values.remove(subscript);
    }
    
    /**
     * Adds (or removes) a vector value at the specified subscript.
     *
     * @param subscript Subscript specifier
     * @param value Value (if null, any existing value is removed).
     */
    public void put(String subscript, Object value) {
        if (value == null) {
            values.remove(subscript);
        } else {
            values.put(subscript, value);
        }
    }
    
    /**
     * Adds a vector value at the specified subscript.
     *
     * @param subscript Array of subscript values.
     * @param value Value.
     */
    public void put(Object[] subscript, Object value) {
        put(BrokerUtil.buildSubscript(subscript), value);
    }
    
    /**
     * Adds a vector value at the specified subscript.
     *
     * @param subscript List of subscript values.
     * @param value Value.
     */
    public void put(Iterable<Object> subscript, Object value) {
        put(BrokerUtil.buildSubscript(subscript), value);
    }
    
    /**
     * Sets the scalar value.
     *
     * @param value Value.
     */
    public void setValue(Object value) {
        put("", value);
    }
    
    /**
     * Returns an iterator to traverse all subscripts.
     */
    @Override
    public Iterator<String> iterator() {
        return values.keySet().iterator();
    }
}
