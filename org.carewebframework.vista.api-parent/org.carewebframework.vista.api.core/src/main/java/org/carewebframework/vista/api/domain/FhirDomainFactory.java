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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.carewebframework.api.domain.IDomainFactory;
import org.carewebframework.common.StrUtil;
import org.carewebframework.fhir.common.FhirClient;
import org.carewebframework.fhir.common.FhirUtil;
import org.carewebframework.fhir.format.xml.XmlParser;
import org.carewebframework.fhir.model.core.ResourceOrFeed;
import org.carewebframework.fhir.model.resource.Resource;
import org.carewebframework.vista.api.mbroker.BrokerRequestFactory;
import org.carewebframework.vista.api.util.VistAUtil;

/**
 * Factory for instantiating serialized domain objects from server.
 */
public class FhirDomainFactory implements IDomainFactory<Resource> {
    
    private static final IDomainFactory<Resource> instance = new FhirDomainFactory();
    
    private static final String REST_ROOT = "broker://RGCWFHIR+REST/";
    
    static {
        FhirClient.getInstance().registerClientHttpRequestFactory("broker://*", new BrokerRequestFactory());
    }
    
    public static IDomainFactory<Resource> getInstance() {
        return instance;
    }
    
    public static ResourceOrFeed parse(String fhir) {
        try {
            return new XmlParser().parseGeneral(IOUtils.toInputStream(fhir, FhirUtil.UTF8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Resource> T parse(String fhir, Class<T> clazz) {
        try {
            return (T) new XmlParser().parse(IOUtils.toInputStream(fhir, FhirUtil.UTF8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Create a new instance of the domain class.
     */
    @Override
    public Resource newObject(Class<Resource> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Fetch an instance of the domain class from the data store.
     */
    @Override
    public Resource fetchObject(Class<Resource> clazz, String id) {
        if (!VistAUtil.validateIEN(id)) {
            return null;
        }
        
        return FhirClient.getInstance().get(REST_ROOT + getAlias(clazz) + "/" + id).getResource();
    }
    
    /**
     * Fetch a keyed instance of the domain class from the data store.
     */
    @Override
    public Resource fetchObject(Class<Resource> clazz, String key, String table) {
        String fhir = VistAUtil.getBrokerSession().callRPC("RGCWFHIR GETBYKEY", getAlias(clazz), key, table);
        return parse(fhir).getResource();
    }
    
    /**
     * Fetch multiple instances of the domain class from the data store.
     */
    @Override
    public List<Resource> fetchObjects(Class<Resource> clazz, String[] ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }
        
        String qs = "?_id=" + StrUtil.fromList(Arrays.asList(ids), ",");
        return FhirUtil.getEntries(FhirClient.getInstance().get(REST_ROOT + getAlias(clazz) + qs).getFeed(), clazz);
    }
    
    /**
     * Returns the alias for the domain class.
     *
     * @param clazz Domain class whose alias is sought.
     * @return The alias for the domain class.
     */
    @Override
    public String getAlias(Class<?> clazz) {
        return Resource.class.isAssignableFrom(clazz) ? clazz.getSimpleName().replace("_", "") : null;
    }
    
}
