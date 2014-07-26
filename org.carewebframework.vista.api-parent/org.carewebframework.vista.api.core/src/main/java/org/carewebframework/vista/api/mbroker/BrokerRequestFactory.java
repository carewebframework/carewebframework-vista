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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.carewebframework.vista.api.util.VistAUtil;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.util.UriUtils;

/**
 * Allows http requests to be handled by the broker.
 */
public class BrokerRequestFactory implements ClientHttpRequestFactory {
    
    /**
     * Converts an http request to a remote procedure call. The URI path represents the remote
     * procedure name.
     */
    private class BrokerRequest extends AbstractClientHttpRequest {
        
        private final URI uri;
        
        private final HttpMethod method;
        
        public BrokerRequest(URI uri, HttpMethod method) {
            this.uri = uri;
            this.method = method;
        }
        
        @Override
        public HttpMethod getMethod() {
            return method;
        }
        
        @Override
        public URI getURI() {
            return uri;
        }
        
        @Override
        protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
            // TODO Auto-generated method stub
            return null;
        }
        
        @Override
        protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
            List<String> request = new ArrayList<String>();
            String path = uri.getPath() + (uri.getQuery() == null ? "" : "?" + uri.getQuery());
            request.add(method.toString() + " " + path + " HTTP/1.0");
            
            for (Entry<String, List<String>> entry : headers.entrySet()) {
                for (String value : entry.getValue()) {
                    request.add(entry.getKey() + ": " + value);
                }
            }
            
            request.add("");
            String rpcName = UriUtils.decode(uri.getAuthority().replace("+", " "), "UTF-8");
            List<String> response = VistAUtil.getBrokerSession().callRPCList(rpcName, null, request);
            return new BrokerResponse(response);
        }
        
    }
    
    private class BrokerResponse extends AbstractClientHttpResponse {
        
        private final List<String> response;
        
        public BrokerResponse(List<String> response) {
            this.response = response;
        }
        
        @Override
        public InputStream getBody() throws IOException {
            return new InputStream() {
                
                private int index = -1;
                
                private int offset;
                
                @Override
                public int read() throws IOException {
                    if (index < 0) {
                        for (int i = 0; i < response.size(); i++) {
                            if (response.get(i).trim().isEmpty()) {
                                index = i + 1;
                                break;
                            }
                        }
                    }
                    
                    while (index >= 0 && index < response.size()) {
                        String ln = response.get(index);
                        
                        if (offset >= ln.length()) {
                            index++;
                            offset = 0;
                        } else {
                            return ln.charAt(offset++);
                        }
                    }
                    
                    return -1;
                }
                
            };
        }
        
        @Override
        public HttpHeaders getHeaders() {
            HttpHeaders headers = new HttpHeaders();
            
            for (int i = 1; i < response.size(); i++) {
                String s = response.get(i).trim();
                
                if (s.isEmpty()) {
                    break;
                }
                
                String[] pcs = s.split("\\:", 2);
                
                if (pcs.length == 2) {
                    headers.add(pcs[0].trim(), pcs[1].trim());
                }
            }
            
            return headers;
        }
        
        @Override
        public int getRawStatusCode() throws IOException {
            String status = response.get(0);
            return Integer.parseInt(status.split("\\ ")[1]);
        }
        
        @Override
        public String getStatusText() throws IOException {
            String status = response.get(0);
            return status.split("\\ ")[2];
        }
        
        @Override
        public void close() {
        }
        
    }
    
    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return new BrokerRequest(uri, httpMethod);
    }
    
}
