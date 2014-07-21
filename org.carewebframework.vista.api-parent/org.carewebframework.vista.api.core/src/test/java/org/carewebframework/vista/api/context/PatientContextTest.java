/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.context;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.carewebframework.api.context.ContextMarshaller;
import org.carewebframework.api.context.UserContext;
import org.carewebframework.api.test.CommonTest;
import org.carewebframework.cal.api.context.PatientContext;
import org.carewebframework.cal.api.context.PatientContext.IPatientContextEvent;
import org.carewebframework.fhir.common.FhirUtil;
import org.carewebframework.fhir.model.core.DateAndTime;
import org.carewebframework.fhir.model.resource.Patient;
import org.carewebframework.fhir.model.resource.User;
import org.carewebframework.fhir.model.type.HumanName;
import org.carewebframework.fhir.model.type.Identifier;

import org.junit.Ignore;
import org.junit.Test;

public class PatientContextTest extends CommonTest {
    
    /**
     * Should accept context change on first survey and refuse on all subsequent surveys.
     */
    private class ContextChangeSubscriber implements IPatientContextEvent {
        
        private String reason = "";
        
        @Override
        public void canceled() {
        }
        
        @Override
        public void committed() {
        }
        
        @Override
        public String pending(boolean silent) {
            String result = reason;
            reason = "refuse change";
            return result;
        }
        
    }
    
    DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
    
    @Test
    public void changePatientContext() throws Exception {
        changeUserContext();
        Patient patient1 = new Patient();
        patient1.setDomainId("321");
        patient1.addName(new HumanName("Smith, Joe"));
        Identifier ssn = new Identifier();
        ssn.setLabelSimple("SSN");
        ssn.setValueSimple("999-99-9999");
        patient1.addIdentifier(ssn);
        patient1.setBirthDateSimple(new DateAndTime("1958-07-27"));
        Patient patient2 = new Patient();
        patient2.setDomainId("123");
        patient2.addName(new HumanName("Doe, Jane"));
        Identifier ssn2 = new Identifier();
        ssn2.setLabelSimple("SSN");
        ssn2.setValueSimple("123-45-6789");
        patient2.addIdentifier(ssn2);
        patient2.setBirthDateSimple(new DateAndTime("1963-5-1"));
        Object subscriber = new ContextChangeSubscriber(); // Create a patient context change subscriber
        appFramework.registerObject(subscriber); // Register it with the context manager
        PatientContext.changePatient(patient1); // Request a context change
        assertSame(patient1, PatientContext.getActivePatient()); // This time should succeed
        PatientContext.changePatient(patient2); // Reattempt the context change
        assertNotSame(patient2, PatientContext.getActivePatient()); // Subscriber should have refused the context change
        appFramework.unregisterObject(subscriber); // Unregister the subscriber
        PatientContext.changePatient(patient2); // Reattempt the context change
        assertSame(patient2, PatientContext.getActivePatient()); // This time should succeed
    }
    
    public void changeUserContext() throws Exception {
        User user = new User();
        user.setDomainId("999");
        UserContext.changeUser(user);
    }
    
    @Test
    @Ignore
    public void marshalling() throws Exception {
        changePatientContext();
        ContextMarshaller marshaller = contextManager.getContextMarshaller("keystore-test");
        String ctx = marshaller.marshal(contextManager.getMarshaledContext());
        String sig = marshaller.sign(ctx);
        PatientContext.changePatient(null);
        assertNull(PatientContext.getActivePatient());
        marshaller.unmarshal(ctx, sig);
        Patient patient = PatientContext.getActivePatient();
        assertTrue("Doe, Jane".equalsIgnoreCase(FhirUtil.formatName(patient.getName())));
    }
    
}
