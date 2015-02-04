package com.vaadin.pekka.resizablecsslayout.client;

import com.vaadin.shared.annotations.DelegateToWidget;

@SuppressWarnings("serial")
public class ResizableCssLayoutState extends
        com.vaadin.shared.ui.csslayout.CssLayoutState {

    @DelegateToWidget
    public boolean resizable;

    /**
     * Defaults to 10.
     */
    @DelegateToWidget
    public int resizeLocationSize = 10;

    @DelegateToWidget
    public boolean autoAcceptResize = true;
}