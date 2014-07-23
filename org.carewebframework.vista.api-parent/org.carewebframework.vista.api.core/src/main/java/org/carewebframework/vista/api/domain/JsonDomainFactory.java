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

import java.util.Collections;
import java.util.List;

import org.carewebframework.api.domain.IDomainFactory;
import org.carewebframework.api.domain.IDomainObject;
import org.carewebframework.common.JSONUtil;
import org.carewebframework.vista.api.util.VistAUtil;

/**
 * Factory for instantiating serialized domain objects from server.
 */
public class JsonDomainFactory implements IDomainFactory<IDomainObject> {
    
    private static final IDomainFactory<IDomainObject> instance = new JsonDomainFactory();
    
    public static IDomainFactory<IDomainObject> getInstance() {
        return instance;
    }
    
    private JsonDomainFactory() {
        
    }
    
    @Override
    public IDomainObject newObject(Class<IDomainObject> clazz) {
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
    public IDomainObject fetchObject(Class<IDomainObject> clazz, String id) {
        if (!VistAUtil.validateIEN(id)) {
            return null;
        }
        
        String json = VistAUtil.getBrokerSession().callRPC("RGCWFSER GETBYIEN", getAlias(clazz), id);
        return (IDomainObject) JSONUtil.deserialize(json);
    }
    
    /**
     * Fetch a keyed instance of the domain class from the data store.
     */
    @Override
    public IDomainObject fetchObject(Class<IDomainObject> clazz, String key, String table) {
        String json = VistAUtil.getBrokerSession().callRPC("RGCWFSER GETBYKEY", getAlias(clazz), key, table);
        return (IDomainObject) JSONUtil.deserialize(json);
    }
    
    /**
     * Fetch multiple instances of the domain class from the data store.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<IDomainObject> fetchObjects(Class<IDomainObject> clazz, String[] ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }
        
        String json = VistAUtil.getBrokerSession().callRPC("RGCWFSER GETBYIEN", getAlias(clazz), ids);
        return (List<IDomainObject>) JSONUtil.deserialize(json);
    }
    
    /**
     * Returns the alias for the domain class. A domain class will typically register its alias in a
     * static initializer block. If the initial attempt to retrieve an alias fails, this method
     * forces the class loader to load the class to ensure that any static initializers are
     * executed, and then tries again.
     *
     * @param clazz Domain class whose alias is sought.
     * @return The alias for the domain class.
     */
    @Override
    public String getAlias(Class<?> clazz) {
        // Locate the alias of the requested class.
        String alias = JSONUtil.getAlias(clazz);
        
        // If not found, ensure that the class has been loaded to allow for its
        // static initializers to execute, then try again.
        if (alias == null) {
            try {
                Class.forName(clazz.getName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            alias = JSONUtil.getAlias(clazz);
        }
        
        return alias;
    }
    
}
