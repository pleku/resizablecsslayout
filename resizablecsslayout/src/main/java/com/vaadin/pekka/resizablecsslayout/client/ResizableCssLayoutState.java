package com.vaadin.pekka.resizablecsslayout.client;

import java.util.ArrayList;

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

    @DelegateToWidget
    public ArrayList<ResizeLocation> resizeLocations = new ArrayList<ResizeLocation>();
}