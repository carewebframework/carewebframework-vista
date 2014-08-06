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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;

import org.carewebframework.api.test.CommonTest;
import org.carewebframework.fhir.common.FhirClient;
import org.carewebframework.fhir.model.core.ResourceOrFeed;
import org.carewebframework.fhir.model.resource.Binary;
import org.carewebframework.fhir.model.resource.DocumentReference;
import org.carewebframework.fhir.model.type.Base64Binary;
import org.carewebframework.fhir.model.type.Uri;
import org.carewebframework.vista.api.mbroker.BrokerRequestFactory;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import org.junit.Test;

public class FhirTest extends CommonTest {
    
    private static final String ROOT = "broker://RGCWSER+REST/FHIR/";
    
    @Test
    public void test() throws URISyntaxException {
        FhirClient rest = FhirClient.getInstance();
        rest.registerClientHttpRequestFactory("broker://*", new BrokerRequestFactory());
        ResourceOrFeed result = rest.get(ROOT + "Patient/1");
        assertNotNull(result.getResource());
        result = rest.get(ROOT + "Patient?_id=1,2");
        assertNotNull(result.getFeed());
        result = rest.get(ROOT + "DocumentReference/1");
        assertNotNull(result.getResource());
        Uri uri = ((DocumentReference) result.getResource()).getLocation();
        result = rest.get(uri.getValue());
        assertNotNull(result.getResource());
        Base64Binary text = ((Binary) result.getResource()).getContent();
        System.out.println(new String(text.getValue()));
        result = rest.get(ROOT + "Condition/1");
        assertNotNull(result.getResource());
        testException(ROOT + "ICD9/1", HttpStatus.FORBIDDEN);
        testException(ROOT + "xxxxxx/1", HttpStatus.NOT_FOUND);
    }
    
    private void testException(String url, HttpStatus status) {
        try {
            FhirClient.getInstance().get(url);
            fail("Expected exception not thrown.");
        } catch (HttpClientErrorException e) {
            assertEquals(status, e.getStatusCode());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }
    
}
