package com.vaadin.pekka.resizablecsslayout.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Widget that implements this interface is a source of {@link ResizeStartEvent}
 * and {@link ResizeEndEvent} events.
 */
public interface HasResizableLayoutHandlers extends HasHandlers {

    /**
     * Adds a handler for {@link ResizeStartEvent} to this widget.
     */
    HandlerRegistration addResizeStartHandler(ResizableLayoutHandler handler);

    /**
     * Adds a handler for {@link ResizeEndEvent} to this widget.
     */
    HandlerRegistration addResizeEndHandler(ResizableLayoutHandler handler);

    /**
     * Adds a handler for {@link ResizeCancelEvent} to this widget.
     */
    HandlerRegistration addResizeCancelHandler(ResizableLayoutHandler handler);
}
