/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.context.location;

import java.util.List;

import ca.uhn.fhir.model.dstu.resource.Location;

import org.carewebframework.cal.api.context.LocationContext;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.ui.zk.PopupDialog;
import org.carewebframework.vista.api.domain.LocationUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

/**
 * Location selection controller. Supports selecting a location from a list of known locations and
 * requesting that the location context be changed to that selection.
 */
public class LocationSelection extends FrameworkController {
    
    private static final long serialVersionUID = 1L;
    
    private Listbox lstLocation;
    
    private Textbox txtLocation;
    
    /**
     * Display the location selection dialog.
     */
    static public void execute() {
        PopupDialog.popup(Constants.RESOURCE_PREFIX + "locationSelection.zul");
    }
    
    /**
     * Wire variables and events.
     * 
     * @throws Exception Unspecified exception.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        init();
    }
    
    /**
     * Lookup a location and populate listbox with results.
     * 
     * @throws Exception Unspecified exception.
     */
    public void onClick$btnLocation() throws Exception {
        locationLookup(txtLocation.getValue(), lstLocation, LocationContext.getActiveLocation());
    }
    
    /**
     * Performs a location lookup and populates the specified listbox with the results.
     * 
     * @param text Partial location name for lookup.
     * @param lstLocation Listbox to populate with results.
     * @param deflt Default location to select.
     * @throws Exception Unspecified exception.
     */
    public static void locationLookup(String text, Listbox lstLocation, Location deflt) throws Exception {
        text = text == null ? "" : text.trim();
        
        if (!text.isEmpty()) {
            lstLocation.setDisabled(true);
            lstLocation.getItems().clear();
            List<Location> locations = LocationUtil.findLocations(text);
            boolean hasMatch = locations != null && locations.size() > 0;
            
            if (hasMatch) {
                for (Location location : locations) {
                    Listitem item = locationAdd(location, lstLocation);
                    
                    if (deflt != null && location.equals(deflt)) {
                        item.setSelected(true);
                        deflt = null;
                    }
                }
            } else {
                lstLocation.appendItem("No matches found", null);
            }
        }
    }
    
    /**
     * Adds the service location to the specified listbox.
     * 
     * @param location Service location to add.
     * @param lstLocation Listbox to receive the location.
     * @return The added list item.
     */
    public static Listitem locationAdd(Location location, Listbox lstLocation) {
        Listitem item = new Listitem(location.getName().getValue());
        item.setValue(location);
        //item.setTooltiptext(location.getDescription());
        item.addForward(Events.ON_DOUBLE_CLICK, "btnOK", Events.ON_CLICK);
        lstLocation.appendChild(item);
        lstLocation.setDisabled(false);
        return item;
    }
    
    /**
     * Initializes the listbox with the current location context, if one is set.
     * 
     * @param lstLocation The list box.
     */
    public static void locationInit(Listbox lstLocation) {
        Location location = LocationContext.getActiveLocation();
        
        if (location != null) {
            locationAdd(location, lstLocation).setSelected(true);
        }
    }
    
    public void onClick$btnCancel() {
        close();
    }
    
    /**
     * Change the location context to the selected location and close the dialog.
     */
    public void onClick$btnOK() {
        Listitem item = lstLocation.getSelectedItem();
        Location location = item == null ? null : (Location) item.getValue();
        
        if (location != null) {
            LocationContext.changeLocation(location);
            close();
        }
    }
    
    /**
     * Close the main dialog.
     */
    private void close() {
        root.detach();
    }
    
    /**
     * Set the selected location to the current context default.
     */
    private void init() {
        locationInit(lstLocation);
    }
}
