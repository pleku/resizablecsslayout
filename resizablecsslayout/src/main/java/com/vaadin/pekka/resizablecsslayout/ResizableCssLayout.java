package com.vaadin.pekka.resizablecsslayout;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import com.vaadin.pekka.resizablecsslayout.client.ResizableCssLayoutClientRpc;
import com.vaadin.pekka.resizablecsslayout.client.ResizableCssLayoutServerRpc;
import com.vaadin.pekka.resizablecsslayout.client.ResizableCssLayoutState;
import com.vaadin.pekka.resizablecsslayout.client.ResizeLocation;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HasComponents;
import com.vaadin.util.ReflectTools;

/**
 * A CssLayout that is resizable from all the sides and corners when
 * {@link #setResizable(boolean)} is set to <code>true</code>.
 */
@SuppressWarnings("serial")
public class ResizableCssLayout extends com.vaadin.ui.CssLayout {

    private ResizeLocation latestResizeLocation;

    private boolean resizing;

    private boolean acceptResize;

    private int pendingWidth;
    private int pendingHeight;
    private int initialWidth;
    private int initialHeight;

    private ResizableCssLayoutServerRpc rpc = new ResizableCssLayoutServerRpc() {
        @Override
        public void onResizeStart(ResizeLocation resizeLocation, int height,
                int width) {
            acceptResize = true;
            latestResizeLocation = resizeLocation;
            initialHeight = height;
            initialWidth = width;
            resizing = true;
            fireResizeStart(resizeLocation, initialHeight, initialWidth);
        }

        @Override
        public void onResizeEnd(int height, int width) {
            pendingHeight = height;
            pendingWidth = width;
            fireResizeEnd(height, width);
            resizing = false;
            respondResizeAcceptance();
        }

        @Override
        public void onResizeCancel() {
            resizing = false;
            fireResizeCancel();
        }

    };

    /**
     * Constructs an empty ResizableCssLayout.
     */
    public ResizableCssLayout() {
        registerRpc(rpc);
        setAllLocationsResizable();
    }

    /**
     * Constructs a ResizableCssLayout with the given components in the given
     * order.
     */
    public ResizableCssLayout(Component... children) {
        this();
        addComponents(children);
    }

    /**
     * When the component is not in auto accept resize mode (
     * {@link #isAutoAcceptResize()} and there is a resize pending
     * {@link #isResizing()}, the resize can be cancelled with this method from
     * a
     * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeListener}
     * when the the
     * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeEndEvent}
     * events are fired.
     * <p>
     * If this method is not triggered, the resize will be accepted.
     */
    public void cancelResize() {
        acceptResize = false;
    }

    /**
     * Responds the acceptance to the resize if not in auto accept mode.
     */
    protected void respondResizeAcceptance() {
        if (!getState(false).autoAcceptResize) {
            getRpcProxy(ResizableCssLayoutClientRpc.class).acceptResize(
                    acceptResize);
            if (acceptResize) {
                internalAccept();
            }
        } else {
            internalAccept();
        }
    }

    /**
     * Updates the new resized size of the component. Moves the component if it
     * is inside a {@link com.vaadin.ui.AbsoluteLayout} and the coordinates need
     * to be updated.
     */
    protected void internalAccept() {
        HasComponents parentContainer = getParent();
        Component positionHolder = this;
        while (parentContainer instanceof CustomComponent) {
            HasComponents parentParent = parentContainer.getParent();
            if (parentParent != null) {
                positionHolder = parentContainer;
                parentContainer = parentParent;
            } else {
                break;
            }
        }

        if (parentContainer instanceof AbsoluteLayout) {
            AbsoluteLayout absoluteLayout = (AbsoluteLayout) parentContainer;
            switch (latestResizeLocation) {
            case TOP_LEFT:
                moveLeft(initialWidth - pendingWidth, absoluteLayout,
                        positionHolder);
            case TOP:
                moveTop(initialHeight - pendingHeight, absoluteLayout,
                        positionHolder);
                break;
            case TOP_RIGHT:
                moveTop(initialHeight - pendingHeight, absoluteLayout,
                        positionHolder);
            case RIGHT:
                moveRight(initialWidth - pendingWidth, absoluteLayout,
                        positionHolder);
                break;
            case BOTTOM_RIGHT:
                moveRight(initialWidth - pendingWidth, absoluteLayout,
                        positionHolder);
            case BOTTOM:
                moveBottom(initialHeight - pendingHeight, absoluteLayout,
                        positionHolder);
                break;
            case BOTTOM_LEFT:
                moveBottom(initialHeight - pendingHeight, absoluteLayout,
                        positionHolder);
            case LEFT:
                moveLeft(initialWidth - pendingWidth, absoluteLayout,
                        positionHolder);
                break;
            default:
                break;
            }
        }
        setWidth(pendingWidth, Unit.PIXELS);
        setHeight(pendingHeight, Unit.PIXELS);
    }

