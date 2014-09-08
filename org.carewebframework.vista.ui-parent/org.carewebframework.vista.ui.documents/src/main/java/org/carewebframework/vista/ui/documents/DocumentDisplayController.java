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

import java.util.Date;
import java.util.List;

import org.carewebframework.cal.api.query.AbstractServiceContext.DateMode;
import org.carewebframework.cal.ui.reporting.controller.AbstractListController;
import org.carewebframework.cal.ui.reporting.model.ServiceContext;
import org.carewebframework.vista.api.documents.Document;
import org.carewebframework.vista.api.documents.DocumentDisplayDataService;
import org.carewebframework.vista.api.documents.DocumentService;

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
public class DocumentDisplayController extends AbstractListController<Document> {
    
    private static final long serialVersionUID = 1L;
    
    private List<Document> documents;
    
    private Label lblInfo;
    
    private Combobox cboHeader;
    
    private final ComboitemRenderer<Document> comboRenderer = new DocumentDisplayComboRenderer();
    
    public DocumentDisplayController(final DocumentService service) {
        super(new DocumentDisplayDataService(service), "vistadocuments", "TIU", "documentsPrint.css");
        setPaging(false);
    }
    
    @Override
    protected void initializeController() {
        super.initializeController();
        cboHeader.setItemRenderer(comboRenderer);
    }
    
    @Override
    protected ServiceContext<Document> getServiceContext() {
        ServiceContext<Document> ctx = super.getServiceContext();
        ctx.setParam("documents", documents);
        return ctx;
    }
    
    /**
     * This view should be closed when the patient context changes.
     */
    @Override
    protected void patientChanged() {
        closeView();
        super.patientChanged();
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
    protected Date getDate(Document result, DateMode dateMode) {
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
    
}
