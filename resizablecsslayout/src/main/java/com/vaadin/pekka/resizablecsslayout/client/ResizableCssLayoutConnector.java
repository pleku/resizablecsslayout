package com.vaadin.pekka.resizablecsslayout.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.client.ui.csslayout.CssLayoutConnector;
import com.vaadin.client.ui.customcomponent.CustomComponentConnector;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
@Connect(ResizableCssLayout.class)
public class ResizableCssLayoutConnector extends CssLayoutConnector implements
        ResizableLayoutHandler {

    ResizableCssLayoutServerRpc rpc = RpcProxy.create(
            ResizableCssLayoutServerRpc.class, this);

    @Override
    public void init() {
        super.init();
        getWidget().addResizeStartHandler(this);
        getWidget().addResizeEndHandler(this);
        getWidget().addResizeCancelHandler(this);
        registerRpc(ResizableCssLayoutClientRpc.class,
                new ResizableCssLayoutClientRpc() {

                    @Override
                    public void acceptResize(boolean acceptResize) {
                        getWidget().acceptResize(acceptResize);
                    }
                });
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(ResizableVCssLayout.class);
    }

    @Override
    public ResizableVCssLayout getWidget() {
        return (ResizableVCssLayout) super.getWidget();
    }

    @Override
    public ResizableCssLayoutState getState() {
        return (ResizableCssLayoutState) super.getState();
    }

    @Override
    public void setParent(ServerConnector parent) {
        super.setParent(parent);
        setResizeBoundary(parent);
    }

    /**
     * Recursively gets the closest parent connector that actually has a widget
     * and sets it as the boundary resize element for the widget.
     */
    protected void setResizeBoundary(ServerConnector parent) {
        if (parent != null) {
            if (parent instanceof AbstractHasComponentsConnector
                    && !(parent instanceof CustomComponentConnector)) {
                getWidget().setResizeBoundaryElement(
                        ((AbstractComponentConnector) parent).getWidget()
                                .getElement());
            } else {
                setResizeBoundary(parent.getParent());
            }
        } else {
            getWidget().setResizeBoundaryElement(null);
        }
    }

    @Override
    public void onResizeStart(ResizeStartEvent event) {
        rpc.onResizeStart(event.getResizeLocation(), getWidget()
                .getOffsetHeight(), getWidget().getOffsetWidth());
    }

    @Override
    public void onResizeEnd(ResizeEndEvent event) {
        rpc.onResizeEnd(event.getHeight(), event.getWidth());
    }

    @Override
    public void onResizeCancel(ResizeCancelEvent resizeCancelEvent) {
        rpc.onResizeCancel();
    }

}
