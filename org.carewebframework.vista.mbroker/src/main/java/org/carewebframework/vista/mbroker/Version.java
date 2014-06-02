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
