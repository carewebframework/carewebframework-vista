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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.carewebframework.common.JSONUtil;
import org.carewebframework.common.MiscUtil;

/**
 * Serialization methods.
 */
public enum SerializationMethod {
    RAW, NULL, JAVA, JSON;
    
    private final String magic;
    
    private SerializationMethod() {
        magic = "@" + name() + "@";
    }
    
    /**
     * Detect serialization method from data.
     * 
     * @param data Data, possibly serialized.
     * @return The serialization method used, or null if not serialized.
     */
    public static SerializationMethod detect(String data) {
        if (data == null) {
            return null;
        }
        
        for (SerializationMethod method : values()) {
            if (data.startsWith(method.magic)) {
                return method;
            }
        }
        
        return null;
    }
    
    /**
     * Deserialize data if necessary.
     * 
     * @param data The raw data.
     * @return The deserialized data, or the original data if not serialized.
     */
    public static Object deserialize(String data) {
        SerializationMethod method = detect(data);
        return method == null ? data : method.doDeserialize(data);
    }
    
    /**
     * Serialize an object.
     * 
     * @param eventData Object to serialize.
     * @return The serialized object.
     */
    public String serialize(Object eventData) {
        if (eventData == null) {
            return NULL.magic;
        }
        
        String data;
        
        switch (this) {
            case JSON:
                data = JSONUtil.serialize(eventData);
                break;
            
            case JAVA:
                try (ByteArrayOutputStream bary = new ByteArrayOutputStream();
                        Base64OutputStream b64 = new Base64OutputStream(bary);
                        ObjectOutputStream oos = new ObjectOutputStream(b64)) {
                    oos.writeObject(eventData);
                    data = bary.toString("UTF-8");
                } catch (Exception e) {
                    throw MiscUtil.toUnchecked(e);
                }
                
                break;
            
            default:
                data = eventData.toString();
        }
        
        return magic + data;
    }
    
    /**
     * Deserialize data.
     * 
     * @param data The raw data.
     * @return The deserialized data.
     */
    private Object doDeserialize(String data) {
        data = data.substring(magic.length());
        
        switch (this) {
            case JSON:
                return JSONUtil.deserialize(data);
            
            case JAVA:
                try (ByteArrayInputStream bary = new ByteArrayInputStream(data.getBytes());
                        Base64InputStream b64 = new Base64InputStream(bary);
                        ObjectInputStream ois = new ObjectInputStream(b64);) {
                    return ois.readObject();
                } catch (Exception e) {
                    throw MiscUtil.toUnchecked(e);
                }
                
            case NULL:
                return null;
            
            default:
                return data;
        }
    }
    
}
