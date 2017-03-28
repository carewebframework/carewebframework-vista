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

import org.apache.commons.lang.math.NumberUtils;
import org.carewebframework.api.spring.SpringUtil;
import org.carewebframework.common.DateUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.FMDate;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.cwf.api.DomainObject;

/**
 * Static utility class for the VistA extensions.
 */
public class VistAUtil {

    public static long parseIEN(String ien) {
        return NumberUtils.toLong(ien);
    }

    public static long parseIEN(IBaseResource resource) {
        return resource == null ? 0 : parseIEN(resource.getIdElement().getIdPart());
    }

    public static long parseIEN(DomainObject object) {
        return object == null ? 0 : parseIEN(object.getId().getIdPart());
    }

    public static boolean validateIEN(String ien) {
        return parseIEN(ien) > 0;
    }

    public static boolean validateIEN(IBaseResource resource) {
        return validateIEN(resource.getIdElement().getIdPart());
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
        return StrUtil.toDelimitedStr(StrUtil.U, params);
    }

    /**
     * Enforces static class.
     */
    private VistAUtil() {
    };
}
