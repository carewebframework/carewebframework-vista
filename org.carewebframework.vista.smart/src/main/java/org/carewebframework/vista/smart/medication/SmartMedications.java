/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.smart.medication;

import java.util.Map;

import org.carewebframework.vista.smart.AbstractAPIBase;

import org.carewebframework.smart.rdf.RDFDocument;

public class SmartMedications extends AbstractAPIBase {
    
    public SmartMedications() {
        super("/records/{record_id}/medications", "Medications");
    }
    
    @Override
    public void handleAPI(RDFDocument doc, Map<String, String> params) {
    }
    
}
