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
package org.carewebframework.vista.plugin.encounter;

import org.carewebframework.ui.zk.ZKUtil;

/**
 * Encounter context-related constants.
 */
public class Constants {
    
    public static final String RESOURCE_PREFIX = ZKUtil.getResourcePath(Constants.class);
    
    public static final String SCLASS_LOCKED = "glyphicon glyphicon-lock text-danger";
    
    public static final String SCLASS_PRIMARY = "encounter-primary-provider";
    
    public static final int ENCOUNTER_DAYS = 366; // Days back to search for existing encounters.
    
    public static final String TX_NO_DAT = "Encounter Date";
    
    public static final String TX_NO_LOC = "Encounter Location";
    
    public static final String TX_NO_PRI = "Primary Provider";
    
    public static final String TX_NO_PRV = "Default Provider";
    
    public static final String TX_NO_CAT = "Service Category";
    
    public static final String TX_NO_LCK = "The selected visit is locked from further changes.\n"
            + "The requested operation requires a visit that is not locked.";
    
    public static final String TX_NO_KEY = "The selected provider does not hold the PROVIDER security key.\n"
            + "The requested operation requires this.";
    
    public static final String TX_NO_FUT = "You may not create a visit with a future encounter date.";
    
    public static final String TX_MISSING = "The following items were not specified:\n";
    
    /**
     * Enforce static class.
     */
    private Constants() {
    };
}
