package com.vaadin.pekka.resizablecsslayout.client;

import com.vaadin.shared.communication.ServerRpc;

public interface ResizableCssLayoutServerRpc extends ServerRpc {

    void onResizeStart(ResizeLocation resizeLocation, int height, int width);

    void onResizeEnd(int height, int width);

    void onResizeCancel();
}
