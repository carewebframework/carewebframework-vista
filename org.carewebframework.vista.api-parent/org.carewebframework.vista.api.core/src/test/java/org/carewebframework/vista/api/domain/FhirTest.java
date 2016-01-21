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
import java.util.List;

import org.carewebframework.api.domain.IDomainFactory;
import org.carewebframework.api.test.CommonTest;
import org.carewebframework.cal.api.ClientUtil;
import org.carewebframework.cal.api.DomainFactory;

import org.junit.Test;

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.resource.BaseResource;
import ca.uhn.fhir.model.dstu2.resource.Binary;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.client.IGenericClient;

public class FhirTest extends CommonTest {
    
    private static final String ROOT = "http://broker/DSTU2/";
    
    private static final String[] PAT_IDS = { "1", "2" };
    
    @Test
    public void test() throws URISyntaxException {
        IDomainFactory<BaseResource> factory = DomainFactory.getInstance();
        Patient pat1 = factory.fetchObject(Patient.class, "1");
        assertNotNull(pat1);
        for (ExtensionDt extension : pat1.getAllUndeclaredExtensions()) {
            System.out.println(extension.getUrlAsString() + "=" + extension.getValue());
        }
        List<Patient> patients = factory.fetchObjects(Patient.class, PAT_IDS);
        assertNotNull(patients);
        assertEquals(2, patients.size());
        DocumentReference dr = factory.fetchObject(DocumentReference.class, "1");
        assertNotNull(dr);
        UriDt uri = dr.getContentFirstRep().getAttachment().getUrlElement();
        IGenericClient client = ClientUtil.getFhirClient();
        Binary result = (Binary) client.read(uri);
        assertNotNull(result);
        byte[] text = result.getContent();
        System.out.println(new String(text));
        Condition cond = factory.fetchObject(Condition.class, "1");
        assertNotNull(cond);
        testException(client, ROOT + "Patient/309349993439");
    }
    
    private void testException(IGenericClient client, String url) {
        try {
            UriDt _url = new UriDt(url);
            client.read(_url);
            fail("Expected exception not thrown.");
        } catch (Exception e) {
        
        }
    }
    
}
