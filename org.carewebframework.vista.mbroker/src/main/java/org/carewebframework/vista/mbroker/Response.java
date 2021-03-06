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

import java.io.DataInputStream;
import java.io.IOException;

/**
 * A response received from the host server.
 */
public class Response {
    
    public static enum ResponseType {
        ACK, ERROR, ASYNC, EVENT
    };
    
    private ResponseType responseType;
    
    private final String data;
    
    private Byte sequenceId;
    
    /**
     * Creates a response from an input stream.
     * 
     * @param stream The input stream.
     * @throws IOException An IO exception.
     */
    public Response(DataInputStream stream) throws IOException {
        int bufsize = 100;
        byte[] temp = new byte[bufsize];
        DynamicByteBuffer buffer = new DynamicByteBuffer(bufsize, 1000);
        int bytesRead;
        boolean eod = false;
        
        while (!eod && (bytesRead = stream.read(temp)) > -1) {
            int offset = 0;
            
            if (bytesRead > 0 && temp[bytesRead - 1] == Constants.EOD) {
                eod = true;
                bytesRead--;
            }
            
            if (bytesRead > 0 && sequenceId == null) {
                sequenceId = temp[offset++];
                bytesRead--;
            }
            
            if (bytesRead > 0 && responseType == null) {
                responseType = getResponseType(temp[offset++]);
                bytesRead--;
            }
            
            if (bytesRead > 0) {
                buffer.put(temp, offset, offset + bytesRead);
            }
        }
        
        data = new String(buffer.toArray(), Constants.UTF8);
    }
    
    /**
     * Returns the response type from the status value.
     * 
     * @param code The response code.
     * @return The response type.
     */
    private ResponseType getResponseType(int code) throws IOException {
        try {
            return ResponseType.values()[code];
        } catch (Exception e) {
            throw new IOException("Unrecognized response code: " + code);
        }
    }
    
    /**
     * Creates an error response.
     *
     * @param e Exception to package.
     */
    public Response(Throwable e) {
        responseType = ResponseType.ERROR;
        data = e.toString();
    }
    
    /**
     * Returns the response type.
     *
     * @return The response type.
     */
    public ResponseType getResponseType() {
        return responseType;
    }
    
    /**
     * Returns the response data.
     *
     * @return The response data.
     */
    public String getData() {
        return data;
    }
    
    public byte getSequenceId() {
        return sequenceId == null ? 0 : sequenceId;
    }
    
}