    /**
     * Adjusts the top position for the component when inside a
     * {@link com.vaadin.ui.AbsoluteLayout} and top position was set with
     * pixels.
     *
     * @param delta
     *            the change of top position in pixels
     * @param the
     *            absolute layout that contains the component
     * @param positionHolder
     *            the component that is inside the absolute layout
     */
    protected void moveTop(int delta, AbsoluteLayout absoluteLayout,
            Component positionHolder) {
        ComponentPosition position = absoluteLayout.getPosition(positionHolder);
        if (position.getTopValue() != null
                && position.getTopUnits().equals(Unit.PIXELS)) {
            float newValue = (position.getTopValue() + delta);
            if (newValue < 0) {
                newValue = 0;
            }
            position.setTopValue(newValue);
            absoluteLayout.setPosition(positionHolder, position);
        } else {
            // position bottom set, NOOP
        }
    }

    /**
     * Adjusts the bottom position for the component when inside a
     * {@link com.vaadin.ui.AbsoluteLayout} and bottom position was set with
     * pixels.
     *
     * @param delta
     *            the change of bottom position in pixels
     * @param the
     *            absolute layout that contains the component
     * @param positionHolder
     *            the component that is inside the absolute layout
     */
    protected void moveBottom(int delta, AbsoluteLayout absoluteLayout,
            Component positionHolder) {
        ComponentPosition position = absoluteLayout.getPosition(positionHolder);
        if (position.getBottomValue() != null
                && position.getBottomUnits().equals(Unit.PIXELS)) {
            float newValue = (position.getBottomValue() + delta);
            if (newValue < 0) {
                newValue = 0;
            }
            position.setBottomValue(newValue);
            absoluteLayout.setPosition(positionHolder, position);
        } else {
            // position top used, NOOP
        }
    }

    /**
     * Adjusts the right position for the component when inside a
     * {@link com.vaadin.ui.AbsoluteLayout} and right position was set with
     * pixels.
     *
     * @param delta
     *            the change of right position in pixels
     * @param the
     *            absolute layout that contains the component
     * @param positionHolder
     */
    protected void moveRight(int delta, AbsoluteLayout absoluteLayout,
            Component positionHolder) {
        ComponentPosition position = absoluteLayout.getPosition(positionHolder);
        if (position.getRightValue() != null
                && position.getRightUnits().equals(Unit.PIXELS)) {
            float newValue = (position.getRightValue() + delta);
            if (newValue < 0) {
                newValue = 0;
            }
            position.setRightValue(newValue);
            absoluteLayout.setPosition(positionHolder, position);
        } else {
            // position left used, NOOP
        }
    }

    /**
     * Adjusts the left position for the component when inside a
     * {@link com.vaadin.ui.AbsoluteLayout} and left position was set with
     * pixels.
     *
     * @param delta
     *            the change of left position in pixels
     * @param the
     *            absolute layout that contains the component
     * @param positionHolder
     *            the component that is inside the absolute layout
     */
    protected void moveLeft(int delta, AbsoluteLayout absoluteLayout,
            Component positionHolder) {
        ComponentPosition position = absoluteLayout.getPosition(positionHolder);
        if (position.getLeftValue() != null
                && position.getLeftUnits().equals(Unit.PIXELS)) {
            float newValue = (position.getLeftValue() + delta);
            if (newValue < 0) {
                newValue = 0;
            }
            position.setLeftValue(newValue);
            absoluteLayout.setPosition(positionHolder, position);
        } else {
            // position right set, NOOP
        }
    }

