/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.carewebframework.api.test.CommonTest;
import org.carewebframework.cal.api.domain.Name;
import org.carewebframework.common.JSONUtil;
import org.carewebframework.vista.api.domain.DomainObjectFactory;
import org.carewebframework.vista.api.domain.Person;
import org.carewebframework.vista.api.domain.User;
import org.carewebframework.vista.api.property.PropertyDefinition;
import org.carewebframework.vista.mbroker.FMDate;

import org.junit.Test;

public class SerializationTest extends CommonTest {

    private static final String PERSON_DATA = "{\"@class\": \"Person\"," + "\"domainId\": 1234,"
            + "\"birthDate\": \"2580727\"," + "\"name\":{" + "\"firstName\":\"Douglas\"," + "\"middleName\":\"Kent\","
            + "\"lastName\":\"Martin\"}" + "}";

    @Test
    public void testDeserializer() {
        new FMDate();
        JSONUtil.registerAlias("Person", Person.class);
        JSONUtil.registerAlias("Name", Name.class);
        Person person = (Person) JSONUtil.deserialize(PERSON_DATA);
        assertEquals("1234", person.getDomainId());
        assertEquals("Martin, Douglas Kent", person.getFullName());
        assertEquals("2580727", (new FMDate(person.getBirthDate())).getFMDate());
    }

    @Test
    public void testFactory() throws Exception {
        User user = DomainObjectFactory.get(User.class, "1");
        assertEquals("1", user.getDomainId());
        List<User> users = DomainObjectFactory.get(User.class, new String[] { "1" });
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    public void testProperty() throws Exception {
        PropertyDefinition def = PropertyDefinition.get("ORB OI ORDERED - INPT");
        assertTrue(def.getName().equals("ORB OI ORDERED - INPT"));
    }
}
