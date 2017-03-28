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
package org.carewebframework.vista.esig;

/**
 * This is the interface declaration for the electronic signature service.
 * 
 * 
 */
public interface IESigService {
    
    void add(ESigItem item); // Adds the item to the list of items pending signature.
    
    boolean getCanSign(); // Returns true if the list contains any signable items.
    
    boolean exist(IESigType esigType, String id); // Returns true if the item identified by type and id exists.
    
    void remove(IESigType esigType, String id); // Removes the item identified by type and id.
    
    void remove(ESigItem item); // Removes the specified item.
    
    boolean requiresReview(); // Returns true if the list has any items the require review.
    
    int getCount(); // Returns the count of all items in the list.
    
    int getCount(IESigType esigType); // Returns the count of items of the specified type.
    
    Iterable<ESigItem> getItems(ESigFilter filter); // Returns an iterable of items matching the specified filter (or all items if filter is null).
}
