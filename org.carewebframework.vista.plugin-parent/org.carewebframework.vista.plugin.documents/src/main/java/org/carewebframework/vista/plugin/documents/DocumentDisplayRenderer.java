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

import org.carewebframework.ui.zk.AbstractListitemRenderer;
import org.carewebframework.vista.api.documents.Document;
import org.hspconsortium.cwf.ui.reporting.Constants;
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
