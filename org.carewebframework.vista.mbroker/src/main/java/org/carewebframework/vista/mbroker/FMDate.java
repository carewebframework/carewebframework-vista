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

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import org.carewebframework.common.DateUtil;
import org.carewebframework.common.JSONUtil;

/**
 * Represents a FileMan-style date.
 */
public class FMDate extends Date {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Overrides default JSON date serialization to use FileMan format.
     */
    public static class FMDateFormat extends SimpleDateFormat {
        
        private static final long serialVersionUID = 1L;
        
        @Override
        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
            FMDate value = date instanceof FMDate ? (FMDate) date : new FMDate(date);
            return toAppendTo.append(value.getFMDate());
        }
        
        @Override
        public Date parse(String source, ParsePosition pos) {
            pos.setIndex(source.length());
            return FMDate.fromString(source);
        }
        
        @Override
        public FMDateFormat clone() {
            return new FMDateFormat();
        }
        
    }
    
    /**
     * Sets the date formatter to be used by the JSON parser.
     */
    static {
        JSONUtil.setDateFormat(new FMDateFormat());
    }
    
    private boolean hasTime;
    
    /**
     * Converts a string value to a FM date.
     *
     * @param value The string value to convert.
     * @return A FM date.
     */
    public static FMDate fromString(String value) {
        return StringUtils.isEmpty(value) ? null : new FMDate(value);
    }
    
    /**
     * Returns a FM date with current date and time.
     * 
     * @return FM date representing current date and time.
     */
    public static FMDate now() {
        return new FMDate();
    }
    
    /**
     * Returns a FM date with current date and no time.
     * 
     * @return FM date representing current date.
     */
    public static FMDate today() {
        return new FMDate(DateUtil.today());
    }
    
    /**
     * Creates a FM date corresponding to the specified FM string representation.
     *
     * @param value FM string.
     */
    public FMDate(String value) {
        super();
        setFMDate(value);
    }
    
    /**
     * Creates a FM date with a value corresponding to the current time.
     */
    public FMDate() {
        super();
        this.hasTime = DateUtil.hasTime(this);
    }
    
    /**
     * Creates a FM date with a value specified by the given date.
     *
     * @param date Value for FM date.
     */
    public FMDate(Date date) {
        super();
        this.setTime(date.getTime());
        this.hasTime = date instanceof FMDate ? ((FMDate) date).hasTime : DateUtil.hasTime(this);
    }
    
    /**
     * Sets a calendar component based on the input value. Meant to be called successively, with the
     * return value used as the input value for the next call, to extract lower digits from the
     * input value to be set into the calendar component.
     *
     * @param cal Calendar component.
     * @param part Calendar component to be set.
     * @param value Current value.
     * @param div Modulus for value to be extracted.
     * @return Original value divided by modulus.
     */
    private long setCalendar(Calendar cal, int part, long value, long div) {
        cal.set(part, (int) (value % div));
        return value / div;
    }
    
    /**
     * Sets the date value based on the specified FM string representation.
     *
     * @param value FM string representation.
     */
    public void setFMDate(String value) {
        final long multiplier = 100000000;
        Calendar cal = Calendar.getInstance();
        double val = Double.parseDouble(value);
        
        if (val < 1000000D || val > 9999999D) {
            throw new IllegalArgumentException();
        }
        
        long date = (long) Math.floor(val * multiplier);
        hasTime = date % multiplier != 0;
        date = setCalendar(cal, Calendar.MILLISECOND, date, 100);
        date = setCalendar(cal, Calendar.SECOND, date, 100);
        date = setCalendar(cal, Calendar.MINUTE, date, 100);
        date = setCalendar(cal, Calendar.HOUR_OF_DAY, date, 100);
        date = setCalendar(cal, Calendar.DAY_OF_MONTH, date, 100);
        date = setCalendar(cal, Calendar.MONTH, date - 1, 100);
        date = setCalendar(cal, Calendar.YEAR, date + 1700, 10000);
        setTime(cal.getTimeInMillis());
    }
    
    /**
     * Returns the FM string representation of the date.
     *
     * @return FM string representation.
     */
    public String getFMDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this);
        StringBuilder sb = new StringBuilder(15);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        
        if (hour == 0 && hasTime) {
            hour = 24;
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        
        addFMPiece(sb, cal.get(Calendar.YEAR) - 1700, 0);
        addFMPiece(sb, cal.get(Calendar.MONTH) + 1, 2);
        addFMPiece(sb, cal.get(Calendar.DAY_OF_MONTH), 2);
        
        if (hasTime) {
            sb.append('.');
            addFMPiece(sb, hour, 2);
            addFMPiece(sb, cal.get(Calendar.MINUTE), 2);
            addFMPiece(sb, cal.get(Calendar.SECOND), 2);
            addFMPiece(sb, cal.get(Calendar.MILLISECOND), 2);
            
            for (int i = sb.length() - 1; i >= 0; i--) {
                char c = sb.charAt(i);
                
                if (c == '0') {
                    sb.deleteCharAt(i);
                } else if (c == '.') {
                    sb.deleteCharAt(i);
                    break;
                } else {
                    break;
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Used to build a FM string representation of a date.
     * 
     * @param sb String builder.
     * @param value Value to append.
     * @param pad Left pad with zeros to this number of digits.
     */
    private void addFMPiece(StringBuilder sb, int value, int pad) {
        String val = Integer.toString(value);
        pad -= val.length();
        
        while (pad-- > 0) {
            sb.append('0');
        }
        
        sb.append(val);
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
