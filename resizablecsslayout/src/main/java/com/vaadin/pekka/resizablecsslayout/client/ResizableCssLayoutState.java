package com.vaadin.pekka.resizablecsslayout.client;

import java.util.ArrayList;

import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.annotations.NoLayout;

@SuppressWarnings("serial")
public class ResizableCssLayoutState extends
        com.vaadin.shared.ui.csslayout.CssLayoutState {

    @NoLayout
    @DelegateToWidget
    public boolean resizable;

    /**
     * Defaults to 10.
     */
    @NoLayout
    @DelegateToWidget
    public int resizeLocationSize = 10;

    @NoLayout
    @DelegateToWidget
    public boolean autoAcceptResize = true;

    @NoLayout
    @DelegateToWidget
    public ArrayList<ResizeLocation> resizeLocations = new ArrayList<ResizeLocation>();

    @NoLayout
    @DelegateToWidget
    public boolean keepAspectRatio;
}