    /**
     * Sets the auto accept resize mode. When set to <code>true</code>, the
     * client side widget will accept the resize immediately and the resize
     * can't be cancelled. When set to <code>false</code>, the resize can be
     * cancelled during a
     * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeEndEvent}
     * with a
     * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeListener}
     * by using {@link #cancelResize()}.
     */
    public void setAutoAcceptResize(boolean autoAcceptResize) {
        if (getState(false).autoAcceptResize != autoAcceptResize) {
            getState().autoAcceptResize = autoAcceptResize;
        }
    }

    /**
     * Is the component in auto accept mode. See
     * {@link #setAutoAcceptResize(boolean)} for more info.
     */
    public boolean isAutoAcceptResize() {
        return getState(false).autoAcceptResize;
    }

    /**
     * Sets the resize mode on/off.
     */
    public void setResizable(boolean resizable) {
        if (getState(false).resizable != resizable) {
            getState().resizable = resizable;
        }
    }

    /**
     * Is the component in resize mode.
     */
    public boolean isResizable() {
        return getState(false).resizable;
    }

    /**
     * Is the component currently being resized.
     */
    public boolean isResizing() {
        return resizing;
    }

    /**
     * Returns the location where the most recent
     * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeStartEvent}
     * happened.
     */
    public ResizeLocation getLatestResizeLocation() {
        return latestResizeLocation;
    }

    /**
     * Sets the sizing in pixels for the resize locations (
     * {@link ResizeLocation}) as follows:
     * <ul>
     * <li>height for {@link ResizeLocation#TOP} & {@link ResizeLocation#BOTTOM}
     * </li>
     * <li>width for {@link ResizeLocation#LEFT} & {@link ResizeLocation#RIGHT}</li>
     * <li>width & height for all the corners</li>
     * </ul>
     * The areas are a half inside and a half outside the ResizableCssLayout
     * component.
     * <p>
     * The default size is {@link ResizableCssLayoutState#resizeLocationSize}
     */
    public void setResizeLocationSize(int resizeLocationSize) {
        if (getState(false).resizeLocationSize != resizeLocationSize) {
            getState().resizeLocationSize = resizeLocationSize;
        }
    }

    /**
     * Returns the current size of the resize locations in pixels. See
     * {@link #setResizeLocationSize(int)} for more info.
     */
    public int getResizeLocationSize() {
        return getState(false).resizeLocationSize;
    }

    /**
     * Returns the current resize locations.
     */
    public Collection<ResizeLocation> getResizeLocations() {
        return getState(false).resizeLocations;
    }

    /**
     * Sets the available locations for resize drag and drop. By default all
     * sides and corners are available for resize.
     * <p>
     * See {@link #setAllLocationsResizable()}, {@link #setCornersResizable()}
     * and {@link #setSidesResizable()} for shortcuts.
     */
    public void setResizeLocations(Collection<ResizeLocation> resizeLocations) {
        ResizableCssLayoutState state = getState();
        state.resizeLocations.clear();
        state.resizeLocations.addAll(resizeLocations);
    }

    /**
     * Shorthand for {@link #setResizeLocations(Collection)}.
     */
    public void setResizeLocations(ResizeLocation... resizeLocations) {
        setResizeLocations(Arrays.asList(resizeLocations));
    }

    /**
     * Enables resizing from all sides and corners.
     */
    public void setAllLocationsResizable() {
        setResizeLocations(ResizeLocation.values());
    }

    /**
     * Enables resizing from corners only.
     */
    public void setCornersResizable() {
        setResizeLocations(ResizeLocation.TOP_LEFT, ResizeLocation.TOP_RIGHT,
                ResizeLocation.BOTTOM_LEFT, ResizeLocation.BOTTOM_RIGHT);
    }

    /**
     * Enables resizing from sides only.
     */
    public void setSidesResizable() {
        setResizeLocations(ResizeLocation.TOP, ResizeLocation.RIGHT,
                ResizeLocation.BOTTOM, ResizeLocation.LEFT);
    }

    /**
     * Set the component to keep or not to keep the original (current) aspect
     * ratio when the user starts the resizing.
     * <p>
     * Default is <code>false</code>.
     */
    public void setKeepAspectRatio(boolean keepAspectRatio) {
        if (getState(false).keepAspectRatio != keepAspectRatio) {
            getState().keepAspectRatio = keepAspectRatio;
        }
    }

    /**
     * Returns whether the component keep the original (current) aspect ratio
     * when the user starts the resizing.
     * <p>
     * Default is <code>false</code>.
     */
    public boolean isKeepAspectRatio() {
        return getState(false).keepAspectRatio;
    }

