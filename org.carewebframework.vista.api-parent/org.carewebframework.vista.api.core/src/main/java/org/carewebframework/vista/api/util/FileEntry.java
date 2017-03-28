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
package org.carewebframework.vista.api.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A single file entry.
 */
public class FileEntry {
    
    private static final Comparator<FileEntry> sortComparator = new Comparator<FileEntry>() {
        
        @Override
        public int compare(FileEntry o1, FileEntry o2) {
            return o1.external.compareToIgnoreCase(o2.external);
        }
        
    };
    
    private final String internal;
    
    private final String external;
    
    public static List<FileEntry> fromList(List<String> data, boolean sorted) {
        List<FileEntry> entries = new ArrayList<>();
        
        for (String next : data) {
            entries.add(new FileEntry(next));
        }
        
        if (sorted) {
            Collections.sort(entries, sortComparator);
        }
        
        return entries;
    }
    
    public static FileEntry find(List<FileEntry> entries, String value) {
        if (value != null) {
            for (FileEntry entry : entries) {
                if (value.equals(entry.internal) || value.equals(entry.external)) {
                    return entry;
                }
            }
        }
        
        return null;
    }
    
    public FileEntry(String data) {
        this(data.split("\\^", 2));
    }
    
    private FileEntry(String[] data) {
        this(data[0], data[1]);
    }
    
    public FileEntry(String internal, String external) {
        this.internal = internal;
        this.external = external;
    }
    
    /**
     * Returns the internal value of the file entry. This is normally the internal entry number, but
     * for sets it will be the internal value of the set member.
     * 
     * @return The unique id.
     */
    public String getInternalValue() {
        return internal;
    }
    
    /**
     * Returns the external value of the file entry. This is normally the value of the .01 field,
     * but for sets it will be the external value of the set member.
     * 
     * @return The value.
     */
    public String getExternalValue() {
        return external;
    }
    
    @Override
    public String toString() {
        return external;
    }
    
    @Override
    public boolean equals(Object object) {
        return object instanceof FileEntry && ((FileEntry) object).internal.equals(internal);
    }
    
}
