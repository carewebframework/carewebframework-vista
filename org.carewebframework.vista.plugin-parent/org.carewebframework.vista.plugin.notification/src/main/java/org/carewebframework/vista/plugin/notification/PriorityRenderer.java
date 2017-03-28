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
package org.carewebframework.vista.plugin.notification;

import org.carewebframework.ui.icons.IconUtil;
import org.carewebframework.vista.api.notification.AbstractNotification.Priority;

import org.zkoss.util.resource.Labels;

/**
 * Returns various rendering attributes for Priority enum.
 */
public class PriorityRenderer {
    
    /**
     * Returns the url of the graphical representation of the priority.
     * 
     * @param priority Priority value
     * @return An image url.
     */
    public static String getImage(Priority priority) {
        String image = getLabel("icon", priority);
        return image == null ? null : IconUtil.getIconPath(image);
    }
    
    /**
     * Returns the color to be used when displaying alerts.
     * 
     * @param priority Priority value
     * @return A color.
     */
    public static String getColor(Priority priority) {
        return getLabel("color", priority);
    }
    
    /**
     * Returns the display name for the priority.
     * 
     * @param priority Priority value
     * @return Display name
     */
    public static String getDisplayName(Priority priority) {
        return getLabel("label", priority);
    }
    
    /**
     * Returns the label property for the specified attribute name and priority.
     * 
     * @param name The attribute name.
     * @param priority Priority value
     * @return The label value.
     */
    private static String getLabel(String name, Priority priority) {
        return priority == null ? null : Labels.getLabel("vistanotification.priority." + name + "." + priority.name());
    }
    
    /**
     * Enforce static class.
     */
    private PriorityRenderer() {
    }
};
