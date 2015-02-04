package com.vaadin.pekka.resizablecsslayout;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@SuppressWarnings("serial")
public class TestUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        final ResizableCssLayout component = new ResizableCssLayout();
        component.setHeight("400px");
        component.setWidth("400px");
        component.addComponent(new Button("Lorem lipsum",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        component.setResizable(!component.isResizable());
                    }
                }));
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.addComponent(component);
        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
        setContent(layout);
    }
}
