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

import java.util.Date;

import org.carewebframework.cal.api.query.AbstractDataService;
import org.carewebframework.cal.api.query.AbstractServiceContext;
import org.carewebframework.cal.api.query.IQueryResult;
import org.carewebframework.common.DateUtil;

/**
 * Data service wrapper for documents service.
 */
public class DocumentListDataService extends AbstractDataService<DocumentService, Document> {
    
    public DocumentListDataService(DocumentService service) {
        super(service);
    }
    
    @Override
    public IQueryResult<Document> fetchData(AbstractServiceContext<Document> ctx) throws Exception {
        Date startDate = ctx.dateRange.getStartDate();
        Date endDate = DateUtil.endOfDay(ctx.dateRange.getEndDate());
        return packageResult(service.retrieveHeaders(ctx.patient, ctx.user, startDate, endDate,
            (DocumentCategory) ctx.getParam("category")));
    }
}
