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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import org.carewebframework.common.DateUtil;

/**
 * Represents a $HOROLOG-style date.
 */
public class HODate extends Date {
    
    private static final long serialVersionUID = 1L;
    
    private boolean hasTime;
    
    /**
     * Converts a string value to a $H date.
     *
     * @param value The string value to convert.
     * @return A $H date.
     */
    public static HODate fromString(String value) {
        return StringUtils.isEmpty(value) ? null : new HODate(value);
    }
    
    /**
     * Creates a date based on the $H-formatted value.
     * 
     * @param value $H-formatted value.
     */
    public HODate(String value) {
        super();
        setHODate(value);
    }
    
    /**
     * Creates a $H date with the current date and time.
     */
    public HODate() {
        super();
        this.hasTime = DateUtil.hasTime(this);
    }
    
    /**
     * Creates a $H date from a date value.
     *
     * @param date Date value.
     */
    public HODate(Date date) {
        super();
        this.setTime(date.getTime());
        this.hasTime = date instanceof HODate ? ((HODate) date).hasTime : DateUtil.hasTime(this);
    }
    
    /**
     * Sets the date to the value corresponding to the $H value.
     *
     * @param value $H-formatted date.
     */
    public void setHODate(String value) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        String[] pcs = value.split("\\,", 2);
        long ms = (Long.parseLong(pcs[0]) - 47116) * 24 * 60 * 60 * 1000;
        cal.setTimeInMillis(ms);
        hasTime = pcs.length == 2;
        
        if (hasTime) {
            int frac = Integer.parseInt(pcs[1]);
            cal.set(Calendar.SECOND, frac % 60);
            frac /= 60;
            cal.set(Calendar.MINUTE, frac % 60);
            frac /= 60;
            cal.set(Calendar.HOUR, frac);
        }
        
        setTime(cal.getTimeInMillis());
    }
    
    /**
     * Returns the display friendly value of the date.
     *
     * @return Display friendly date.
     */
    @Override
    public String toString() {
        return DateUtil.formatDate(this, false, !hasTime);
    }
    
    /**
     * Returns the display friendly value of the date, including time zone.
     *
     * @return Display friendly date.
     */
    public String toStringFull() {
        return !hasTime ? toString() : DateUtil.formatDate(this, true);
    }
    
    /**
     * Returns the display friendly value of the date, ignoring any time component.
     *
     * @return Display friendly date.
     */
    public String toStringDateOnly() {
        return DateUtil.formatDate(this, false, true);
    }
}
