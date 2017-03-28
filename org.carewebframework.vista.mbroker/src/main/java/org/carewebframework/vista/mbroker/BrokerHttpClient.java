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
package org.carewebframework.vista.mbroker;

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

/**
 * Allows HTTP requests to be handled by the broker.
 */
@SuppressWarnings("deprecation")
public class BrokerHttpClient extends CloseableHttpClient {
    
    private final BrokerSession brokerSession;
    
    public BrokerHttpClient(BrokerSession brokerSession) {
        this.brokerSession = brokerSession;
    }
    
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
            if (!"Authentication".equalsIgnoreCase(header.getName())) {
                data.add(header.getName() + ": " + header.getValue());
            }
        }
        
        data.add("Host: " + pcs[0]);
        data.add("");
        List<String> response = brokerSession.callRPCList("RGNETBRP HTTPREQ", null, data);
        return new BrokerHttpResponse(response);
    }
    
}
