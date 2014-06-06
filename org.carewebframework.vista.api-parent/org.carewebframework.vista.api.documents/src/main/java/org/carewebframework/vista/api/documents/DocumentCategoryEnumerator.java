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

import java.util.Iterator;
import java.util.List;

/**
 * Provides an iterable interface for document categories.
 *
 * @author dmartin
 */
public class DocumentCategoryEnumerator implements Iterable<DocumentCategory> {
    
    public DocumentCategoryEnumerator() {
    }
    
    @Override
    public Iterator<DocumentCategory> iterator() {
        List<DocumentCategory> categories = DocumentService.getInstance().getCategories();
        return categories.iterator();
    }
    
}
