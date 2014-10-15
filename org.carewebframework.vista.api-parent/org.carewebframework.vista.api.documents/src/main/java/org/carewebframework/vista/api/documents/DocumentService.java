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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.dstu.resource.User;

import org.carewebframework.api.spring.SpringUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.FMDate;

/**
 * This is the documents api implementation.
 */
public class DocumentService {
    
    private final BrokerSession broker;
    
    public static DocumentService getInstance() {
        return SpringUtil.getBean("vistaDocumentService", DocumentService.class);
    }
    
    public DocumentService(BrokerSession broker) {
        this.broker = broker;
    };
    
    public boolean hasDocuments(Patient patient) {
        return patient != null;
    }
    
    public List<DocumentCategory> getCategories() {
        List<DocumentCategory> categories = new ArrayList<DocumentCategory>();
        
        for (String result : broker.callRPCList("CIAURPC FILGET", null, 8925.1, null, null, "I $P(^(0),U,4)=\"CL\"")) {
            String[] pcs = StrUtil.split(result, StrUtil.U);
            DocumentCategory cat = new DocumentCategory(pcs[0]);
            cat.setName(pcs[1]);
            categories.add(cat);
        }
        
        Collections.sort(categories);
        return categories;
    }
    
    /**
     * RPC: TIU GET RECORD TEXT
     *
     * <pre>
     *     Get the textual portion of a TIU Document.
     * 
     *     Input parameters:
     *         IEN     -  IEN[8925] in the TIU Document file.
     *         ACTION  -  Defaults to VIEW.  .01 Field for file USR ACTION.
     * 
     *     Return format:
     *         An array of text.
     *           Example (partial):
     *             Dest[0]=TITLE: PC ACUTE CARE VISIT
     *         Dest[1]=DATE OF NOTE: DEC 17, 2004@11:05     ENTRY DATE: DEC 17,2004@11:05:28
     *         Dest[2]=      AUTHOR: HAGER,MARY G         EXP COSIGNER:
     *         Dest[3]=     URGENCY:                            STATUS: COMPLETED
     *         Dest[4]=
     *         Dest[5]=12/16/04 16:14
     *         Dest[6]=
     *         Dest[7]=Patient is a 49 year old MALE
     *         Dest[8]=
     *         Dest[9]=Vitals:
     *         Dest[10]=WT:240 (108 kg), HT:70 (177 cm), TMP:98.6 (37 C), BP:140/90 , PU:80, RS:16,
     *         Dest[11]=PA:0
     * </pre>
     *
     * @param documents Documents for which to retrieve contents.
     */
    public void retrieveContents(List<Document> documents) {
        for (Document doc : documents) {
            if (doc.getBody() == null) {
                List<String> body = broker.callRPCList("TIU GET RECORD TEXT", null, doc.getId().getIdPart());
                doc.setBody(StrUtil.fromList(body));
            }
        }
    }
    
    /**
     * RPC: TIU DOCUMENTS BY CONTEXT
     *
     * <pre>
     *     Returns lists of TIU Documents that satisfy the following search criteria:
     * 
     *     1 - signed documents (all)
     *     2 - unsigned documents
     *     3 - uncosigned documents
     *     4 - signed documents/author
     *     5 - signed documents/date range
     * 
     *     Input parameters:
     *         DocClass -  Pointer to TIU DOCUMENT DEFINITION #8925.1
     *     Context  -  1=All Signed (by PT),
     *                 2=Unsigned (by PT&(AUTHOR!TANSCRIBER))
     *                 3=Uncosigned (by PT&EXPECTED COSIGNER
     *                 4=Signed notes (by PT&selected author)
     *                 5=Signed notes (by PT&date range)
     *     DFN      -  Patient DFN
     *     Early    -  Fileman date to begin search (optional)
     *     Late     -  Fileman date to end search (optional)
     *     Person   -  IEN of file 200 (optional)  Uses DUZ if not passed.
     *     OccLim   -  Occurrence Limit (optional).  Maximum number of documents to be retrieved.
     *     SortSeq  -  Direction to search through dates, "A" - ascending, "D" - descending
     *     ShowAdd  -  parameter determines whether addenda will be included in the return array
     *     IncUnd   -  Flag to include undictated and untranscribed.  Only applies when CONTEXT=2
     * 
     *     Return format:
     *         An array in the following format:
     *        1    2             3                            4                       5                6
     *           IEN^TITLE^REFERENCE DATE/TIME (INT)^PATIENT NAME (LAST I/LAST 4)^AUTHOR (INT;EXT)^HOSPITAL LOCATION^
     *                7              8                 9                        10
     *       SIGNATURE STATUS^Visit Date/Time^Discharge Date/time^Variable Pointer to Request (e.g., Consult)^
     *                  11             12        13              14
     *       # of Associated Images^Subject^Has Children^IEN of Parent Document
     * </pre>
     *
     * @param patient The patient.
     * @param user The user.
     * @param startDate The start date for retrieval.
     * @param endDate The end date for retrieval.
     * @param category The document category.
     * @return List of matching documents.
     */
    public List<Document> retrieveHeaders(Patient patient, User user, Date startDate, Date endDate, DocumentCategory category) {
        List<DocumentCategory> categories = category == null ? getCategories() : Collections.singletonList(category);
        List<Document> results = new ArrayList<Document>();
        
        for (DocumentCategory cat : categories) {
            for (String result : broker.callRPCList("TIU DOCUMENTS BY CONTEXT", null, cat.getId().getIdPart(), 1, patient
                    .getId().getIdPart(), startDate, endDate, user.getId().getIdPart())) {
                String[] pcs = StrUtil.split(result, StrUtil.U, 14);
                Document doc = new Document();
                doc.setId(pcs[0]);
                doc.setTitle(pcs[1]);
                doc.setDateTime(new FMDate(pcs[2]));
                doc.setAuthorName(StrUtil.piece(pcs[4], ";", 3));
                doc.setLocationName(pcs[5]);
                doc.setSubject(pcs[11]);
                doc.setCategory(cat);
                results.add(doc);
            }
        }
        
        return results;
    }
}
