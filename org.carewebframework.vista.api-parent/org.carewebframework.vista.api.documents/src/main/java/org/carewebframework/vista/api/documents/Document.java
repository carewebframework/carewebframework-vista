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

import org.carewebframework.cal.api.domain.DomainObject;

/**
 * Model object wrapping a clinical document.
 */
public class Document extends DomainObject implements Comparable<Document> {
    
    private String authorName;
    
    private Date dateTime;
    
    private String body;
    
    private String locationName;
    
    private String subject;
    
    private String title;
    
    private DocumentCategory category;
    
    @Override
    public int compareTo(Document document) {
        return 0;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public Date getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public String getLocationName() {
        return locationName;
    }
    
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    
    @Override
    public void setLogicalId(String id) {
        super.setLogicalId(id);
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public DocumentCategory getCategory() {
        return category;
    }
    
    public void setCategory(DocumentCategory category) {
        this.category = category;
    }
    
    public boolean hasCategory(DocumentCategory cat) {
        return category != null && category.equals(cat);
    }
    
}
