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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.carewebframework.api.query.AbstractQueryFilter;
import org.carewebframework.api.query.DateQueryFilter.DateType;
import org.carewebframework.api.query.IQueryContext;
import org.carewebframework.cal.ui.reporting.controller.AbstractListController;
import org.carewebframework.ui.zk.ZKUtil;
import org.carewebframework.vista.api.documents.Document;
import org.carewebframework.vista.api.documents.DocumentCategory;
import org.carewebframework.vista.api.documents.DocumentListQueryService;
import org.carewebframework.vista.api.documents.DocumentService;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ext.Selectable;

/**
 * Controller for the list-based display of clinical documents.
 */
public class DocumentListController extends AbstractListController<Document, Document> {
    
    /**
     * Handles filtering by document category.
     */
    private class QueryFilter extends AbstractQueryFilter<Document> {
        
        @Override
        public boolean include(Document document) {
            DocumentCategory filter = getCurrentFilter();
            return filter == null || document.hasCategory(filter);
        }
        
        @Override
        public boolean updateContext(IQueryContext context) {
            context.setParam("category", getCurrentFilter());
            return true;
        }
        
    }
    
    private static final long serialVersionUID = 1L;
    
    private Button btnClear;
    
    private Button btnView;
    
    private String viewText; //default view selected documents
    
    private final String lblBtnViewSelectAll = Labels.getLabel("vistadocuments.plugin.btn.view.selectall.label");
    
    private Combobox cboFilter;
    
    private Label lblFilter;
    
    private Label lblInfo;
    
    private DocumentCategory fixedFilter;
    
    private final List<DocumentCategory> allCategories;
    
    public DocumentListController(DocumentService service) {
        super(new DocumentListQueryService(service), "vistadocuments", "TIU", "documentsPrint.css");
        setPaging(false);
        registerQueryFilter(new QueryFilter());
        allCategories = service.getCategories();
    }
    
    @Override
    public void initializeController() {
        super.initializeController();
        viewText = btnView.getLabel();
        getContainer().registerProperties(this, "fixedFilter");
        updateSelectCount(0);
    }
    
    /**
     * This is a good place to update the filter list.
     */
    @Override
    protected List<Document> toModel(List<Document> results) {
        updateListFilter(results);
        return results;
    }
    
    /**
     * Limit categories in category filter to only those present in the unfiltered document list.
     *
     * @param documents The unfiltered document list.
     */
    private void updateListFilter(List<Document> documents) {
        if (fixedFilter != null) {
            return;
        }
        
        DocumentCategory currentFilter = getCurrentFilter();
        List<Comboitem> items = cboFilter.getItems();
        List<DocumentCategory> categories = new ArrayList<DocumentCategory>();
        
        while (items.size() > 1) {
            items.remove(1);
        }
        
        if (currentFilter != null) {
            categories.add(currentFilter);
        }
        
        if (documents != null) {
            for (Document doc : documents) {
                DocumentCategory cat = doc.getCategory();
                
                if (cat != null) {
                    if (!categories.contains(cat)) {
                        categories.add(cat);
                    }
                }
            }
        }
        
        Collections.sort(categories);
        cboFilter.setSelectedIndex(0);
        
        for (DocumentCategory cat : categories) {
            Comboitem item = cboFilter.appendItem(cat.getName());
            item.setValue(cat);
            
            if (cat.equals(currentFilter)) {
                cboFilter.setSelectedItem(item);
            }
        }
    }
    
    /**
     * Returns the currently active category filter.
     *
     * @return The active category filter.
     */
    private DocumentCategory getCurrentFilter() {
        return fixedFilter != null ? fixedFilter
                : cboFilter.getSelectedIndex() > 0 ? (DocumentCategory) cboFilter.getSelectedItem().getValue() : null;
    }
    
    /**
     * Handle change in category filter selection.
     */
    public void onSelect$cboFilter() {
        applyFilters();
    }
    
    /**
     * Update the display count of selected documents.
     *
     * @param selCount The selection count.
     */
    private void updateSelectCount(int selCount) {
        if (selCount == 0) {
            btnView.setLabel(lblBtnViewSelectAll);
            btnClear.setDisabled(true);
        } else {
            btnView.setLabel(viewText + " (" + selCount + ")");
            btnClear.setDisabled(false);
        }
        
        btnView.setDisabled(listBox.getItemCount() == 0);
    }
    
    /**
     * Update selection count.
     */
    public void onSelect$listBox() {
        updateSelectCount(listBox.getSelectedCount());
    }
    
    /**
     * Double-clicking enters document view mode.
     *
     * @param event The onDoubleClick event.
     */
    public void onDoubleClick$listBox(Event event) {
        Component cmpt = ZKUtil.getEventOrigin(event).getTarget();
        
        if (cmpt instanceof Listitem) {
            Events.postEvent("onDeferredOpen", listBox, cmpt);
        }
    }
    
    /**
     * Opening the display view after a double-click is deferred to avoid anomalies with selection
     * of the associated list item.
     * 
     * @param event The deferred open event.
     */
    public void onDeferredOpen$listBox(Event event) {
        Listitem item = (Listitem) ZKUtil.getEventOrigin(event).getData();
        item.setSelected(true);
        onSelect$listBox();
        onClick$btnView();
    }
    
    /**
     * Clear selected items
     */
    @Override
    public void clearSelection() {
        super.clearSelection();
        updateSelectCount(0);
    }
    
    /**
     * Triggers document view mode.
     */
    public void onClick$btnView() {
        Events.postEvent("onViewOpen", root, true);
    }
    
    /**
     * Returns a list of currently selected documents, or if no documents are selected, of all
     * documents.
     *
     * @return List of currently selected documents.
     */
    protected List<Document> getSelectedDocuments() {
        return getObjects(listBox.getSelectedCount() > 0);
    }
    
    /**
     * Returns the fixed filter, if any.
     *
     * @return The fixed filter.
     */
    public String getFixedFilter() {
        return fixedFilter == null ? null : fixedFilter.getName();
    }
    
    /**
     * Sets the fixed filter.
     *
     * @param name The fixed filter.
     */
    public void setFixedFilter(String name) {
        fixedFilter = findCategory(name);
        cboFilter.setVisible(fixedFilter == null);
        lblFilter.setVisible(fixedFilter != null);
        lblFilter.setValue(fixedFilter == null ? null : fixedFilter.getName());
        refresh();
    }
    
    private DocumentCategory findCategory(String name) {
        if (name != null && !name.isEmpty()) {
            for (DocumentCategory cat : allCategories) {
                if (cat.getName().equals(name)) {
                    return cat;
                }
            }
        }
        
        return null;
    }
    
    @Override
    protected void setListModel(ListModel<Document> model) {
        super.setListModel(model);
        int docCount = model == null ? 0 : model.getSize();
        lblInfo.setValue(docCount + " document(s)");
        btnView.setDisabled(docCount == 0);
        updateSelectCount(((Selectable<?>) model).getSelection().size());
    }
    
    @Override
    public Date getDateByType(Document result, DateType dateMode) {
        return result.getDateTime();
    }
}
