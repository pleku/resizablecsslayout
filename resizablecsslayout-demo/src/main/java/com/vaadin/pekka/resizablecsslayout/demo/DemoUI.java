package com.vaadin.pekka.resizablecsslayout.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeEndEvent;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeListener;
import com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeStartEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
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
        final Grid grid = createGrid();
        final ResizableCssLayout gridWrapper = new ResizableCssLayout();
        gridWrapper.setHeight("400px");
        gridWrapper.setWidth("400px");
        gridWrapper.addResizeListener(new ResizeListener() {

            @Override
            public void resizeStart(ResizeStartEvent event) {
            }

            @Override
            public void resizeEnd(ResizeEndEvent event) {
                if (!gridWrapper.isAutoAcceptResize()
                        && cancelResize.getValue()) {
                    gridWrapper.cancelResize();
                } else {
                    // used to make the grid columns resize
                    grid.getColumn("name").setExpandRatio(
                            grid.getColumn("name").getExpandRatio() == 2 ? 3
                                    : 2);
                }
            }
        });
        gridWrapper.addComponent(grid);

        final AbsoluteLayout absoluteLayout = new AbsoluteLayout();
        absoluteLayout.setSizeFull();
        absoluteLayout.addComponent(gridWrapper, "top:100px;left:100px;");

        final CheckBox autoAcceptResize = new CheckBox("Auto accept resize",
                gridWrapper.isAutoAcceptResize());
        autoAcceptResize.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                gridWrapper.setAutoAcceptResize(autoAcceptResize.getValue());
                cancelResize.setEnabled(!autoAcceptResize.getValue());
            }
        });

        cancelResize = new CheckBox("Cancel resize on server");
        cancelResize.setEnabled(!gridWrapper.isAutoAcceptResize());

        final CheckBox toggleResizable = new CheckBox("Toggle resizable");
        toggleResizable.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                gridWrapper.setResizable(toggleResizable.getValue());
            }
        });
        toggleResizable.setValue(true);

        final HorizontalLayout options = new HorizontalLayout();
        options.addComponent(toggleResizable);
        options.addComponent(autoAcceptResize);
        options.addComponent(cancelResize);
        options.setWidth(null);
        options.setSpacing(true);

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

    private Grid createGrid() {
        BeanItemContainer<GridExampleBean> container = new BeanItemContainer<GridExampleBean>(
                GridExampleBean.class);
        for (int i = 0; i < 1000; i++) {
            container.addItem(new GridExampleBean("Bean " + i, i * i, i / 10d));
        }
        Grid grid = new Grid();
        grid.setContainerDataSource(container);
        grid.getColumn("name").setExpandRatio(2);
        grid.getColumn("amount").setExpandRatio(1);
        grid.getColumn("count").setExpandRatio(1);
        grid.setSizeFull();
        grid.setSelectionMode(SelectionMode.NONE);
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
    }
}
