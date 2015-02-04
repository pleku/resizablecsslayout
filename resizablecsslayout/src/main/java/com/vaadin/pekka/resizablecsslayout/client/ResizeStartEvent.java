package com.vaadin.pekka.resizablecsslayout.client;

import com.google.gwt.event.shared.GwtEvent;

/**
 * GWT event for resize start. Fired when user starts resize by pressing the
 * mouse down on the location {@link #getResizeLocation()}.
 */
public class ResizeStartEvent extends GwtEvent<ResizableLayoutHandler> {

    private final ResizeLocation resizeLocation;

    public ResizeStartEvent(ResizeLocation resizeLocation) {
        this.resizeLocation = resizeLocation;
    }

    /**
     * @return the location where this resize was started
     */
    public ResizeLocation getResizeLocation() {
        return resizeLocation;
    }

    /**
     * Event type for click events. Represents the meta-data associated with
     * this event.
     */
    private static final Type<ResizableLayoutHandler> TYPE = new Type<ResizableLayoutHandler>();

    /**
     * Gets the event type associated with click events.
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
        handler.onResizeStart(this);
    }

}
