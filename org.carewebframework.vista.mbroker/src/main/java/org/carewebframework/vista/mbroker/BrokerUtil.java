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
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class BrokerUtil {

    private static final String QT = "\"";

    private static final String QT2 = "\"\"";

    /**
     * Converts a value to a string form suitable for sending to the server.
     *
     * @param value Object value to convert.
     * @return Converted form.
     */
    public static String toString(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value) ? "1" : "0";
        }

        if (value instanceof Double) {
            String val = value.toString();
            return val.startsWith("0.") ? val.substring(1) : val.startsWith("-0.") ? "-" + val.substring(2) : val;
        }

        if (value instanceof FMDate) {
            return ((FMDate) value).getFMDate();
        }

        if (value instanceof Date) {
            return new FMDate((Date) value).getFMDate();
        }

        return value == null ? "" : value.toString();
    }

    /**
     * Converts an array of objects to a string of M subscripts.
     *
     * @param subscripts Array to convert.
     * @return List of subscripts in M format.
     */
    public static String buildSubscript(Object[] subscripts) {
        return buildSubscript(Arrays.asList(subscripts));
    }

    /**
     * Converts a list of objects to a string of M subscripts.
     *
     * @param subscripts List to convert.
     * @return List of subscripts in M format.
     */
    public static String buildSubscript(Iterable<Object> subscripts) {
        StringBuilder sb = new StringBuilder();

        for (Object subscript : subscripts) {
            String value = toString(subscript);

            if (value.isEmpty()) {
                throw new RuntimeException("Null subscript not allowed.");
            }

            if (sb.length() > 0) {
                sb.append(",");
            }

            if (StringUtils.isNumeric(value)) {
                sb.append(value);
            } else {
                sb.append(QT);
                sb.append(value.replaceAll(QT, QT2));
                sb.append(QT);
            }
        }

        return sb.toString();
    }

    /**
     * Enforces static class.
     */
    private BrokerUtil() {
    };
}
