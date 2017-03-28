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

import java.net.URISyntaxException;

import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.api.test.CommonTest;
import org.carewebframework.vista.api.property.PropertyDefinition;

import org.junit.Test;

public class JsonTest extends CommonTest {
    
    @Test
    public void test() throws URISyntaxException {
        PropertyDefinition def = DomainFactoryRegistry.fetchObject(PropertyDefinition.class, "1");
        assertEquals("1", def.getId().getIdPart());
    }
    
}
