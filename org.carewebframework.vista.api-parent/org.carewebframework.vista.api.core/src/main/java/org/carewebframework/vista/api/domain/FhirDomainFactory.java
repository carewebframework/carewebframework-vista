/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.domain;

import org.carewebframework.api.domain.IDomainFactory;
import org.carewebframework.fhir.common.FhirClient;
import org.carewebframework.fhir.model.resource.Resource;
import org.carewebframework.vista.api.mbroker.BrokerRequestFactory;
import org.carewebframework.vista.api.util.VistAUtil;

/**
 * Factory for instantiating serialized domain objects from server.
 */
public class FhirDomainFactory extends org.carewebframework.cal.api.domain.FhirDomainFactory {
    
    private static final IDomainFactory<Resource> instance = new FhirDomainFactory();
    
    static {
        FhirClient.getInstance().registerClientHttpRequestFactory("broker://*", new BrokerRequestFactory());
    }
    
    public static IDomainFactory<Resource> getInstance() {
        return instance;
    }
    
    /**
     * Fetch a keyed instance of the domain class from the data store.
     */
    @Override
    public Resource fetchObject(Class<Resource> clazz, String key, String table) {
        String fhir = VistAUtil.getBrokerSession().callRPC("RGCWFHIR GETBYKEY", getAlias(clazz), key, table);
        return parse(fhir).getResource();
    }
    
}