    @Override
    public ResizableCssLayoutState getState() {
        return (ResizableCssLayoutState) super.getState();
    }

    @Override
    protected ResizableCssLayoutState getState(boolean markAsDirty) {
        return (ResizableCssLayoutState) super.getState(markAsDirty);
    }

    /**
     * Adds a resize listener for
     * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeStartEvent}
     * and
     * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeEndEvent}
     * for this component.
     */
    public void addResizeListener(ResizeListener listener) {
        addListener(ResizeStartEvent.class, listener,
                ResizeListener.RESIZE_START_METHOD);
        addListener(ResizeEndEvent.class, listener,
                ResizeListener.RESIZE_END_METHOD);
        addListener(ResizeCancelEvent.class, listener,
                ResizeListener.RESIZE_CANCEL_METHOD);
    }

    /**
     * Removes the resize listener from this component.
     */
    public void removeResizeListener(ResizeListener listener) {
        removeListener(ResizeStartEvent.class, listener);
        removeListener(ResizeEndEvent.class, listener);
    }

    protected void fireResizeStart(ResizeLocation resizeLocation, int height,
            int width) {
        fireEvent(new ResizeStartEvent(this, resizeLocation, height, width));
    }

    protected void fireResizeEnd(int height, int width) {
        fireEvent(new ResizeEndEvent(this, height, width));
    }

    protected void fireResizeCancel() {
        fireEvent(new ResizeCancelEvent(this));
    }

    /**
     * Interface for listening to
     * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeStartEvent}
     * and
     * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout.ResizeEndEvent}
     * in {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout}
     * component.
     */
    public interface ResizeListener extends Serializable {

        public static final Method RESIZE_START_METHOD = ReflectTools
                .findMethod(ResizeListener.class, "resizeStart",
                        ResizeStartEvent.class);
        public static final Method RESIZE_END_METHOD = ReflectTools.findMethod(
                ResizeListener.class, "resizeEnd", ResizeEndEvent.class);

        public static final Method RESIZE_CANCEL_METHOD = ReflectTools
                .findMethod(ResizeListener.class, "resizeCancel",
                        ResizeCancelEvent.class);

        void resizeStart(ResizeStartEvent event);

        void resizeEnd(ResizeEndEvent event);

        void resizeCancel(ResizeCancelEvent event);
    }

    /**
     * Event for resize start, fired when the user has pressed the mouse down on
     * the resize location ({@link #getResizeLocation()}.
     */
    public static class ResizeStartEvent extends Event {

        private final ResizeLocation resizeLocation;
        private final int height;
        private final int width;

        public ResizeStartEvent(Component source,
                ResizeLocation resizeLocation, int height, int width) {
            super(source);
            this.resizeLocation = resizeLocation;
            this.height = height;
            this.width = width;
        }

        /**
         * The location where the resize was started on.
         */
        public ResizeLocation getResizeLocation() {
            return resizeLocation;
        }

        /**
         * The current client side height of the
         * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout} .
         */
        public int getHeight() {
            return height;
        }

        /**
         * The current client side width of the
         * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout} .
         */
        public int getWidth() {
            return width;
        }
    }

    /**
     * Event for resize end, fired when the user has released the mouse button.
     * This event can be used for cancelling the resize, if the component is not
     * in auto accept resize mode (
     * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout#isAutoAcceptResize()}
     * ).
     */
    public static class ResizeEndEvent extends Event {

        private final int height;
        private final int width;

        public ResizeEndEvent(Component source, int height, int width) {
            super(source);
            this.height = height;
            this.width = width;
        }

        /**
         * The new resized height or proposed height in pixels (pending
         * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout#isAutoAcceptResize()}
         * ) for the event source.
         */
        public int getHeight() {
            return height;
        }

        /**
         * The new resized width or proposed width in pixels (pending
         * {@link com.vaadin.pekka.resizablecsslayout.ResizableCssLayout#isAutoAcceptResize()}
         * ) for the event source.
         */
        public int getWidth() {
            return width;
        }
    }

    /**
     * Event for resize cancel, fired when the user has canceled the resize by
     * pressing the ESC key. The resizing is no longer active and the component
     * size has not been changed by the resize.
     */
    public static class ResizeCancelEvent extends Event {

        public ResizeCancelEvent(Component source) {
            super(source);
        }

    }
}
