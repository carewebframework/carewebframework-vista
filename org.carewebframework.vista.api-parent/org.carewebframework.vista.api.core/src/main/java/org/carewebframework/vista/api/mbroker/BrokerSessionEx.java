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
package org.carewebframework.vista.api.mbroker;

import org.carewebframework.api.alias.AliasType;
import org.carewebframework.api.alias.AliasTypeRegistry;
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
