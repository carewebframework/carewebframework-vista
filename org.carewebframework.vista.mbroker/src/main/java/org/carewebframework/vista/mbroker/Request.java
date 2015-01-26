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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.carewebframework.vista.mbroker.RPCParameter.HasData;

/**
 * Request to be sent to host.
 */
public class Request {
    
    private static final byte[] ACTION_CODE = { 'C', // connect
            'D', // disconnect
            'P', // ping
            'Q', // query
            'R', // RPC call
            'S', // subscribe
            'U' // unsubscribe
    };
    
    private static final String[] ACTION_TEXT = { "Connect", "Disconnect", "Ping", "Query", "Remote Procedure Call",
            "Subscribe", "Unsubscribe" };
    
    public static enum Action {
        CONNECT, DISCONNECT, PING, QUERY, RPC, SUBSCRIBE, UNSUBSCRIBE;
        
        public String getText() {
            return ACTION_TEXT[this.ordinal()];
        }
        
        public byte getCode() {
            return ACTION_CODE[this.ordinal()];
        }
    };
    
    private static final byte[] PREAMBLE = new byte[] { '{', 'C', 'I', 'A', '}', Constants.EOD };
    
    private final Action action;
    
    private byte sequenceId;
    
    private final DynamicByteBuffer buffer = new DynamicByteBuffer();
    
    public Request(Action action) {
        this.action = action;
    }
    
    /**
     * Pack an argument and place in buffer.
     *
     * @param data Argument to pack.
     */
    private void pack(String data) {
        byte[] bytes = data.getBytes(Constants.UTF8);
        buffer.put(getLengthDescriptor(bytes.length));
        buffer.put(bytes);
    }
    
    /**
     * Returns the length descriptor for a data packet. Length descriptor consists of a descriptor
     * length byte whose high nybble is the number of additional bytes in the descriptor and whose
     * low nybble is the low nybble of the data length value. The additional descriptor bytes (if
     * any) form the rest of the data length value from low byte to high byte.
     *
     * @param length The length to be encoded.
     * @return The length descriptor.
     */
    private byte[] getLengthDescriptor(int length) {
        int c = 0;
        int j = length & 15;
        length = length >> 4;
        int max = 9;
        byte[] result = new byte[max + 1];
        
        while (length > 0) {
            byte b = (byte) (length & 255);
            result[max - c] = b;
            length = length >> 8;
            c++;
        }
        
        byte b = (byte) ((c << 4) + j);
        result[max - c] = b;
        return Arrays.copyOfRange(result, max - c, max + 1);
    }
    
    /**
     * Adds RPC parameters to the request.
     *
     * @param parameters RPC parameters.
     */
    public void addParameters(RPCParameters parameters) {
        for (int i = 0; i < parameters.getCount(); i++) {
            RPCParameter parameter = parameters.get(i);
            
            if (parameter.hasData() != HasData.NONE) {
                addParameter(Integer.toString(i + 1), parameter);
            }
        }
    }
    
    /**
     * Adds map-based parameters to the request.
     *
     * @param parameters Map of parameters.
     * @param suppressNull If true, ignore null parameters.
     */
    public void addParameters(Map<String, Object> parameters, boolean suppressNull) {
        
        for (String name : parameters.keySet()) {
            Object value = parameters.get(name);
            
            if (!suppressNull || value != null) {
                addParameter(name, value);
            }
        }
    }
    
    /**
     * Adds a subscripted parameter to the request.
     * 
     * @param name Parameter name.
     * @param sub Subscript(s).
     * @param data Parameter value.
     */
    public void addParameter(String name, String sub, Object data) {
        pack(name);
        pack(sub);
        pack(BrokerUtil.toString(data));
    }
    
    /**
     * Adds an RPC parameter to the request.
     *
     * @param name Parameter name.
     * @param parameter RPC parameter.
     */
    public void addParameter(String name, RPCParameter parameter) {
        for (String subscript : parameter) {
            addParameter(name, subscript, parameter.get(subscript));
        }
    }
    
    /**
     * Adds a parameter to the request.
     *
     * @param name Parameter name (optionally with subscripts).
     * @param value Parameter value.
     */
    public void addParameter(String name, Object value) {
        String subscript = "";
        
        if (name.contains("(")) {
            String[] pcs = name.split("\\(", 2);
            name = pcs[0];
            subscript = pcs[1];
            
            if (subscript.endsWith(")")) {
                subscript = subscript.substring(0, subscript.length() - 1);
            }
        }
        
        addParameter(name, subscript, value);
    }
    
    /**
     * Returns the action associated with the request.
     *
     * @return An action.
     */
    public Action getAction() {
        return action;
    }
    
    /**
     * Returns the sequence id for the request.
     * 
     * @return The sequence id.
     */
    public byte getSequenceId() {
        return sequenceId;
    }
    
    /**
     * Write request components to output stream.
     * 
     * @param stream Output stream.
     * @param sequenceId Sequence identifier for packet.
     * @throws IOException An IO exception.
     */
    public void write(DataOutputStream stream, byte sequenceId) throws IOException {
        this.sequenceId = sequenceId;
        stream.write(PREAMBLE);
        stream.write(sequenceId);
        stream.write(action.getCode());
        stream.write(buffer.toArray());
        stream.write(Constants.EOD);
    }
}
