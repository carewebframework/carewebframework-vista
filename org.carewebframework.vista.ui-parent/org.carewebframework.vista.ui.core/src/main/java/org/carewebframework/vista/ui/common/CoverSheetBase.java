/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.common;

import static org.carewebframework.common.StrUtil.U;
import static org.carewebframework.common.StrUtil.fromList;
import static org.carewebframework.common.StrUtil.piece;
import static org.carewebframework.common.StrUtil.toList;

import java.util.List;

import org.carewebframework.ui.sharedforms.ListViewForm;
import org.carewebframework.ui.zk.ReportBox;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.ui.mbroker.AsyncRPCCompleteEvent;
import org.carewebframework.vista.ui.mbroker.AsyncRPCErrorEvent;
import org.carewebframework.vista.ui.mbroker.AsyncRPCEventDispatcher;
import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.IReferenceable;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;

/**
 * Controller for cover sheet components.
 *
 * @param <T> Type of model object.
 */
public abstract class CoverSheetBase<T> extends ListViewForm<T> implements PatientContext.IPatientContextEvent {

    private static final long serialVersionUID = 1L;

    protected Label detailView;

    protected Patient patient;

    private String detailTitle;

    protected String detailRPC;

    protected String listRPC;

    private BrokerSession broker;

    private AsyncRPCEventDispatcher asyncDispatcher;

    private Component target;

    /**
     * Get top level component.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        this.target = comp;
    }

    @SuppressWarnings("unchecked")
    protected T parseData(String data) {
        return (T) data;
    }

    protected void setup(String title, String detailTitle, String listRPC, String detailRPC, int sortBy, String... headers) {
        this.detailTitle = detailTitle;
        this.listRPC = listRPC;
        this.detailRPC = detailRPC;
        super.setup(title, sortBy, headers);
    }

    @Override
    public String pending(boolean silent) {
        return null;
    }

    @Override
    public void committed() {
        patient = PatientContext.getActivePatient();
        refresh();
    }

    @Override
    public void canceled() {
    }

    /**
     * Override load list to clear display if no patient in context.
     */
    @Override
    protected void loadData() {
        if (patient == null) {
            asyncAbort();
            reset();
            status("No patient selected.");
        } else {
            super.loadData();
        }

        detailView.setValue(null);
    }

    @Override
    protected void requestData() {
        getAsyncDispatcher().callRPCAsync(listRPC, patient.getIdElement().getIdPart());
    }

    /**
     * Show detail for specified list item.
     *
     * @param li The list item.
     */
    protected void showDetail(Listitem li) {
        @SuppressWarnings("unchecked")
        T value = li == null ? null : (T) li.getValue();
        String detail = value == null ? null : getDetail(value);
        detailView.setValue(detail);

        if (!getShowDetailPane() && detail != null) {
            ReportBox.modal(detail, detailTitle, getAllowPrint());
        }
    }

    protected String getLogicalId(T data) {
        return data instanceof String ? piece((String) data, U)
                : data instanceof IReferenceable ? ((IReferenceable) data).getId().getIdPart() : "";

    }

    /**
     * Logic to return detail information for specified item.
     *
     * @param data Source for detail information.
     * @return The detail information.
     */
    protected String getDetail(T data) {
        String ien = getLogicalId(data);
        return detailRPC == null || ien == null || ien.isEmpty() ? null
                : fromList(getBroker().callRPCList(detailRPC, null, patient.getIdElement().getIdPart(), ien));
    }

    protected String getError(List<String> list) {
        String data = list.isEmpty() ? null : list.get(0);

        if (data != null && data.startsWith(U)) {
            return data.substring(1);
        } else {
            return null;
        }
    }

    /**
     * Display detail when item is selected.
     */
    @Override
    protected void itemSelected(Listitem li) {
        showDetail(li);
    }

    @Override
    protected void init() {
        super.init();
        committed();
    }

    public void onAsyncRPCComplete(AsyncRPCCompleteEvent event) {
        List<String> results = toList(event.getData(), null, "\r");
        String error = getError(results);

        if (error != null) {
            status(error);
            model.clear();
        } else {
            for (String result : results) {
                T value = parseData(result);

                if (value != null) {
                    model.add(value);
                }
            }

            renderData();
        }
    }

    public void onAsyncRPCError(AsyncRPCErrorEvent event) {
        status(event.getData());
    }

    @Override
    protected void asyncAbort() {
        getAsyncDispatcher().abort();
    }

    public AsyncRPCEventDispatcher getAsyncDispatcher() {
        if (asyncDispatcher == null) {
            asyncDispatcher = new AsyncRPCEventDispatcher(broker, target);
        }

        return asyncDispatcher;
    }

    public BrokerSession getBroker() {
        return broker;
    }

    public void setBroker(BrokerSession broker) {
        this.broker = broker;
    }

}
