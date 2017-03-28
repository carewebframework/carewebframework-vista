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

import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.api.domain.IUser;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.api.util.VistAUtil;

/**
 * Notification recipient.
 */
public class Recipient {
    
    private final String name;
    
    private final long ien;
    
    /**
     * Creates a recipient based on the specified user.
     *
     * @param user A user.
     */
    public Recipient(IUser user) {
        this.name = user.getFullName();
        this.ien = VistAUtil.parseIEN(user.getLogicalId());
    }
    
    /**
     * Creates a recipient based on raw data.
     *
     * @param data Raw data.
     * @param isGroup If true, the raw data represents a group. If false, the type of recipient is
     *            to be inferred from the raw data.
     */
    public Recipient(String data, boolean isGroup) {
        String[] pcs = StrUtil.split(data, StrUtil.U, 2);
        long val = NumberUtils.toLong(pcs[0]);
        this.ien = isGroup && val > 0 ? -val : val;
        this.name = pcs[1];
    }
    
    /**
     * Creates a recipient based on raw data.
     *
     * @param data Raw data.
     */
    public Recipient(String data) {
        this(data, false);
    }
    
    /**
     * Returns true if this recipient is a mail group.
     *
     * @return True if a mail group.
     */
    public boolean isGroup() {
        return ien < 0;
    }
    
    /**
     * Returns the recipient name.
     *
     * @return The recipient name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the recipient's internal entry number. Note that a negative value means that this is
     * a mail group, while a positive value means it is a user.
     *
     * @return Recipient's IEN.
     */
    public long getIen() {
        return ien;
    }
    
};
