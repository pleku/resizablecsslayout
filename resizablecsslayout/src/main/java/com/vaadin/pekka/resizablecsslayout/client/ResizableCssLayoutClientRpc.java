package com.vaadin.pekka.resizablecsslayout.client;

import com.vaadin.shared.communication.ClientRpc;

public interface ResizableCssLayoutClientRpc extends ClientRpc {

    public void acceptResize(boolean acceptResize);

}