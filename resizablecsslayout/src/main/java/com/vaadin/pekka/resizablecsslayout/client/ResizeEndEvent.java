package com.vaadin.pekka.resizablecsslayout.client;

import com.google.gwt.event.shared.GwtEvent;

/**
 * GWT event for resize end. Fired when the user stops the resize by releasing
 * the pressed mouse button. Contains the resized new width and height for the
 * event source.
 */
public class ResizeEndEvent extends GwtEvent<ResizableLayoutHandler> {

    private final int height;
    private final int width;

    public ResizeEndEvent(int height, int width) {
        this.height = height;
        this.width = width;
    }

    /**
     * @return the proposed resized height for the event source.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the proposed resized width for the event source.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Event type for resize end events.
     */
    private static final Type<ResizableLayoutHandler> TYPE = new Type<ResizableLayoutHandler>();

    /**
     * Gets the event type associated with resize end events.
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
        handler.onResizeEnd(this);
    }
}
