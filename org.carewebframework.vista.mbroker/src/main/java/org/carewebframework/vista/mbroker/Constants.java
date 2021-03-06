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

import java.nio.charset.Charset;

public class Constants {
    
    public static final byte EOD = -1;
    
    public static final String VERSION = "1.0";
    
    public static final String DEFAULT_APP_ID = "XWB RPC TEST";
    
    public static final Charset UTF8 = Charset.forName("UTF-8");
    
    public static final String LINE_SEPARATOR = "\r";
    
    /**
     * Enforce static class.
     */
    private Constants() {
    };
}
