/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at 
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.mbroker;

import org.carewebframework.api.AliasTypeRegistry;
import org.carewebframework.api.AliasTypeRegistry.AliasType;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.RPCParameters;

/**
 * This subclass exists to allow transparent use of RPC aliases in broker calls.
 */
public class BrokerSessionEx extends BrokerSession {
    
    private final AliasType rpcAliasType = AliasTypeRegistry.getType("RPC");
    
    @Override
    public String callRPC(String name, boolean async, int timeout, RPCParameters params) {
        String alias = rpcAliasType.get(name);
        return super.callRPC(alias == null ? name : alias, async, timeout, params);
    }
    
}
