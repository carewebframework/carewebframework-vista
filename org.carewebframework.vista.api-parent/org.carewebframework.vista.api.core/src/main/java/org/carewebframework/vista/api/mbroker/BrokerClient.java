/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.mbroker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import org.carewebframework.vista.api.util.VistAUtil;

/**
 * Allows http requests to be handled by the broker.
 */
@SuppressWarnings("deprecation")
public class BrokerClient extends CloseableHttpClient {
    
    @Override
    public HttpParams getParams() {
        return null;
    }
    
    @Override
    public ClientConnectionManager getConnectionManager() {
        return null;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException,
                                                                                                        ClientProtocolException {
        List<String> data = new ArrayList<String>();
        String[] requestLine = request.getRequestLine().toString().split("\\ ", 3);
        String uri = requestLine[1];
        
        if (uri.startsWith("http://")) {
            uri = uri.substring(7);
        }
        
        String[] pcs = uri.split("\\/", 2);
        data.add(requestLine[0] + " " + pcs[1] + " " + requestLine[2]);
        
        for (Header header : request.getAllHeaders()) {
            data.add(header.getName() + ": " + header.getValue());
        }
        
        data.add("Host: " + pcs[0]);
        data.add("");
        List<String> response = VistAUtil.getBrokerSession().callRPCList("RGSER REST", null, data);
        return new BrokerResponse(response);
    }
    
}
