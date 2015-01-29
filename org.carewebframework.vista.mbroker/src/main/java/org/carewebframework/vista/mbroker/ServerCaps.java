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
    
    private final boolean contextCached;
    
    boolean concurrentMode;
    
    String domainName;
    
    String siteName;
    
    private final String cipherKey;
    
    private final List<String> preLoginMessage;
    
    public ServerCaps(String init) {
        List<String> data = StrUtil.toList(init, null, Constants.LINE_SEPARATOR);
        String[] pcs = StrUtil.split(data.get(0), StrUtil.U, 6, true);
        concurrentMode = StrUtil.toBoolean(pcs[0]);
        authMethod = AuthMethod.values()[StrUtil.toInt(pcs[1])];
        serverVersion = new Version(pcs[2]);
        caseSensitivePassword = StrUtil.toBoolean(pcs[3]);
        contextCached = StrUtil.toBoolean(pcs[4]);
        cipherKey = pcs[5];
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
    
    public boolean isContextCached() {
        return contextCached;
    }
    
    public boolean isConcurrentMode() {
        return concurrentMode;
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
