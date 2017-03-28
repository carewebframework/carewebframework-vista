/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.documents;

import org.hspconsortium.cwf.api.DomainObject;

/**
 * Model object wrapping a clinical document.
 */
public class DocumentCategory extends DomainObject implements Comparable<DocumentCategory> {

    private String name;

    public DocumentCategory(String logicalId) {
        super(logicalId);
    }

    @Override
    public int compareTo(DocumentCategory cat) {
        return name.compareToIgnoreCase(cat.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
