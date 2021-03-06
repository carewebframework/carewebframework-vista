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

import java.util.Date;
import java.util.List;

import org.carewebframework.api.query.DateQueryFilter.DateType;
import org.carewebframework.api.query.IQueryContext;
import org.carewebframework.vista.api.documents.Document;
import org.carewebframework.vista.api.documents.DocumentDisplayQueryService;
import org.carewebframework.vista.api.documents.DocumentService;
import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.ui.reporting.controller.AbstractListController;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listitem;

/**
 * Controller for displaying the contents of selected documents.
 */
public class DocumentDisplayController extends AbstractListController<Document, Document> {

    private static final long serialVersionUID = 1L;

    private List<Document> documents;

    private Label lblInfo;

    private Combobox cboHeader;

    private final ComboitemRenderer<Document> comboRenderer = new DocumentDisplayComboRenderer();

    public DocumentDisplayController(DocumentService service) {
        super(new DocumentDisplayQueryService(service), "vistadocuments", "TIU", "documentsPrint.css");
        setPaging(false);
    }

    @Override
    protected void initializeController() {
        super.initializeController();
        cboHeader.setItemRenderer(comboRenderer);
    }

    @Override
    protected void prepareQueryContext(IQueryContext ctx) {
        ctx.setParam("documents", documents);
    }

    /**
     * This view should be closed when the patient context changes.
     */
    @Override
    protected void onPatientChanged(Patient patient) {
        closeView();
        super.onPatientChanged(patient);
    }

    /**
     * Suppress data fetch if there are no documents in the view.
     */
    @Override
    protected void fetchData() {
        if (documents != null) {
            super.fetchData();
        }
    }

    /**
     * Scroll to document with same header.
     */
    public void onSelect$cboHeader() {
        Document doc = cboHeader.getSelectedItem().getValue();

        for (Listitem item : listBox.getItems()) {
            Document doc2 = (Document) item.getValue();

            if (doc2 == null) {
                listBox.renderItem(item);
                doc2 = (Document) item.getValue();
            }

            if (doc == doc2) {
                Clients.scrollIntoView(item);
                break;
            }
        }
    }

    /**
     * Clears any displayed documents and reverts to document selection mode.
     */
    public void onClick$btnReturn() {
        closeView();
    }

    /**
     * Not really needed.
     */
    @Override
    public Date getDateByType(Document result, DateType dateType) {
        return result.getDateTime();
    }

    /**
     * Clears any displayed documents and reverts to document selection mode.
     */
    protected void closeView() {
        documents = null;
        setListModel(null);
        Events.postEvent("onViewOpen", root, false);
    }

    /**
     * Sets the documents to be displayed and updates the displayed count.
     *
     * @param documents The documents to be displayed.
     */
    protected void setDocuments(List<Document> documents) {
        this.documents = documents;
        int docCount = documents == null ? 0 : documents.size();
        lblInfo.setValue(docCount + " document(s)");
        refresh();
    }

    /**
     * Updates the header selector when the model changes.
     */
    @Override
    protected void setListModel(ListModel<Document> model) {
        super.setListModel(model);
        cboHeader.setModel(model);
        cboHeader.setText(null);
    }

    @Override
    protected List<Document> toModel(List<Document> results) {
        return results;
    }

}
