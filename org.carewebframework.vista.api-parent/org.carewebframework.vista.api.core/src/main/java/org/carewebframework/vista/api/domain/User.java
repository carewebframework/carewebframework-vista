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

import org.carewebframework.api.domain.IUser;
import org.carewebframework.common.JSONUtil;

public class User extends Person implements IUser {
    
    private static final long serialVersionUID = 1L;
    
    static {
        JSONUtil.registerAlias("User", User.class);
    }
    
    private String username;
    
    protected User() {
        super();
    }
    
    public User(long id) {
        super(id);
    }
    
    @Override
    public void setUsername(String username) {
        this.username = username;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
}
