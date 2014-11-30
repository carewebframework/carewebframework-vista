/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.notification;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.common.NumUtil;
import org.carewebframework.ui.icons.IconUtil;

import org.zkoss.util.resource.Labels;

/**
 * Notification priority.
 */
public enum Priority {
    HIGH("bullet_red.png", "error"), MEDIUM("bullet_yellow.png", "warning"), LOW("bullet_green.png", "success");
    
    private String image;
    
    private String color;
    
    private Priority(String image, String color) {
        this.image = image == null ? null : IconUtil.getIconPath(image, "16x16", "silk");
        this.color = color;
    }
    
    /**
     * Returns the priority associated with the input value.
     * 
     * @param value The input value. May either be numeric, or the priority name.
     * @return The corresponding priority.
     */
    public static Priority fromString(String value) {
        if (StringUtils.isNumeric(value)) {
            return Priority.values()[NumUtil.enforceRange(NumberUtils.toInt(value) - 1, 0, 2)];
        } else {
            return valueOf(value);
        }
    }
    
    /**
     * Returns the url of the graphical representation of the priority.
     * 
     * @return An image url.
     */
    public String getImage() {
        return image;
    }
    
    /**
     * Returns the color to be used when displaying alerts.
     * 
     * @return A color.
     */
    public String getColor() {
        return color;
    }
    
    /**
     * Returns the display name for the priority.
     */
    @Override
    public String toString() {
        return Labels.getLabel("vistanotification.priority.label." + name());
    }
};
