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
import java.io.InputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

/**
 * Converts the response from a REST call via the broker to an http response.
 */
class BrokerHttpResponse extends BasicHttpResponse implements CloseableHttpResponse {
    
    /**
     * Delivers the string list as a stream.
     */
    private static class ListInputStream extends InputStream {
        
        private final List<String> list;
        
        private int index = 0;
        
        private int pos = 1; // First byte is a line feed, so ignore.
        
        ListInputStream(List<String> list) {
            this.list = list;
        }
        
        @Override
        public int read() throws IOException {
            byte[] _byte = new byte[1];
            return read(_byte, 0, 1) == -1 ? -1 : _byte[0];
        }
        
        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }
        
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int count = 0;
            
            while (len > 0) {
                if (index >= list.size()) {
                    break;
                }
                
                byte[] bytes = list.get(index).getBytes();
                int ln = bytes.length - pos;
                
                if (ln <= 0) {
                    pos = 0;
                    index++;
                    continue;
                }
                
                int chunk = Math.min(len, ln);
                len -= chunk;
                count += chunk;
                
                while (chunk-- > 0) {
                    b[off++] = bytes[pos++];
                }
            }
            
            return count == 0 ? -1 : count;
        }
        
    }
    
    private static StatusLine createStatusLine(String statusLine) {
        String[] pcs = statusLine.split("\\ ", 3);
        String p1[] = pcs[0].split("\\/", 2);
        String p2[] = p1[1].split("\\.", 2);
        ProtocolVersion protocolVersion = new ProtocolVersion(p1[0], Integer.parseInt(p2[0]), Integer.parseInt(p2[1]));
        int statusCode = Integer.parseInt(pcs[1]);
        String reasonPhrase = pcs[2];
        return new BasicStatusLine(protocolVersion, statusCode, reasonPhrase);
    }
    
    public BrokerHttpResponse(List<String> response) {
        super(createStatusLine(response.get(0)));
        ContentType contentType = null;
        InputStream body = null;
        
        for (int i = 1; i < response.size(); i++) {
            String s = response.get(i).trim();
            
            if (s.isEmpty()) {
                body = new ListInputStream(response.subList(i + 1, response.size()));
                break;
            }
            
            String[] pcs = s.split("\\:", 2);
            Header header = new BasicHeader(pcs[0].trim(), pcs[1].trim());
            addHeader(header);
            
            if (contentType == null && header.getName().equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
                contentType = parseContentType(header.getValue());
            }
        }
        
        setEntity(new InputStreamEntity(body, contentType));
    }
    
    /**
     * Parse the returned content type.
     * 
     * @param value Value to parse
     * @return ContentType instance
     */
    private ContentType parseContentType(String value) {
        String[] pcs = value.split("\\;");
        String mimeType = pcs[0].trim();
        String charSet = "UTF-8";
        
        for (int i = 1; i < pcs.length; i++) {
            String s = pcs[i].trim().toUpperCase();
            
            if (s.startsWith("CHARSET=")) {
                charSet = s.substring(8);
                break;
            }
        }
        
        return ContentType.create(mimeType, charSet);
    }
    
    @Override
    public void close() {
    }
    
}
