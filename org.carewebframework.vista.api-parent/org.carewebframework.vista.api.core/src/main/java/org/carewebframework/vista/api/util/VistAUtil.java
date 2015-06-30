/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.util;

import java.util.Date;

import ca.uhn.fhir.model.api.IResource;

import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.api.spring.SpringUtil;
import org.carewebframework.cal.api.DomainObject;
import org.carewebframework.common.DateUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.FMDate;

/**
 * Static utility class for the VistA extensions.
 */
public class VistAUtil {
    
    public static long parseIEN(String ien) {
        return NumberUtils.toLong(ien);
    }
    
    public static long parseIEN(IResource resource) {
        return resource == null ? 0 : parseIEN(resource.getId().getIdPart());
    }
    
    public static long parseIEN(DomainObject object) {
        return object == null ? 0 : parseIEN(object.getId().getIdPart());
    }
    
    public static boolean validateIEN(String ien) {
        return parseIEN(ien) > 0;
    }
    
    public static boolean validateIEN(IResource resource) {
        return validateIEN(resource.getId().getIdPart());
    }
    
    public static boolean validateIEN(DomainObject object) {
        return parseIEN(object) > 0;
    }
    
    public static BrokerSession getBrokerSession() {
        return SpringUtil.getBean("brokerSession", BrokerSession.class);
    }
    
    public static String normalizeDate(String value) {
        return normalizeDate(value, false);
    }
    
    public static String normalizeDate(String value, boolean includeTime) {
        Date date = parseDate(value);
        return date == null ? "" : DateUtil.formatDate(date, false, !includeTime);
    }
    
    public static Date parseDate(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        if (NumberUtils.isNumber(value)) {
            return new FMDate(value);
        }
        
        return DateUtil.parseDate(value);
    }
    
    public static String trimNarrative(String narrative) {
        return StrUtil.xlate(narrative == null ? "" : narrative, "^\n\r", "   ").trim();
    }
    
    public static String getSysParam(String param, String dflt, String instance) {
        String s = getBrokerSession().callRPC("RGCWFPAR GETPAR", param, "", instance == null ? "1" : instance);
        return s.isEmpty() ? dflt : s;
    }
    
    public static boolean setSysParam(String param, String value) {
        String s = StrUtil.piece(getBrokerSession().callRPC("RGCWFPAR SETPAR", param, value, "USR"), StrUtil.U, 2);
        return s.isEmpty();
    }
    
    /**
     * Converts a parameter list into a ^-delimited string
     *
     * @param params The parameter list.
     * @return Concatenated list.
     */
    public static String concatParams(Object... params) {
        return concatParams(StrUtil.U, params);
    }
    
    /**
     * Converts a parameter list into a delimited string
     * 
     * @param delimiter Delimiter to use.
     * @param params The parameter list.
     * @return Concatenated list.
     */
    public static String concatParams(String delimiter, Object... params) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        if (params != null) {
            for (Object param : params) {
                if (!first) {
                    sb.append(delimiter);
                } else {
                    first = false;
                }
                
                if (param != null) {
                    sb.append(param);
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Enforces static class.
     */
    private VistAUtil() {
    };
}
