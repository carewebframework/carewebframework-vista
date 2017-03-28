/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.plugin.documents;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.carewebframework.ui.zk.AbstractListitemRenderer;
import org.carewebframework.vista.api.documents.Document;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;

/**
 * Renderer for the document list.
 *
 * @author dmartin
 */
public class DocumentListRenderer extends AbstractListitemRenderer<Document, Object> {

    private static final Log log = LogFactory.getLog(DocumentListRenderer.class);

    public DocumentListRenderer() {
        super("background-color: white", null);
    }

    /**
     * Render the list item for the specified document.
     *
     * @param item List item to render.
     * @param doc The document associated with the list item.
     */
    @Override
    public void renderItem(Listitem item, Document doc) {
        log.trace("item render");
        item.setSelectable(true);
        item.addForward(Events.ON_DOUBLE_CLICK, item.getListbox(), Events.ON_DOUBLE_CLICK);
        addCell(item, "");
        addCell(item, doc.getDateTime());
        addCell(item, doc.getTitle());
        addCell(item, doc.getLocationName());
        addCell(item, doc.getAuthorName());
    }

    /**
     * Add a cell to the list item containing the specified text value.
     *
     * @param item List item to receive new cell.
     * @param value Text to include in the new cell.
     */
    private void addCell(Listitem item, Object value) {
        createCell(item, value, null, null);
    }

}
