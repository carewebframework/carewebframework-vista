/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.documents;

import org.carewebframework.ui.zk.AbstractComboitemRenderer;
import org.carewebframework.vista.api.documents.Document;

import org.zkoss.zul.Comboitem;

/**
 * Renderer for the document display combo box selector.
 */
public class DocumentDisplayComboRenderer extends AbstractComboitemRenderer<Document> {

    /**
     * Render the combo item for the specified document.
     *
     * @param item Combo item to render.
     * @param doc The document associated with the list item.
     */
    @Override
    public void renderItem(final Comboitem item, final Document doc) {
        item.setLabel(doc.getTitle());
    }
}
