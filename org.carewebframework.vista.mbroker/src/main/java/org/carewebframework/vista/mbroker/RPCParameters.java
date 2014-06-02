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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of all parameters to be passed to an RPC.
 */
public class RPCParameters {

    private final List<RPCParameter> params = new ArrayList<RPCParameter>();

    /**
     * Creates an empty parameter list.
     */
    public RPCParameters() {

    }

    /**
     * Creates a parameter list from one or more parameter values.
     *
     * @param args Parameter value(s).
     */
    public RPCParameters(Object... args) {
        int i = 0;

        for (Object arg : args) {
            RPCParameter param = get(i++);

            if (arg instanceof Iterable<?>) {
                param.assign((Iterable<?>) arg);
            } else if (arg != null && arg.getClass().isArray()) {
                param.assignArray(arg);
            } else {
                param.setValue(arg);
            }
        }
    }

    /**
     * Adds an empty parameter to the list.
     *
     * @return Index of added parameter.
     */
    public int add() {
        int result = params.size();
        params.add(new RPCParameter());
        return result;
    }

    /**
     * Returns the parameter at the specified index. The list will be automatically expanded if
     * necessary.
     *
     * @param index Parameter index.
     * @return The RPC parameter.
     */
    public RPCParameter get(int index) {
        expand(index + 1);
        return params.get(index);
    }

    /**
     * Returns the number of parameters in the list.
     *
     * @return Parameter count.
     */
    public int getCount() {
        return params.size();
    }

    /**
     * Removes all parameters from the list.
     */
    public void clear() {
        params.clear();
    }

    /**
     * Copies parameters from another list to this one.
     *
     * @param source Source parameter list.
     */
    public void assign(RPCParameters source) {
        clear();

        for (RPCParameter param : source.params) {
            params.add(new RPCParameter(param));
        }
    }
    
    /**
     * Adds a parameter at the specified index.
     *
     * @param index Index for parameter.
     * @param param Parameter to add.
     */
    public void put(int index, RPCParameter param) {
        expand(index);

        if (index < params.size()) {
            params.set(index, param);
        } else {
            params.add(param);
        }
    }

    /**
     * Expands the parameter list, if necessary, to the minimal size.
     *
     * @param size Minimal size of parameter list.
     */
    private void expand(int size) {
        while (size > params.size()) {
            add();
        }
    }
}
