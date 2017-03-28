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
package org.carewebframework.vista.api.notification;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.common.DateUtil;
import org.carewebframework.common.NumUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.mbroker.FMDate;

/**
 * Base class for notifications.
 */
public abstract class AbstractNotification implements Serializable {
    
    public enum Priority {
        HIGH, MEDIUM, LOW;
        
        /**
         * Returns the priority associated with the input value.
         * 
         * @param value The input value. May either be numeric, or the priority name.
         * @return The corresponding priority.
         */
        public static Priority fromString(String value) {
            if (StringUtils.isNumeric(value)) {
                return Priority.values()[NumUtil.enforceRange(NumberUtils.toInt(value) - 1, 0, 2)];
            } else {
                return valueOf(value);
            }
        }
        
    };
    
    private static final long serialVersionUID = 1L;
    
    private String patientName;
    
    private String subject;
    
    private FMDate deliveryDate;
    
    private String senderName;
    
    private String[] extraInfo = {};
    
    private List<String> message;
    
    /**
     * Returns true if this notification has an associated action.
     * 
     * @return True if actionable.
     */
    public abstract boolean isActionable();
    
    /**
     * Returns true if this notification can be deleted.
     * 
     * @return True if notification can be deleted.
     */
    public abstract boolean canDelete();
    
    /**
     * Returns the priority of this notification.
     * 
     * @return Notification priority.
     */
    public abstract Priority getPriority();
    
    /**
     * Sets the priority of this notification.
     * 
     * @param priority Notification priority.
     */
    public abstract void setPriority(Priority priority);
    
    /**
     * Returns the DFN of the associated patient.
     * 
     * @return DFN of the associated patient (null if no patient association).
     */
    public abstract String getDfn();
    
    /**
     * Sets the DFN of the associated patient.
     * 
     * @param dfn DFN of the associated patient (null if no patient association).
     */
    public abstract void setDfn(String dfn);
    
    /**
     * Returns the name of the associated patient.
     * 
     * @return Name of the associated patient (null if no patient association).
     */
    public String getPatientName() {
        return patientName;
    }
    
    /**
     * Sets the name of the associated patient.
     * 
     * @param patientName Name of the associated patient (null if no patient association).
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    /**
     * Returns true if the notification has a patient association.
     * 
     * @return True if there is a patient association.
     */
    public boolean hasPatient() {
        return !StringUtils.isEmpty(getDfn());
    }
    
    /**
     * Returns the subject line of this notification.
     * 
     * @return Subject line.
     */
    public String getSubject() {
        return subject;
    }
    
    /**
     * Sets the subject line of this notification.
     * 
     * @param subject Subject line.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * Returns the delivery date of this notification.
     * 
     * @return Delivery date.
     */
    public FMDate getDeliveryDate() {
        return deliveryDate;
    }
    
    /**
     * Sets the delivery date of this notification.
     * 
     * @param deliveryDate Delivery date.
     */
    public void setDeliveryDate(FMDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    /**
     * Returns the name of the sender of this notification.
     * 
     * @return Sender name.
     */
    public String getSenderName() {
        return senderName;
    }
    
    /**
     * Sets the name of the sender of this notification.
     * 
     * @param senderName Sender name.
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    /**
     * Returns extra information associated with this notification. Format is type-specific.
     * 
     * @return Extra information.
     */
    public String[] getExtraInfo() {
        return extraInfo;
    }
    
    /**
     * Sets extra information associated with this notification. Format is type-specific.
     * 
     * @param extraInfo Extra information.
     */
    public void setExtraInfo(String[] extraInfo) {
        this.extraInfo = extraInfo;
    }
    
    /**
     * Returns the message associated with this notification.
     * 
     * @return The associated message. May be null.
     */
    public List<String> getMessage() {
        return message;
    }
    
    /**
     * Sets the message associated with this notification.
     * 
     * @param message The associated message. May be null.
     */
    public void setMessage(List<String> message) {
        this.message = message;
    }
    
    /**
     * Locate a parameter from name/value pairs in extra info.
     * 
     * @param param Parameter name.
     * @return Index of parameter in extra info, or -1 if not found.
     */
    public int findParam(String param) {
        param += "=";
        
        for (int i = 0; i < extraInfo.length; i++) {
            if (extraInfo[i].startsWith(param)) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Extract a parameter from name/value pairs in extra info.
     * 
     * @param param Parameter name.
     * @return Parameter value, or null if not found.
     */
    public String getParam(String param) {
        int i = findParam(param);
        return i < 0 ? null : StrUtil.piece(extraInfo[i], "=", 2);
    }
    
    /**
     * Sets the value for a parameter in extra info.
     * 
     * @param param Parameter name.
     * @param value Parameter value (null to remove).
     * @return Index of parameter in extra info.
     */
    public int setParam(String param, Object value) {
        int i = findParam(param);
        
        if (value == null && i < 0) {
            return i;
        }
        
        if (i < 0) {
            i = extraInfo.length;
            extraInfo = Arrays.copyOf(extraInfo, i + 1);
        }
        
        if (value == null) {
            extraInfo = (String[]) ArrayUtils.remove(extraInfo, i);
        } else {
            extraInfo[i] = param + "=" + value;
        }
        
        return i;
    }
    
    /**
     * Returns the display text for this notification.
     * 
     * @return Display text.
     */
    public String getDisplayText() {
        StringBuilder sb = new StringBuilder();
        appendText(sb, getPatientName(), "patient");
        appendText(sb, getSubject(), "subject");
        appendText(sb, getSenderName(), "sender");
        appendText(sb, DateUtil.formatDate(getDeliveryDate()), "delivered");
        appendText(sb, getPriority().toString(), "priority");
        appendText(sb, "dummy", isActionable() ? "actionable" : "infoonly");
        appendText(sb, "dummy", canDelete() ? "delete.yes" : "delete.no");
        
        if (message != null && !message.isEmpty()) {
            sb.append("\n");
            
            for (String text : message) {
                sb.append(text).append("\n");
            }
        }
        return sb.toString();
    }
    
    /**
     * Appends a text element if it is not null or empty.
     * 
     * @param sb String builder.
     * @param text Text value to append.
     * @param format Format specifier.
     */
    private void appendText(StringBuilder sb, String text, String format) {
        if (text != null && !text.isEmpty()) {
            format = "@vistanotification.detail." + format + ".label";
            sb.append(StrUtil.formatMessage(format, text)).append("\n");
        }
    }
    
}
