/*
 * #%L
 * carewebframework
 * %%
 * Copyright (C) 2008 - 2017 Regenstrief Institute, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.carewebframework.org/licensing/disclaimer.
 *
 * #L%
 */
package org.carewebframework.vista.api.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.List;

import org.carewebframework.api.domain.IDomainFactory;
import org.carewebframework.api.test.CommonTest;
import org.hl7.fhir.dstu3.model.BaseResource;
import org.hl7.fhir.dstu3.model.Binary;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.UriType;
import org.hspconsortium.cwf.api.ClientUtil;
import org.hspconsortium.cwf.api.DomainFactory;
import org.junit.Test;

import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.client.IGenericClient;

public class FhirTest extends CommonTest {

    private static final String ROOT = "http://broker/STU3/";

    private static final String[] PAT_IDS = { "1", "2" };

    @Test
    public void test() throws URISyntaxException {
        IDomainFactory<BaseResource> factory = DomainFactory.getInstance();
        Patient pat1 = factory.fetchObject(Patient.class, "1");
        assertNotNull(pat1);
        for (Extension extension : pat1.getExtension()) {
            System.out.println(extension.getUrl() + "=" + extension.getValue());
        }
        List<Patient> patients = factory.fetchObjects(Patient.class, PAT_IDS);
        assertNotNull(patients);
        assertEquals(2, patients.size());
        DocumentReference dr = factory.fetchObject(DocumentReference.class, "1");
        assertNotNull(dr);
        UriType uri = dr.getContentFirstRep().getAttachment().getUrlElement();
        IGenericClient client = ClientUtil.getFhirClient();
        Binary result = client.read(Binary.class, uri.toString());
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
