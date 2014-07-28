/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.fhir;

import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;

import org.carewebframework.api.test.CommonTest;
import org.carewebframework.fhir.common.FhirClient;
import org.carewebframework.fhir.model.core.ResourceOrFeed;
import org.carewebframework.vista.api.mbroker.BrokerRequestFactory;

import org.junit.Test;

public class FhirTest extends CommonTest {
    
    @Test
    public void test() throws URISyntaxException {
        FhirClient rest = FhirClient.getInstance();
        rest.registerClientHttpRequestFactory("broker://*", new BrokerRequestFactory());
        ResourceOrFeed result = rest.get("broker://RGCWFHIR+REST/Patient/1");
        assertNotNull(result.getResource());
        result = rest.get("broker://RGCWFHIR+REST/Patient?_id=1,2");
        assertNotNull(result.getFeed());
        result = rest.get("broker://RGCWFHIR+REST/Document/1");
        assertNotNull(result.getFeed());
        //result = rest.get("broker://RGCWFHIR+REST/Document?_id=1,2");
        //assertNotNull(result.getFeed());
    }
    
}
