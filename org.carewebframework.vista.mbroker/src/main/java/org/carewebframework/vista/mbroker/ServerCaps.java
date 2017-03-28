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

import java.util.List;

import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.mbroker.BrokerSession.AuthMethod;

/**
 * Server capabilities descriptor.
 */
public class ServerCaps implements Cloneable {
    
    private AuthMethod authMethod = AuthMethod.Normal;
    
    private final Version serverVersion;
    
    private final boolean caseSensitivePassword;
    
    boolean debugMode;
    
    String domainName;
    
    String siteName;
    
    private final String cipherKey;
    
    private final List<String> preLoginMessage;
    
    public ServerCaps(String init) {
        List<String> data = StrUtil.toList(init, null, Constants.LINE_SEPARATOR);
        String[] pcs = StrUtil.split(data.get(0), StrUtil.U, 6, true);
        debugMode = StrUtil.toBoolean(pcs[0]);
        authMethod = AuthMethod.values()[StrUtil.toInt(pcs[1])];
        serverVersion = new Version(pcs[2]);
        caseSensitivePassword = StrUtil.toBoolean(pcs[3]);
        cipherKey = pcs[4];
        data.remove(0);
        preLoginMessage = data;
    }
    
    public AuthMethod getAuthMethod() {
        return authMethod;
    }
    
    public Version getServerVersion() {
        return serverVersion;
    }
    
    public boolean isCaseSensitivePassword() {
        return caseSensitivePassword;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public String getDomainName() {
        return domainName;
    }
    
    public String getSiteName() {
        return siteName;
    }
    
    public String getCipherKey() {
        return cipherKey;
    }
    
    public List<String> getPreLoginMessage() {
        return preLoginMessage;
    }
    
}
