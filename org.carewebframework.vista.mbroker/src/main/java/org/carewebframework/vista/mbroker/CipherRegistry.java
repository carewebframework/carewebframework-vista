/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.mbroker;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.carewebframework.common.AbstractRegistry;
import org.carewebframework.common.RegistryMap.DuplicateAction;

/**
 * Registry of encryption ciphers. This allows for the inclusion of custom ciphers for a particular
 * VistA implementation.
 */
public class CipherRegistry extends AbstractRegistry<String, String[]> {
    
    private static final CipherRegistry instance = new CipherRegistry();
    
    /**
     * Return singleton instance of cipher registry.
     * 
     * @return Cipher registry.
     */
    public static CipherRegistry getInstance() {
        return instance;
    }
    
    /**
     * Register a single cipher.
     * 
     * @param cipher Cipher to be registered.
     * @return CipherRegistry instance (for chaining).
     */
    public static CipherRegistry registerCipher(String[] cipher) {
        instance.register(cipher);
        return instance;
    }
    
    /**
     * Register multiple ciphers.
     * 
     * @param ciphers Ciphers to be registered.
     * @return CipherRegistry instance (for chaining).
     */
    public static CipherRegistry registerCiphers(String[]... ciphers) {
        for (String[] cipher : ciphers) {
            instance.register(cipher);
        }
        return instance;
    }
    
    /**
     * Register one or more ciphers from an input stream.
     * 
     * @param source Input stream containing cipher(s).
     * @return CipherRegistry instance (for chaining).
     */
    public static CipherRegistry registerCiphers(InputStream source) {
        try {
            if (source == null) {
                throw new IllegalArgumentException("Cipher source not found.");
            }
            
            List<String> lines = IOUtils.readLines(source);
            int start = -1;
            int index = 0;
            boolean error = false;
            
            while (index < lines.size()) {
                String line = lines.get(index);
                
                if (line.trim().isEmpty()) {
                    lines.remove(index);
                    continue;
                }
                
                if (line.equals("-----BEGIN CIPHER-----")) {
                    if (start == -1) {
                        start = index + 1;
                    } else {
                        error = true;
                    }
                } else if (line.equals("-----END CIPHER-----")) {
                    if (start > 0) {
                        int length = index - start;
                        String[] cipher = new String[length];
                        registerCipher(lines.subList(start, index).toArray(cipher));
                        start = -1;
                    } else {
                        error = true;
                    }
                }
                
                if (error) {
                    throw new IllegalArgumentException("Unexpected text in cipher: " + line);
                }
                
                index++;
            }
            
            if (start > 0) {
                throw new IllegalArgumentException("Missing end cipher token.");
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(source);
        }
        
        return instance;
    }
    
    /**
     * Returns the cipher for the specified key.
     * 
     * @param cipherKey The cipher key.
     * @return The corresponding cipher.
     */
    public static String[] getCipher(String cipherKey) {
        return instance.get(cipherKey);
    }
    
    /**
     * Enforce singleton instance. Register default cipher.
     */
    private CipherRegistry() {
        super(DuplicateAction.ERROR);
    }
    
    /**
     * Override to return default cipher if input is null or throw an exception if the cipher key is
     * not recognized.
     */
    @Override
    public String[] get(String key) {
        String[] cipher = super.get(key);
        
        if (cipher == null) {
            throw new IllegalArgumentException("Cipher is unknown.");
        }
        
        return cipher;
    }
    
    /**
     * The cipher key is the first four characters of the cipher itself.
     */
    @Override
    protected String getKey(String[] item) {
        return item[0].substring(0, 4);
    }
    
}
