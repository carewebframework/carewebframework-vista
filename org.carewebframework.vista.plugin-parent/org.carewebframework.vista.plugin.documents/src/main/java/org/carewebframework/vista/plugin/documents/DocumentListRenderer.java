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
