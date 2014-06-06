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

import org.carewebframework.cal.ui.reporting.Constants;
import org.carewebframework.ui.zk.AbstractListitemRenderer;
import org.carewebframework.vista.api.documents.Document;

import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

/**
 * Renderer for the document display.
 */
public class DocumentDisplayRenderer extends AbstractListitemRenderer<Document, Object> {
    
    public DocumentDisplayRenderer() {
        super("", null);
    }
    
    /**
     * Render the list item for the specified document.
     *
     * @param item List item to render.
     * @param doc The document associated with the list item.
     */
    @Override
    public void renderItem(final Listitem item, final Document doc) {
        final Listcell cell = new Listcell();
        item.appendChild(cell);
        final Div sep = new Div();
        sep.setSclass("vista-documents-sep");
        cell.appendChild(sep);
        final Div div = new Div();
        div.setSclass(Constants.SCLASS_TEXT_REPORT_TITLE);
        cell.appendChild(div);
        final Hbox boxHeader = new Hbox();
        final Label header = new Label(doc.getTitle());
        header.setZclass(Constants.SCLASS_TEXT_REPORT_TITLE);
        boxHeader.appendChild(header);
        div.appendChild(boxHeader);
        Label body = new Label(doc.getBody());
        body.setMultiline(true);
        body.setPre(true);
        cell.appendChild(body);
    }
    
}
