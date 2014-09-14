/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.security.mock;

import java.util.List;

import ca.uhn.fhir.model.dstu.resource.User;

import org.carewebframework.fhir.common.FhirUtil;
import org.carewebframework.security.spring.CWFAuthenticationDetails;
import org.carewebframework.vista.security.base.BaseAuthenticationProvider;

/**
 * Provides authentication support for the framework. Takes provided authentication credentials and
 * authenticates them against the database.
 */
public final class AuthenticationProvider extends BaseAuthenticationProvider {
    
    public AuthenticationProvider() {
        super(true);
    }
    
    public AuthenticationProvider(List<String> grantedAuthorities) {
        super(grantedAuthorities);
    }
    
    /**
     * Performs a user login.
     *
     * @param details Authentication details
     * @param username Username for the login.
     * @param password Password for the login (ignored if the user is pre-authenticated).
     * @param domain Domain for which the login is requested.
     * @return Authorization result
     */
    @Override
    protected User login(CWFAuthenticationDetails details, String username, String password, String domain) {
        User user = new User();
        user.setId("1");
        user.setName(FhirUtil.parseName("USER,MOCK"));
        user.setLogin(username);
        details.setDetail("user", user);
        return user;
    }
    
}
