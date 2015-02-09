package com.vaadin.pekka.resizablecsslayout.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.vaadin.client.ui.VCssLayout;

/**
 * Widget that wraps CssLayout providing resizability from all sides and
 * corners. Resizing is started on
 * {@link com.google.gwt.user.client.Event#ONMOUSEDOWN} on one of the locations
 * ({@link ResizeLocation}) and ended on
 * {@link com.google.gwt.user.client.Event#ONMOUSEUP}.
 * <p>
 * If the component is in {@link #isAutoAcceptResize()} mode, the resize is done
 * immediately. If not, then resize must be accepted or canceled using
 * {@link #acceptResize(boolean)}.
 * <p>
 * Boundaries for the resize can be specified by using
 * {@link #setResizeBoundaryElement(com.google.gwt.dom.client.Element)}.
 * <p>
 * The size of the resizable areas can be specified with
 * {@link #setResizeLocationSize(int)}. The areas are a half outside of the
 * component and a half inside the component.
 */
public class ResizableVCssLayout extends VCssLayout implements
        HasResizableLayoutHandlers {

    private static final String RESIZABLE_STYLE_NAME = "resizable";
    private static final int DEFAULT_DRAG_SIZE_PIXELS = 10;
    private static final String UNUSED_STYLE_NAME = "unused";
    private DivElement topLeftCorner;
    private DivElement topRightCorner;
    private DivElement bottomLeftCorner;
    private DivElement bottomRightCorner;
    private DivElement topSide;
    private DivElement rightSide;
    private DivElement bottomSide;
    private DivElement leftSide;
    private DivElement dragOverlayElement;

    private final ResizeHandler resizeHandler;
    private boolean resizable;
    private boolean autoAcceptResize = true;
    private Element boundaryElement;
    private int dragSizePixels;
    private HashSet<ResizeLocation> resizeLocations = new HashSet<ResizeLocation>();

    @SuppressWarnings("serial")
    private static final Map<ResizeLocation, String> locationToCursorMap = new HashMap<ResizeLocation, String>() {
        {
            put(ResizeLocation.TOP_LEFT, "nwse-resize");
            put(ResizeLocation.TOP, "ns-resize");
            put(ResizeLocation.TOP_RIGHT, "nesw-resize");
            put(ResizeLocation.RIGHT, "ew-resize");
            put(ResizeLocation.BOTTOM_RIGHT, "nwse-resize");
            put(ResizeLocation.BOTTOM, "ns-resize");
            put(ResizeLocation.BOTTOM_LEFT, "nesw-resize");
            put(ResizeLocation.LEFT, "ew-resize");
        }
    };

    public ResizableVCssLayout() {
        super();
        resizeHandler = new ResizeHandler();

        initDOM();
        initListeners();
        setResizeLocationSize(DEFAULT_DRAG_SIZE_PIXELS);
    }

    private void initDOM() {
        Document document = Document.get();
        topLeftCorner = document.createDivElement();
        topLeftCorner.addClassName("tlc");
        topRightCorner = document.createDivElement();
        topRightCorner.addClassName("trc");
        bottomLeftCorner = document.createDivElement();
        bottomLeftCorner.addClassName("blc");
        bottomRightCorner = document.createDivElement();
        bottomRightCorner.addClassName("brc");

        topSide = document.createDivElement();
        topSide.addClassName("ts");
        rightSide = document.createDivElement();
        rightSide.addClassName("rs");
        bottomSide = document.createDivElement();
        bottomSide.addClassName("bs");
        leftSide = document.createDivElement();
        leftSide.addClassName("ls");

        dragOverlayElement = document.createDivElement();
        dragOverlayElement.addClassName("drag-overlay");

        topSide.appendChild(topLeftCorner);
        topSide.appendChild(topRightCorner);

        bottomSide.appendChild(bottomLeftCorner);
        bottomSide.appendChild(bottomRightCorner);
    }

    private void initListeners() {
        Event.sinkEvents(topLeftCorner, Event.MOUSEEVENTS);
        Event.sinkEvents(topRightCorner, Event.MOUSEEVENTS);
        Event.sinkEvents(bottomLeftCorner, Event.MOUSEEVENTS);
        Event.sinkEvents(bottomRightCorner, Event.MOUSEEVENTS);
        Event.sinkEvents(topSide, Event.MOUSEEVENTS);
        Event.sinkEvents(rightSide, Event.MOUSEEVENTS);
        Event.sinkEvents(bottomSide, Event.MOUSEEVENTS);
        Event.sinkEvents(leftSide, Event.MOUSEEVENTS);
    }

    protected void setupResizeLocations() {
        if (resizeLocations.contains(ResizeLocation.TOP_LEFT)) {
            enableResizeLocation(topLeftCorner);
        } else {
            disableResizeLocation(topLeftCorner);
        }
        if (resizeLocations.contains(ResizeLocation.TOP_RIGHT)) {
            enableResizeLocation(topRightCorner);
        } else {
            disableResizeLocation(topRightCorner);
        }
        if (resizeLocations.contains(ResizeLocation.BOTTOM_RIGHT)) {
            enableResizeLocation(bottomRightCorner);
        } else {
            disableResizeLocation(bottomRightCorner);
        }
        if (resizeLocations.contains(ResizeLocation.BOTTOM_LEFT)) {
            enableResizeLocation(bottomLeftCorner);
        } else {
            disableResizeLocation(bottomLeftCorner);
        }
        if (resizeLocations.contains(ResizeLocation.TOP)) {
            enableResizeLocation(topSide);
        } else {
            disableResizeLocation(topSide);
        }
        if (resizeLocations.contains(ResizeLocation.LEFT)) {
            enableResizeLocation(leftSide);
        } else {
            disableResizeLocation(leftSide);
        }
        if (resizeLocations.contains(ResizeLocation.RIGHT)) {
            enableResizeLocation(rightSide);
        } else {
            disableResizeLocation(rightSide);
        }
        if (resizeLocations.contains(ResizeLocation.BOTTOM)) {
            enableResizeLocation(bottomSide);
        } else {
            disableResizeLocation(bottomSide);
        }
    }

    private void enableResizeLocation(Element element) {
        Event.setEventListener(element, resizeHandler);
        element.removeClassName(UNUSED_STYLE_NAME);
    }

    private void disableResizeLocation(Element element) {
        Event.setEventListener(element, null);
        element.addClassName(UNUSED_STYLE_NAME);
    }

    /**
     * Trigger the resizable mode for the component.
     */
    public void setResizable(boolean resizable) {
        if (this.resizable != resizable) {
            this.resizable = resizable;
            if (resizable) {
                getElement().appendChild(topSide);
                getElement().appendChild(leftSide);
                getElement().appendChild(rightSide);
                getElement().appendChild(bottomSide);
                getElement().addClassName(RESIZABLE_STYLE_NAME);
            } else {
                getElement().removeChild(topSide);
                getElement().removeChild(leftSide);
                getElement().removeChild(rightSide);
                getElement().removeChild(bottomSide);
                getElement().removeClassName(RESIZABLE_STYLE_NAME);
            }
        }
    }

    /**
     * Is the component in resizable mode.
     */
    public boolean getResizable() {
        return resizable;
    }

    /**
     * Set the used resize locations.
     */
    public void setResizeLocations(ArrayList<ResizeLocation> resizeLocations) {
        this.resizeLocations.clear();
        this.resizeLocations.addAll(resizeLocations);
        setupResizeLocations();
    }

    /**
     * Returns the used resize locations.
     */
    public HashSet<ResizeLocation> getResizeLocations() {
        return resizeLocations;
    }

    /**
     * Accept or cancel the pending resize. This only has effect if the
     * component is not in auto accept resize mode (
     * {@link #isAutoAcceptResize()} and if the current resize is pending.
     */
    public void acceptResize(boolean accept) {
        resizeHandler.acceptResize(accept);
    }

    /**
     * Trigger the auto accept resize mode. When set to <code>true</code>, the
     * component will resize automatically. When set to <code>false</code> the
     * user can accept or cancel the resize with {@link #acceptResize(boolean).
     */
    public void setAutoAcceptResize(boolean autoAcceptResize) {
        this.autoAcceptResize = autoAcceptResize;
    }

    /**
     * Is the component in auto accept resize mode.
     */
    public boolean isAutoAcceptResize() {
        return autoAcceptResize;
    }

    /**
     * Set the limiting boundary element for the resize. E.g. the parent element
     * of this component.
     */
    public void setResizeBoundaryElement(Element boundaryElement) {
        this.boundaryElement = boundaryElement;
    }

    /**
     * The current boundary element for the component, or <code>null</code> if
     * none set.
     */
    public Element getResizeBoundaryElement() {
        return boundaryElement;
    }

    /**
     * Set the size of the resize locations in pixels. This will be the
     * width&height for the corner locations, width for the left&right sides,
     * and height for the top&bottom sides.
     * <p>
     * Default size is {@value #DEFAULT_DRAG_SIZE_PIXELS}.
     */
    public void setResizeLocationSize(int resizeLocationSize) {
        if (dragSizePixels != resizeLocationSize) {
            dragSizePixels = resizeLocationSize;
            topLeftCorner.getStyle().setHeight(resizeLocationSize, Unit.PX);
            topLeftCorner.getStyle().setWidth(resizeLocationSize, Unit.PX);
            topRightCorner.getStyle().setHeight(resizeLocationSize, Unit.PX);
            topRightCorner.getStyle().setWidth(resizeLocationSize, Unit.PX);
            bottomLeftCorner.getStyle().setHeight(resizeLocationSize, Unit.PX);
            bottomLeftCorner.getStyle().setWidth(resizeLocationSize, Unit.PX);
            bottomRightCorner.getStyle().setHeight(resizeLocationSize, Unit.PX);
            bottomRightCorner.getStyle().setWidth(resizeLocationSize, Unit.PX);

            topSide.getStyle().setHeight(resizeLocationSize, Unit.PX);
            rightSide.getStyle().setWidth(resizeLocationSize, Unit.PX);
            bottomSide.getStyle().setHeight(resizeLocationSize, Unit.PX);
            leftSide.getStyle().setWidth(resizeLocationSize, Unit.PX);

            int negativeMargin = new BigDecimal(resizeLocationSize)
                    .divideToIntegralValue(new BigDecimal(2)).negate()
                    .intValueExact();
            topLeftCorner.getStyle().setMarginLeft(negativeMargin, Unit.PX);
            topRightCorner.getStyle().setMarginRight(negativeMargin, Unit.PX);
            bottomRightCorner.getStyle()
                    .setMarginRight(negativeMargin, Unit.PX);
            bottomLeftCorner.getStyle().setMarginLeft(negativeMargin, Unit.PX);
            topSide.getStyle().setMarginTop(negativeMargin, Unit.PX);
            rightSide.getStyle().setMarginRight(negativeMargin, Unit.PX);
            bottomSide.getStyle().setMarginBottom(negativeMargin, Unit.PX);
            leftSide.getStyle().setMarginLeft(negativeMargin, Unit.PX);
        }
    }

    @Override
    public HandlerRegistration addResizeStartHandler(
            ResizableLayoutHandler handler) {
        return addHandler(handler, ResizeStartEvent.getType());
    }

    @Override
    public HandlerRegistration addResizeEndHandler(
            ResizableLayoutHandler handler) {
        return addHandler(handler, ResizeEndEvent.getType());
    }

    protected void fireResizeEnd(int clientWidth, int clientHeight) {
        fireEvent(new ResizeEndEvent(clientHeight, clientWidth));
    }

    protected void fireResizeStart(ResizeLocation resizeLocation) {
        fireEvent(new ResizeStartEvent(resizeLocation));
    }

    protected class ResizeHandler implements EventListener {

        private int startClientX = 0;
        private int startClientY = 0;
        private int startWidth = 0;
        private int startHeight = 0;
        private boolean resizingX;
        private boolean revertX;
        private boolean revertY;
        private boolean resizingY;
        private Element draggedElement;
        private boolean waitingAccept;

        @Override
        public void onBrowserEvent(Event event) {
            final EventTarget currentTarget = event.getCurrentEventTarget();
            final Element target = currentTarget.cast();
            final Element targetParent = target.getParentElement();
            if (resizingX
                    || resizingY
                    || getElement().equals(targetParent)
                    || (targetParent != null && getElement().equals(
                            targetParent.getParentElement()))) {
                switch (event.getTypeInt()) {
                case Event.ONMOUSEMOVE:
                    onMouseMove(event);
                    break;
                case Event.ONMOUSEDOWN:
                    onResizeStart(event, target);
                    break;
                case Event.ONMOUSEUP:
                    onResizeEnd(event);
                    break;
                default:
                    break;
                }
            }
        }

        private void onMouseMove(Event event) {
            if (resizingY) {
                int clientY = event.getClientY();
                if (!isInVerticalBoundary(event)) {
                    // set the size to the edge of the boundary element
                    clientY = clientY < boundaryElement.getAbsoluteTop() ? (boundaryElement
                            .getAbsoluteTop() + 2) : (boundaryElement
                            .getAbsoluteBottom() - 2);
                }
                int extraScrollHeight = boundaryElement == null ? 0
                        : boundaryElement.getScrollTop();
                dragOverlayElement.getStyle().setHeight(
                        startHeight
                                + extraScrollHeight
                                + (revertY ? startClientY - clientY : clientY
                                        - startClientY), Unit.PX);
                event.stopPropagation();
            }
            if (resizingX) {
                int clientX = event.getClientX();
                if (!isInHorizontalBoundary(event)) {
                    // set the size to the edge of the boundary element
                    clientX = clientX < boundaryElement.getAbsoluteLeft() ? (boundaryElement
                            .getAbsoluteLeft() + 2) : (boundaryElement
                            .getAbsoluteRight() - 2);
                }
                int extraScrollWidth = boundaryElement == null ? 0
                        : boundaryElement.getScrollLeft();
                dragOverlayElement.getStyle().setWidth(
                        startWidth
                                + extraScrollWidth
                                + (revertX ? startClientX - clientX : clientX
                                        - startClientX), Unit.PX);
                event.stopPropagation();
            }
        }

        private boolean isInHorizontalBoundary(Event event) {
            if (boundaryElement != null) {
                int clientX = event.getClientX();
                int right = boundaryElement.getAbsoluteRight() - 1;
                int left = boundaryElement.getAbsoluteLeft() + 1;
                return clientX > left && clientX < right;
            }
            return true;
        }

        private boolean isInVerticalBoundary(Event event) {
            if (boundaryElement != null) {
                int clientY = event.getClientY();
                int top = boundaryElement.getAbsoluteTop() + 1;
                int bottom = boundaryElement.getAbsoluteBottom() - 1;
                return clientY > top && clientY < bottom;
            }
            return true;
        }

        private void overrideCursor(ResizeLocation location) {
            String cursorValue = locationToCursorMap.get(location);
            if (boundaryElement != null) {
                boundaryElement.getStyle().setProperty("cursor", cursorValue);
            }
            dragOverlayElement.getStyle().setProperty("cursor", cursorValue);
        }

        private void stopCursorOverride() {
            if (boundaryElement != null) {
                boundaryElement.getStyle().clearCursor();
            }
            dragOverlayElement.getStyle().clearCursor();
        }

        private void markBoundaryResizing() {
            if (boundaryElement != null) {
                boundaryElement.addClassName("resizing-child");
            }
        }

        private void unmarkBoundaryResizing() {
            if (boundaryElement != null) {
                boundaryElement.removeClassName("resizing-child");
            }
        }

        protected void acceptResize(boolean accept) {
            if (waitingAccept) {
                waitingAccept = false;
                if (accept) {
                    getElement().getStyle().setWidth(
                            dragOverlayElement.getClientWidth(), Unit.PX);
                    getElement().getStyle().setHeight(
                            dragOverlayElement.getClientHeight(), Unit.PX);
                }
                resizingX = false;
                resizingY = false;
                draggedElement = null;
                dragOverlayElement.removeFromParent();
                Style style = dragOverlayElement.getStyle();
                style.clearTop();
                style.clearRight();
                style.clearBottom();
                style.clearLeft();
                style.clearHeight();
                style.clearWidth();
                startClientX = 0;
                startClientY = 0;
                startHeight = 0;
                startWidth = 0;
                getElement().removeClassName("resizing");
            }
        }

        private void onResizeEnd(Event event) {
            if (resizingX || resizingY) {
                resizingX = false;
                resizingY = false;
                waitingAccept = true;
                Event.releaseCapture(draggedElement);
                event.stopPropagation();
                stopCursorOverride();
                unmarkBoundaryResizing();
                fireResizeEnd(dragOverlayElement.getClientWidth(),
                        dragOverlayElement.getClientHeight());
                if (autoAcceptResize) {
                    acceptResize(true);
                }
            }
        }

        private void onResizeStart(Event event, Element target) {
            if (!(resizingX || resizingY || waitingAccept)) {
                draggedElement = target;
                ResizeLocation resizeLocation;
                if (target.equals(topSide) || target.equals(bottomSide)) {
                    resizeLocation = startVerticalResize(event, target);
                } else if (target.equals(leftSide) || target.equals(rightSide)) {
                    resizeLocation = startHorizontalResize(event, target);
                } else {
                    resizeLocation = startDiagonalResize(event, target);
                }
                fireResizeStart(resizeLocation);
                getElement().addClassName("resizing");
                getElement().appendChild(dragOverlayElement);
                Event.setCapture(target);
                event.stopPropagation();
                overrideCursor(resizeLocation);
                markBoundaryResizing();
            }
        }

        private ResizeLocation startDiagonalResize(Event event, Element target) {
            ResizeLocation resizeLocation;
            resizingX = true;
            resizingY = true;
            Style style = dragOverlayElement.getStyle();
            startHeight = getElement().getClientHeight();
            startClientY = event.getClientY();
            style.setHeight(startHeight, Unit.PX);
            startWidth = getElement().getClientWidth();
            startClientX = event.getClientX();
            style.setWidth(startWidth, Unit.PX);
            if (target.equals(topLeftCorner) || target.equals(topRightCorner)) {
                revertY = true;
                style.setBottom(0, Unit.PX);
                resizeLocation = target.equals(topLeftCorner) ? ResizeLocation.TOP_LEFT
                        : ResizeLocation.TOP_RIGHT;
            } else {
                revertY = false;
                style.setTop(0, Unit.PX);
                resizeLocation = target.equals(bottomRightCorner) ? ResizeLocation.BOTTOM_RIGHT
                        : ResizeLocation.BOTTOM_LEFT;
            }
            if (target.equals(topLeftCorner) || target.equals(bottomLeftCorner)) {
                revertX = true;
                style.setRight(0, Unit.PX);
            } else {
                revertX = false;
                style.setLeft(0, Unit.PX);
            }
            return resizeLocation;
        }

        private ResizeLocation startVerticalResize(Event event, Element target) {
            ResizeLocation resizeLocation;
            resizingY = true;
            Style style = dragOverlayElement.getStyle();
            startHeight = getElement().getClientHeight();
            startClientY = event.getClientY();
            style.setHeight(startHeight, Unit.PX);
            if (target.equals(topSide)) {
                revertY = true;
                style.setBottom(0, Unit.PX);
                resizeLocation = ResizeLocation.TOP;
            } else {
                revertY = false;
                style.setTop(0, Unit.PX);
                resizeLocation = ResizeLocation.BOTTOM;
            }
            style.setLeft(0, Unit.PX);
            style.setRight(0, Unit.PX);
            return resizeLocation;
        }

        private ResizeLocation startHorizontalResize(Event event, Element target) {
            ResizeLocation resizeLocation;
            resizingX = true;
            Style style = dragOverlayElement.getStyle();
            startWidth = getElement().getClientWidth();
            startClientX = event.getClientX();
            style.setWidth(startWidth, Unit.PX);
            if (target.equals(leftSide)) {
                revertX = true;
                style.setRight(0, Unit.PX);
                resizeLocation = ResizeLocation.LEFT;
            } else {
                revertX = false;
                style.setLeft(0, Unit.PX);
                resizeLocation = ResizeLocation.RIGHT;
            }
            style.setTop(0, Unit.PX);
            style.setBottom(0, Unit.PX);
            return resizeLocation;
        }

    }
}
