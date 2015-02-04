package com.vaadin.pekka.resizablecsslayout.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeEndEvent;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeListener;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeStartEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("ResizableCssLayout Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    private CheckBox cancelResize;

    @Override
    protected void init(VaadinRequest request) {
        final ResizableCssLayout component = new ResizableCssLayout();
        component.setHeight("200px");
        component.setWidth("200px");
        component.addResizeListener(new ResizeListener() {

            @Override
            public void resizeStart(ResizeStartEvent event) {
            }

            @Override
            public void resizeEnd(ResizeEndEvent event) {
                if (cancelResize.getValue()) {
                    component.cancelResize();
                }
            }
        });

        Button button = new Button("Toggle resizable",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        component.setResizable(!component.isResizable());
                    }
                });

        button.setWidth("100%");
        component.addComponent(button);

        final AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setCaption("AbsoluteLayout 80% x 80%");
        absoluteLayout.setWidth("80%");
        absoluteLayout.setHeight("80%");
        CustomComponent wrapper = new CustomComponent(component);
        wrapper.setSizeUndefined();
        absoluteLayout.addComponent(wrapper, "top:100px;left:100px;");

        final CheckBox autoAcceptResize = new CheckBox("Auto accept resize",
                component.isAutoAcceptResize());
        autoAcceptResize.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                component.setAutoAcceptResize(autoAcceptResize.getValue());
                cancelResize.setEnabled(!autoAcceptResize.getValue());
            }
        });

        cancelResize = new CheckBox("Cancel resize on server");
        cancelResize.setEnabled(!component.isAutoAcceptResize());

        final VerticalLayout options = new VerticalLayout();
        options.addComponent(autoAcceptResize);
        options.addComponent(cancelResize);
        options.setWidth(null);
        options.setSpacing(true);
        options.setMargin(true);

        final HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(options);
        layout.addComponent(absoluteLayout);
        layout.setComponentAlignment(absoluteLayout, Alignment.MIDDLE_LEFT);
        layout.setExpandRatio(absoluteLayout, 1.0F);
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        setContent(layout);
    }
}
