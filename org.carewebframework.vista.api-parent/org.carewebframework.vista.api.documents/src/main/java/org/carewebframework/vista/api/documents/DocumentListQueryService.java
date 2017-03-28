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

import org.carewebframework.api.query.AbstractQueryServiceEx;
import org.carewebframework.api.query.IQueryContext;
import org.carewebframework.api.query.IQueryResult;
import org.carewebframework.api.query.QueryUtil;
import org.carewebframework.common.DateRange;
import org.carewebframework.common.DateUtil;
import org.hl7.fhir.dstu3.model.Patient;

/**
 * Data service wrapper for documents service.
 */
public class DocumentListQueryService extends AbstractQueryServiceEx<DocumentService, Document> {

    public DocumentListQueryService(DocumentService service) {
        super(service);
    }

    @Override
    public IQueryResult<Document> fetch(IQueryContext context) {
        DateRange dateRange = (DateRange) context.getParam("dateRange");
        Date startDate = dateRange.getStartDate();
        Date endDate = DateUtil.endOfDay(dateRange.getEndDate());
        Patient patient = (Patient) context.getParam("patient");
        DocumentCategory category = (DocumentCategory) context.getParam("category");
        return QueryUtil.packageResult(service.retrieveHeaders(patient, startDate, endDate, category));
    }

    @Override
    public boolean hasRequired(IQueryContext context) {
        return true;
    }
}
