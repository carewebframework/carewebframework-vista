/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.smart.problem;

import java.util.Map;

import org.carewebframework.vista.smart.AbstractAPIBase;

import org.carewebframework.smart.rdf.RDFDocument;

public class SmartProblems extends AbstractAPIBase {
    
    private static final String ICD = "http://purl.bioontology.org/ontology/ICD-9/";
    
    public SmartProblems() {
        super("/records/{record_id}/problems", "Problems");
    }
    
    @Override
    public void handleAPI(RDFDocument doc, Map<String, String> params) {
    }
    
}
