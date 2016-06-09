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
    RAW("@raw@"), NULL("@null@"), JAVA("@java.io.Serializable@"), JSON("@json@");
    
    private final String magic;
    
    /**
     * Detect serialization method from data.
     * 
     * @param data Data, possibly serialized.
     * @return The serialization method used, or null if not serialized.
     */
    public static SerializationMethod detect(String data) {
        if (data != null) {
            for (SerializationMethod method : values()) {
                if (data.startsWith(method.magic)) {
                    return method;
                }
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
    
    private SerializationMethod(String magic) {
        this.magic = magic;
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
