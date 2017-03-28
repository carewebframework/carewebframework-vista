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

import java.util.Arrays;

/**
 * Parses version information.
 */
public class Version {
    
    private final long[] ver = new long[4];
    
    /**
     * Create empty version instance.
     */
    public Version() {
        
    }
    
    /**
     * Extract version components from input value.
     *
     * @param value Input value.
     */
    public Version(String value) {
        this();
        setVersion(value);
    }
    
    /**
     * Set version components from input value.
     *
     * @param value Input value.
     */
    public void setVersion(String value) {
        clear();
        
        if (value == null || value.length() == 0) {
            return;
        }
        
        String pcs[] = value.split("\\.", 4);
        int len = Math.min(pcs.length, 4);
        
        for (int i = 0; i < len; i++) {
            ver[i] = Long.parseLong(pcs[i]);
        }
    }
    
    /**
     * Clear version components.
     */
    public void clear() {
        Arrays.fill(ver, 0);
    }
    
    /**
     * Return version info as formatted string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(20);
        int level = 1;
        
        for (int i = 0; i < 4; i++) {
            if (ver[i] != 0) {
                while (level < i) {
                    appendVersion(sb, 0);
                    level++;
                }
                
                appendVersion(sb, ver[i]);
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Append version component.
     *
     * @param sb String builder.
     * @param ver Version component value.
     */
    private void appendVersion(StringBuilder sb, long ver) {
        if (sb.length() > 0) {
            sb.append(".");
        }
        
        sb.append(ver);
    }
    
}
