package com.vaadin.pekka.resizablecsslayout.client;

import com.google.gwt.event.shared.EventHandler;

public interface ResizableLayoutHandler extends EventHandler {

    /**
     * Fired when the user presses the mouse button down on one of the locations
     * ( {@link ResizeStartEvent#getResizeLocation()}).
     */
    void onResizeStart(ResizeStartEvent event);

    /**
     * Fired when the user releases the pressed the mouse button.
     */
    void onResizeEnd(ResizeEndEvent event);

    /**
     * Fired when the user cancels the resize by pressing the ESC key.
     */
    void onResizeCancel(ResizeCancelEvent resizeCancelEvent);

}
