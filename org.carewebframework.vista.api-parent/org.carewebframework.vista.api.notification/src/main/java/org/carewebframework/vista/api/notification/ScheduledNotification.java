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

import static org.carewebframework.common.StrUtil.U;

import java.util.Arrays;

import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.mbroker.FMDate;

/**
 * A scheduled notification.
 */
public class ScheduledNotification extends AbstractNotification {
    
    private static final long serialVersionUID = 1L;
    
    private long ien;
    
    public ScheduledNotification() {
        
    }
    
    /**
     * Create a scheduled notification from raw data.
     * 
     * @param data Format is <code>
     *  1      2         3            4         5
     * IEN ^ Date ^ Patient Name ^ Subject ^ Extra Info
     * </code>
     */
    public ScheduledNotification(String data) {
        String[] pcs = StrUtil.split(data, U, 5);
        setIen(Long.parseLong(pcs[0]));
        setDeliveryDate(FMDate.fromString(pcs[1]));
        setPatientName(pcs[2]);
        setSubject(pcs[3]);
        setExtraInfo(Arrays.copyOfRange(pcs, 4, pcs.length));
    }
    
    /**
     * Returns the internal entry number of the notification.
     * 
     * @return The internal entry number. Will be 0 if this is a new notification.
     */
    public long getIen() {
        return ien;
    }
    
    /**
     * Sets the internal entry number of the notification.
     * 
     * @param ien The internal entry number. Will be 0 if this is a new notification.
     */
    protected void setIen(long ien) {
        this.ien = ien;
    }
    
    /**
     * Returns the priority of this notification.
     */
    @Override
    public Priority getPriority() {
        return Priority.fromString(getParam("PRI"));
    }
    
    /**
     * Sets the priority of this notification.
     */
    @Override
    public void setPriority(Priority priority) {
        setParam("PRI", priority.ordinal() + 1);
    }
    
    /**
     * Returns the internal entry number of the associated patient, or null if no associated
     * patient.
     */
    @Override
    public String getDfn() {
        return getParam("DFN");
    }
    
    /**
     * Sets the internal entry number of the associated patient. Use null if no associated patient.
     */
    @Override
    public void setDfn(String dfn) {
        setParam("DFN", dfn);
    }
    
    /**
     * Scheduled notifications are not currently actionable.
     */
    @Override
    public boolean isActionable() {
        return false;
    }
    
    /**
     * All scheduled notifications may be deleted.
     */
    @Override
    public boolean canDelete() {
        return true;
    }
    
}
