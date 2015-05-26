package com.vaadin.pekka.resizablecsslayout.client;

import com.google.gwt.event.shared.GwtEvent;

/**
 * GWT event for resize cancel. Fired when the user stops the resize by pressing
 * the ESC key.
 *
 */
public class ResizeCancelEvent extends GwtEvent<ResizableLayoutHandler> {

    /**
     * Event type for resize cancel events.
     */
    private static final Type<ResizableLayoutHandler> TYPE = new Type<ResizableLayoutHandler>();

    /**
     * Gets the event type associated with resize cancel events.
     *
     * @return the handler type
     */
    public static Type<ResizableLayoutHandler> getType() {
        return TYPE;
    }

    @Override
    public final Type<ResizableLayoutHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ResizableLayoutHandler handler) {
        handler.onResizeCancel(this);
    }

}
