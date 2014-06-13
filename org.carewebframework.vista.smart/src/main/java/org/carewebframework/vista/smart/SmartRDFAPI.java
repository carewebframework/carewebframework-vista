/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.smart;

import java.util.Map;

import org.carewebframework.smart.rdf.RDFAPIBase;

/**
 * Base API for building RDF responses.
 */
public abstract class SmartRDFAPI extends RDFAPIBase {
    
    public SmartRDFAPI(String pattern, String capability) {
        super(pattern, capability);
    }
    
    /**
     * Validate the request.
     *
     * @param params The associated request parameters.
     * @return True if the request is valid.
     */
    @Override
    protected boolean validateRequest(Map<String, String> params) {
        return SmartAPI.validateRequest(params);
    }

}
