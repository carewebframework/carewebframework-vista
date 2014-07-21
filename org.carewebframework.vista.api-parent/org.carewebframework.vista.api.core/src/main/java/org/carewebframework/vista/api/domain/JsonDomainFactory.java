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
public class JsonDomainFactory implements IDomainFactory {
    
    private static final IDomainFactory instance = new JsonDomainFactory();
    
    public static IDomainFactory getInstance() {
        return instance;
    }
    
    private JsonDomainFactory() {
        
    }
    
    @Override
    public <T extends IDomainObject> T newObject(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Fetch an instance of the domain class from the data store.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IDomainObject> T fetchObject(Class<T> clazz, String id) {
        if (!VistAUtil.validateIEN(id)) {
            return null;
        }
        
        String json = VistAUtil.getBrokerSession().callRPC("RGCWFSER GETBYIEN", getAlias(clazz), id);
        return (T) JSONUtil.deserialize(json);
    }
    
    /**
     * Fetch a keyed instance of the domain class from the data store.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IDomainObject> T fetchObject(Class<T> clazz, String key, String table) {
        String json = VistAUtil.getBrokerSession().callRPC("RGCWFSER GETBYKEY", getAlias(clazz), key, table);
        return (T) JSONUtil.deserialize(json);
    }
    
    /**
     * Fetch multiple instances of the domain class from the data store.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IDomainObject> List<T> fetchObjects(Class<T> clazz, String[] ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }
        
        String json = VistAUtil.getBrokerSession().callRPC("RGCWFSER GETBYIEN", getAlias(clazz), ids);
        return (List<T>) JSONUtil.deserialize(json);
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
    public String getAlias(Class<? extends IDomainObject> clazz) {
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
