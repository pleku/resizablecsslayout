package com.vaadin.pekka.resizablecsslayout.demo;

import java.util.Set;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeCancelEvent;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeEndEvent;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeListener;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeStartEvent;
import com.vaadin.pekka.resizablecsslayout.client.ResizeLocation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Theme("demo")
@Title("ResizableCssLayout Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    private ResizableCssLayout gridWrapper;
    private ResizableCssLayout formWrapper;

    private CheckBox cancelResizeToggle;
    private CheckBox listenerToggle;

    public enum AvailableResizeLocations {
        All, Corners, Sides, Top_Left, Top, Top_Right, Right, Bottom_Right, Bottom, Bottom_Left, Left;
    }

    @Override
    protected void init(VaadinRequest request) {
        final Grid grid = createGrid();
        gridWrapper = new ResizableCssLayout();
        gridWrapper.setResizable(true);
        gridWrapper.setHeight("400px");
        gridWrapper.setWidth("400px");
        gridWrapper.addComponent(grid);
        gridWrapper.setCaption("Resize from grid's edges");

        Layout form = createForm(grid);
        formWrapper = new ResizableCssLayout(form);
        formWrapper.setResizable(true);
        formWrapper.setCaption("Resize form");
        formWrapper.setHeight("250px");
        formWrapper.setWidth("250px");

        final AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setSizeFull();
        absoluteLayout.addComponent(gridWrapper, "top:100px;left:100px;");
        absoluteLayout.addComponent(formWrapper, "right:100px; bottom:100px;");

        HorizontalLayout options = createOptions();

        final VerticalLayout layout = new VerticalLayout();
        layout.addComponent(options);
        layout.addComponent(absoluteLayout);
        layout.setComponentAlignment(absoluteLayout, Alignment.MIDDLE_LEFT);
        layout.setExpandRatio(absoluteLayout, 1.0F);
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.setSpacing(true);
        setContent(layout);

    }

    private Layout createForm(final Grid grid) {
        final VerticalLayout formLayout = new VerticalLayout();
        formLayout.setSizeFull();
        grid.addSelectionListener(new SelectionListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void select(SelectionEvent event) {
                formLayout.removeAllComponents();
                Set<Object> selected = event.getSelected();
                if (!selected.isEmpty()) {
                    Object next = selected.iterator().next();
                    BeanItem<GridExampleBean> beanItem = (BeanItem<GridExampleBean>) grid
                            .getContainerDataSource().getItem(next);
                    FieldGroup fieldGroup = new FieldGroup(beanItem);
                    fieldGroup.setBuffered(false);
                    for (Object propertyId : beanItem.getItemPropertyIds()) {
                        formLayout.addComponent(fieldGroup
                                .buildAndBind(propertyId));
                    }
                    for (Component component : formLayout) {
                        formLayout.setComponentAlignment(component,
                                Alignment.MIDDLE_CENTER);
                    }
                }
            }
        });
        grid.select(grid.getContainerDataSource().getIdByIndex(0));
        return formLayout;
    }

    private HorizontalLayout createOptions() {
        final CheckBox autoAcceptResize = new CheckBox("Auto accept resize",
                gridWrapper.isAutoAcceptResize());
        autoAcceptResize.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                gridWrapper.setAutoAcceptResize(autoAcceptResize.getValue());
                formWrapper.setAutoAcceptResize(autoAcceptResize.getValue());
                cancelResizeToggle.setEnabled(!autoAcceptResize.getValue());
            }
        });

        cancelResizeToggle = new CheckBox("Cancel resize on server");
        cancelResizeToggle.setEnabled(!gridWrapper.isAutoAcceptResize());
        cancelResizeToggle.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (cancelResizeToggle.getValue()) {
                    listenerToggle.setValue(true);
                }

            }
        });

        final CheckBox toggleResizable = new CheckBox("Toggle resizable");
        toggleResizable.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                gridWrapper.setResizable(toggleResizable.getValue());
                formWrapper.setResizable(toggleResizable.getValue());
            }
        });
        toggleResizable.setValue(true);

        final CheckBox aspectRatioToggle = new CheckBox("Keep Aspect Ratio");
        aspectRatioToggle.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                gridWrapper.setKeepAspectRatio(aspectRatioToggle.getValue());
                formWrapper.setKeepAspectRatio(aspectRatioToggle.getValue());
            }
        });

        listenerToggle = new CheckBox("Server side listener");
        listenerToggle.addValueChangeListener(new ValueChangeListener() {
            ResizeListener listener = new ResizeListener() {

                @Override
                public void resizeStart(ResizeStartEvent event) {
                    Notification.show("Resize Started",
                            "Location: " + event.getResizeLocation(),
                            Notification.Type.TRAY_NOTIFICATION);
                }

                @Override
                public void resizeEnd(ResizeEndEvent event) {
                    if (!gridWrapper.isAutoAcceptResize()
                            && cancelResizeToggle.getValue()) {
                        Notification.show("Resize Ended - Canceled",
                                "Width / Height: " + event.getWidth() + "/"
                                        + event.getHeight(),
                                Notification.Type.TRAY_NOTIFICATION);
                        gridWrapper.cancelResize();
                        formWrapper.cancelResize();
                    } else {
                        Notification.show("Resize Ended", "Width / Height: "
                                + event.getWidth() + "/" + event.getHeight(),
                                Notification.Type.TRAY_NOTIFICATION);
                    }
                }

                @Override
                public void resizeCancel(ResizeCancelEvent event) {
                    Notification.show("Resize Canceled",
                            Notification.Type.TRAY_NOTIFICATION);
                }
            };

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (listenerToggle.getValue()) {
                    gridWrapper.addResizeListener(listener);
                    formWrapper.addResizeListener(listener);
                } else {
                    gridWrapper.removeResizeListener(listener);
                    formWrapper.removeResizeListener(listener);
                }
            }
        });
        listenerToggle.setValue(true);

        final HorizontalLayout options = new HorizontalLayout();
        options.addComponent(toggleResizable);
        options.addComponent(listenerToggle);
        options.addComponent(autoAcceptResize);
        options.addComponent(cancelResizeToggle);
        options.addComponent(createResizeLocations());
        options.addComponent(aspectRatioToggle);
        for (Component component : options) {
            options.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
        }
        options.setWidth(null);
        options.setSpacing(true);
        return options;
    }

    private ComboBox createResizeLocations() {
        final ComboBox comboBox = new ComboBox("Available Resize Locations:");
        comboBox.addStyleName(ValoTheme.COMBOBOX_TINY);
        comboBox.addItems(AvailableResizeLocations.values());
        comboBox.select(AvailableResizeLocations.All);
        comboBox.setNullSelectionAllowed(false);
        comboBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                AvailableResizeLocations value = (AvailableResizeLocations) event
                        .getProperty().getValue();
                switch (value) {
                case All:
                    gridWrapper.setAllLocationsResizable();
                    formWrapper.setAllLocationsResizable();
                    break;
                case Corners:
                    gridWrapper.setCornersResizable();
                    formWrapper.setCornersResizable();
                    break;
                case Sides:
                    gridWrapper.setSidesResizable();
                    formWrapper.setSidesResizable();
                    break;
                default:
                    gridWrapper.setResizeLocations(ResizeLocation.valueOf(value
                            .toString().toUpperCase()));
                    formWrapper.setResizeLocations(ResizeLocation.valueOf(value
                            .toString().toUpperCase()));
                    break;
                }
            }
        });
        return comboBox;
    }

    private Grid createGrid() {
        BeanItemContainer<GridExampleBean> container = new BeanItemContainer<GridExampleBean>(
                GridExampleBean.class);
        for (int i = 0; i < 1000; i++) {
            container.addItem(new GridExampleBean("Bean " + i, i * i, i / 10d));
        }
        Grid grid = new Grid();
        grid.setContainerDataSource(container);
        grid.getColumn("name").setExpandRatio(2);
        grid.setSizeFull();
        grid.setSelectionMode(SelectionMode.SINGLE);
        return grid;
    }

    public class GridExampleBean {
        private String name;
        private int count;
        private double amount;

        public GridExampleBean() {
        }

        public GridExampleBean(String name, int count, double amount) {
            this.name = name;
            this.count = count;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        public double getAmount() {
            return amount;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getSum() {
            return getAmount() * getCount();
        }
    }
}
