package com.vaadin.pekka.resizablecsslayout.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeCancelEvent;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeEndEvent;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeListener;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeStartEvent;
import com.vaadin.pekka.resizablecsslayout.client.ResizeLocation;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Theme("demo")
@Title("ResizableCssLayout Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    public static final int NUMBER_OR_ROWS = 100;
    private ResizableCssLayout gridWrapper;
    private ResizableCssLayout formWrapper;

    private CheckBox cancelResizeToggle;
    private CheckBox listenerToggle;
    private ResizableCssLayout imageWrapper;

    public enum AvailableResizeLocations {
        All, Corners, Sides, Top_Left, Top, Top_Right, Right, Bottom_Right, Bottom, Bottom_Left, Left;
    }

    @Override
    protected void init(VaadinRequest request) {
        final Grid<ExampleBean> grid = createGrid();
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

        Image image = new Image(null, new ThemeResource("img/swan.jpg"));
        image.setSizeFull();
        imageWrapper = new ResizableCssLayout(image);
        imageWrapper.setResizable(true);
        imageWrapper.setKeepAspectRatio(true);
        imageWrapper.setCaption("Image keeps aspect ratio");
        imageWrapper.setWidth("250px");
        imageWrapper.setHeight("167px");

        final AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setSizeFull();
        absoluteLayout.addComponent(gridWrapper, "top:50px; left:50px;");
        absoluteLayout.addComponent(formWrapper, "right:100px; bottom:100px;");
        absoluteLayout.addComponent(imageWrapper, "top:50px; left:500px");

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

    private Layout createForm(final Grid<ExampleBean> grid) {
        TextField name = new TextField("Name");
        name.setWidth("100%");

        ComboBox<Integer> count = new ComboBox<>("Count");
        count.setItems(IntStream.range(0, NUMBER_OR_ROWS + 1).boxed());
        count.setWidth("100px");

        TextField amount = new TextField("Amount");
        amount.setWidth("100%");

        final FormLayout formLayout = new FormLayout(name, count, amount);
        formLayout.setSizeFull();

        Binder<ExampleBean> binder = new Binder<>();
        binder.forField(name).bind(ExampleBean::getName, ExampleBean::setName);
        binder.forField(count).bind(ExampleBean::getCount, ExampleBean::setCount);
        binder.forField(amount).withConverter(new StringToDoubleConverter("Please enter a decimal number")).bind(ExampleBean::getAmount, ExampleBean::setAmount);

        grid.addSelectionListener(event -> {
            binder.setBean(event.getFirstSelectedItem().orElse(null));
        });
        binder.addValueChangeListener(event -> grid.getDataProvider().refreshItem(binder.getBean()));
        grid.select(((ListDataProvider<ExampleBean>) grid.getDataProvider()).getItems().iterator().next());
        return formLayout;
    }

    private HorizontalLayout createOptions() {
        final CheckBox autoAcceptResize = new CheckBox("Auto accept resize",
                gridWrapper.isAutoAcceptResize());
        autoAcceptResize.addValueChangeListener(event -> {
            gridWrapper.setAutoAcceptResize(autoAcceptResize.getValue());
            formWrapper.setAutoAcceptResize(autoAcceptResize.getValue());
            imageWrapper.setAutoAcceptResize(autoAcceptResize.getValue());
            cancelResizeToggle.setEnabled(!autoAcceptResize.getValue());
        });

        cancelResizeToggle = new CheckBox("Cancel resize on server");
        cancelResizeToggle.setEnabled(!gridWrapper.isAutoAcceptResize());
        cancelResizeToggle.addValueChangeListener(event -> {
            if (cancelResizeToggle.getValue()) {
                listenerToggle.setValue(true);
            }
        });

        final CheckBox toggleResizable = new CheckBox("Toggle resizable");
        toggleResizable.setValue(true);
        toggleResizable.addValueChangeListener(event -> {
            gridWrapper.setResizable(toggleResizable.getValue());
            formWrapper.setResizable(toggleResizable.getValue());
            imageWrapper.setResizable(toggleResizable.getValue());
        });

        final CheckBox aspectRatioToggle = new CheckBox("Keep Aspect Ratio");
        aspectRatioToggle.addValueChangeListener(event -> {
            gridWrapper.setKeepAspectRatio(aspectRatioToggle.getValue());
            formWrapper.setKeepAspectRatio(aspectRatioToggle.getValue());
        });

        listenerToggle = new CheckBox("Server side listener");
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
                    gridWrapper.cancelResize();
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

        listenerToggle.addValueChangeListener(event -> {
            if (listenerToggle.getValue()) {
                gridWrapper.addResizeListener(listener);
                formWrapper.addResizeListener(listener);
                imageWrapper.addResizeListener(listener);
            } else {
                gridWrapper.removeResizeListener(listener);
                formWrapper.removeResizeListener(listener);
                imageWrapper.removeResizeListener(listener);
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
        final ComboBox<AvailableResizeLocations> comboBox = new ComboBox<>("Available Resize Locations:");
        comboBox.addStyleName(ValoTheme.COMBOBOX_TINY);
        comboBox.setItems(AvailableResizeLocations.values());
        comboBox.setValue(AvailableResizeLocations.All);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.addValueChangeListener(event -> {
            AvailableResizeLocations value = event
                    .getValue();
            final ResizeLocation resizeLocation = ResizeLocation
                    .valueOf(value.toString().toUpperCase());
            switch (value) {
                case All:
                    gridWrapper.setAllLocationsResizable();
                    formWrapper.setAllLocationsResizable();
                    imageWrapper.setAllLocationsResizable();
                    break;
                case Corners:
                    gridWrapper.setCornersResizable();
                    formWrapper.setCornersResizable();
                    imageWrapper.setCornersResizable();
                    break;
                case Sides:
                    gridWrapper.setSidesResizable();
                    formWrapper.setSidesResizable();
                    imageWrapper.setCornersResizable();
                    break;
                default:
                    gridWrapper.setResizeLocations(resizeLocation);
                    formWrapper.setResizeLocations(resizeLocation);
                    imageWrapper.setResizeLocations(resizeLocation);
                    break;
            }
        });
        return comboBox;
    }

    private Grid<ExampleBean> createGrid() {
        List<ExampleBean> items = new ArrayList<>();
        for (int i = 0; i < NUMBER_OR_ROWS; i++) {
            items.add(new ExampleBean("Bean " + i, i, new BigDecimal(i).divide(new BigDecimal(10)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()));
        }
        Grid<ExampleBean> grid = new Grid<>(ExampleBean.class);
        grid.getColumn("name").setExpandRatio(2);
        grid.setSizeFull();
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setItems(items);
        return grid;
    }

    public static class ExampleBean {
        private String name;
        private Integer count;
        private double amount;

        public ExampleBean() {
        }

        public ExampleBean(String name, int count, double amount) {
            this.name = name;
            this.count = count;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public Integer getCount() {
            return count;
        }

        public double getAmount() {
            return amount;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getSum() {
            return new BigDecimal(getAmount() * getCount()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
    }
}
