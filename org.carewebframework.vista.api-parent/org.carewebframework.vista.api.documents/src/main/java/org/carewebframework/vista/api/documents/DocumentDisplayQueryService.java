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

import java.util.List;

import org.carewebframework.api.query.AbstractQueryServiceEx;
import org.carewebframework.api.query.IQueryContext;
import org.carewebframework.api.query.IQueryResult;
import org.carewebframework.api.query.QueryUtil;

/**
 * Data service wrapper for retrieving document contents.
 */
public class DocumentDisplayQueryService extends AbstractQueryServiceEx<DocumentService, Document> {
    
    public DocumentDisplayQueryService(DocumentService service) {
        super(service);
    }
    
    @Override
    public IQueryResult<Document> fetch(IQueryContext context) {
        @SuppressWarnings("unchecked")
        List<Document> documents = (List<Document>) context.getParam("documents");
        
        if (documents != null) {
            service.retrieveContents(documents);
        }
        return QueryUtil.packageResult(documents);
    }
    
    @Override
    public boolean hasRequired(IQueryContext context) {
        return context.getParam("documents") instanceof List;
    }
    
}
