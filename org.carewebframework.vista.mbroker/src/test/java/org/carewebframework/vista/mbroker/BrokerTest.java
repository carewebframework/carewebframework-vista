/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.mbroker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.carewebframework.vista.mbroker.BrokerSession.IAsyncRPCEvent;
import org.carewebframework.vista.mbroker.BrokerSession.SerializationMethod;
import org.carewebframework.vista.mbroker.PollingThread.IHostEventHandler;
import org.carewebframework.vista.mbroker.Security.AuthResult;
import org.carewebframework.vista.mbroker.Security.AuthStatus;
import org.junit.Test;

public class BrokerTest implements IHostEventHandler, IAsyncRPCEvent {
    
    public static class TestBean implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        private int intValue;
        
        private String strValue;
        
        public TestBean() {
            
        }
        
        public TestBean(int intValue, String strValue) {
            this.intValue = intValue;
            this.strValue = strValue;
        }
        
        public int getIntValue() {
            return intValue;
        }
        
        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }
        
        public String getStrValue() {
            return strValue;
        }
        
        public void setStrValue(String strValue) {
            this.strValue = strValue;
        }
    }
    
    private int asyncHandle;
    
    private int eventsSent = 2;
    
    private final TestBean testBean = new TestBean(123, "test");
    
    @Test
    public void testConnection() throws Exception {
        String server = System.getenv("cwf_test_server");
        assertTrue("Environment variable 'cwf_test_server' not set.", server != null);
        CipherRegistry.registerCiphers(BrokerTest.class.getResourceAsStream("/mbroker.ciphers"));
        BrokerSession session = getConnection(server);
        AuthResult authResult = session.connect();
        assertEquals(AuthStatus.SUCCESS, authResult.status);
        assertTrue(session.isConnected());
        assertTrue(session.isAuthenticated());
        print("Connected to " + session.getConnectionParams());
        session.addHostEventHandler(this);
        List<String> results = new ArrayList<String>();
        session.callRPCList("XWB EGCHO LIST", results);
        assertList(results);
        session.callRPCList("XWB EGCHO MEMO", results, results);
        assertEquals("DHCP RECEIVED:", results.get(0));
        results.remove(0);
        assertList(results);
        session.eventSubscribe("test", true);
        session.setSerializationMethod(SerializationMethod.JSON);
        session.fireRemoteEvent("test", testBean, (String) null);
        session.setSerializationMethod(SerializationMethod.JAVA);
        session.fireRemoteEvent("test", testBean, (String) null);
        asyncHandle = session.callRPCAsync("XWB EGCHO LIST", this);
        print("Async RPC Handle: " + asyncHandle);
        int tries = 30;
        
        while (tries-- > 0 && (asyncHandle != 0 || eventsSent > 0)) {
            Thread.sleep(1000);
        }
        
        assertTrue("Host event failed - is TaskMan running?", eventsSent == 0);
        assertTrue("Async RPC failed - is TaskMan running?", asyncHandle == 0);
        session.disconnect();
    }
    
    private void assertList(List<String> list) {
        assertTrue(list.size() == 28);
        
        for (int i = 0; i < 28; i++) {
            assertEquals("List Item #" + (i + 1), list.get(i));
        }
    }
    
    public BrokerSession getConnection(String params) throws Exception {
        ConnectionParams connectionParams = new ConnectionParams(params);
        System.out.println("Requesting connection from " + connectionParams);
        return new BrokerSession(connectionParams);
    }
    
    @Override
    public void onRPCComplete(int handle, String data) {
        assertTrue(handle == asyncHandle);
        asyncHandle = 0;
        print("Async Success: " + handle);
        print(data);
    }
    
    @Override
    public void onRPCError(int handle, int code, String text) {
        print("Async Error: " + code);
        print(text);
    }
    
    @Override
    public void onHostEvent(String name, Object data) {
        assertEquals("test", name);
        assertTrue(data instanceof TestBean);
        TestBean bean = (TestBean) data;
        assertEquals(testBean.intValue, bean.intValue);
        assertEquals(testBean.strValue, bean.strValue);
        print("Host Event Name: " + name);
        print("Host Event Data: " + data);
        eventsSent--;
    }
    
    private void print(Object object) {
        System.out.println(object);
    }
    
}
