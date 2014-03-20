/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at 
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.property;

import org.carewebframework.api.spring.SpringUtil;

/**
 * Static convenience class for obtaining a reference to the property data access service.
 * 
 * 
 */
public class PropertyUtil {
    
    public static IPropertyDAO getPropertyDAO() {
        return SpringUtil.getBean("propertyService", IPropertyDAO.class);
    }
    
    /**
     * Enforces static class.
     */
    private PropertyUtil() {
    };
}
