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
package org.carewebframework.vista.plugin.cwad;

import java.util.List;

import org.carewebframework.api.event.IGenericEvent;
import org.carewebframework.shell.plugins.PluginContainer;
import org.carewebframework.shell.plugins.PluginController;
import org.carewebframework.ui.zk.ReportBox;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.api.patient.PatientContext.IPatientContextEvent;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

/**
 * Controller for CWAD display.
 */
public class MainController extends PluginController implements IPatientContextEvent {

    private static final long serialVersionUID = 1L;

    private boolean popupFlags;

    private boolean allowPrint;

    private String eventId;

    private Window dlgFlags;

    private Toolbarbutton btnPostings;

    private Toolbarbutton btnCWAD;

    private BrokerSession broker;

    private final IGenericEvent<Object> gmraListener = new IGenericEvent<Object>() {

        @Override
        public void eventCallback(String eventName, Object eventData) {
            doUpdate(true);
        }

    };

    private void doUpdate(boolean noPopup) {
        if (dlgFlags != null) {
            dlgFlags.detach();
            dlgFlags = null;
        }

        Patient patient = PatientContext.getActivePatient();
        String cwad = patient == null ? "" : broker.callRPC("RGCWCACV CWAD", patient.getIdElement().getIdPart());
        boolean noPostings = cwad.isEmpty();
        btnCWAD.setLabel(cwad);
        btnCWAD.setDisabled(noPostings);
        btnPostings.setLabel(noPostings ? "No Postings" : "Postings");
        btnPostings.setDisabled(noPostings);

        if (!noPopup && popupFlags && cwad.contains("F")) {
            List<String> lst = broker.callRPCList("RGCWCACV PRF", null, patient.getIdElement().getIdPart());

            if (!lst.isEmpty()) {
                dlgFlags = ReportBox.amodal(lst, "Record Flags", allowPrint);
            }
        }

        if (eventId != null) {
            getEventManager().unsubscribe(eventId, gmraListener);
            eventId = null;
        }

        if (patient != null) {
            eventId = "GMRA." + patient.getIdElement().getIdPart();
            getEventManager().subscribe(eventId, gmraListener);
        }
    }

    @Override
    public String pending(boolean silent) {
        return null;
    }

    @Override
    public void committed() {
        doUpdate(false);
    }

    @Override
    public void canceled() {
    }

    public boolean getPopupFlags() {
        return popupFlags;
    }

    public void setPopupFlags(boolean popupFlags) {
        this.popupFlags = popupFlags;

        if (popupFlags && dlgFlags == null) {
            doUpdate(false);
        }
    }

    public boolean getAllowPrint() {
        return allowPrint;
    }

    public void setAllowPrint(boolean allowPrint) {
        this.allowPrint = allowPrint;
    }

    public void setBrokerSession(BrokerSession broker) {
        this.broker = broker;
    }

    @Override
    public void onLoad(PluginContainer container) {
        super.onLoad(container);
        container.registerProperties(this, "allowPrint", "popupFlags");
        doUpdate(false);
    }

    @Override
    public void refresh() {
        doUpdate(true);
    }

    public void onClick() {
        if (PatientContext.getActivePatient() != null) {
            DetailsController.show(allowPrint);
        }
    }

}
