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

import org.apache.commons.lang.StringUtils;

/**
 * Wraps exceptions returned by an RPC.
 */
public class RPCException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private int code = 0;
    
    /**
     * Exception information parsed from the RPC return value.
     */
    private static class ExceptionInfo {
        
        private int code;
        
        private String text;
        
        private ExceptionInfo(String text) {
            int i = text.indexOf(Constants.LINE_SEPARATOR);
            String first = i < 1 ? "" : text.substring(0, i);
            
            if (StringUtils.isNumeric(first)) {
                code = Integer.parseInt(first);
                this.text = text.substring(++i);
            } else {
                this.text = text;
            }
        }
    }
    
    /**
     * Creates an exception based on the error text.
     *
     * @param text Error text returned by RPC.
     */
    public RPCException(String text) {
        this(new ExceptionInfo(text));
    }
    
    /**
     * Creates an RPC exception from an error code and text.
     * 
     * @param code The error code.
     * @param text The error text.
     */
    public RPCException(int code, String text) {
        super(text);
        this.code = code;
    }
    
    /**
     * Creates an exception from the parsed text.
     *
     * @param value Exception information.
     */
    private RPCException(ExceptionInfo value) {
        this(value.code, value.text);
    }
    
    /**
     * Returns the error code.
     *
     * @return The error code.
     */
    public int getCode() {
        return code;
    }
    
    @Override
    public String toString() {
        return code + ": " + super.toString();
    }
}
