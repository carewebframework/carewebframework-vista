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

import org.carewebframework.fhir.model.core.IReferenceable;
import org.carewebframework.fhir.model.type.Reference;

/**
 * Abstract base class for domain objects.
 */
public abstract class DomainObject implements IReferenceable {
    
    private final Reference reference = new Reference();
    
    public DomainObject() {
        super();
    }
    
    public DomainObject(String logicalId) {
        super();
        setLogicalId(logicalId);
    }
    
    @Override
    public String getLogicalId() {
        return reference.getLogicalId();
    }
    
    @Override
    public String getUniversalId() {
        return reference.getUniversalId();
    }
    
    public void setLogicalId(String logicalId) {
        reference.setUniversalId(getClass().getSimpleName() + "/" + logicalId);
    }
    
}
