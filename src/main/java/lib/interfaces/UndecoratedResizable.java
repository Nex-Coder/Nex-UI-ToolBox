package lib.interfaces;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * This class provides the static methods for hooking up the events for the ResizeListener class.
 * Use this to completely setup the resizing events.
 *
 * @author Nex
 */
public interface UndecoratedResizable {

    /**
     * Enables the events for resizing the implemented stage.
     *
     * It should depend on a private function within the UndecoratedResizable interface called "addResizeListener". This
     * will take care of the core functionality. However, it is ideal that this function checks or removes the listener
     * to avoid duplicating it.
     */
    void enableResize();

    /**
     * Removes the resize event listener
     */
    void disableResize();


    /**
     * The function in which you can assign any of the parameters automatically for resizing. This function handles
     * adding the listeners for resizing events for the undecorated window.
     * @param stage The stage which holds the application. Its *current* min and max width & height will be used for
     *              the resize limits.
     * @return The ResizeListener used to enable resizing. Useful for removing later.
     */
    default ResizeListener addResizeListener(Stage stage) {
        return addResizeListener(stage, stage.getMinWidth(), stage.getMinHeight(), stage.getMaxWidth(), stage.getMaxHeight());
    }

    /**
     * The manual function in which you can assign any of the parameters manually. This function handles adding the
     * listeners for resizing events for the undecorated window.
     * @param stage The stage which holds the application.
     * @param minWidth Minimum width in which the program can be resized to.
     * @param minHeight Minimum height in which the program can be resized to.
     * @param maxWidth Maximum width in which the program can be resized to.
     * @param maxHeight Maximum height in which the program can be resized to.
     * @return The ResizeListener used to enable resizing. Useful for removing later.
     */
    default ResizeListener addResizeListener(Stage stage, double minWidth, double minHeight, double maxWidth, double maxHeight) {
        ResizeListener resizeListener = new ResizeListener(stage);

        // Assign range of resizing.
        resizeListener.setMinWidth(minWidth);
        resizeListener.setMinHeight(minHeight);
        resizeListener.setMaxWidth(maxWidth);
        resizeListener.setMaxHeight(maxHeight);


        stage.addEventHandler(MouseEvent.MOUSE_MOVED, resizeListener);
        stage.addEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
        stage.addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeListener);
        stage.addEventHandler(MouseEvent.MOUSE_EXITED, resizeListener);
        stage.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, resizeListener);

        return resizeListener;
    }

    /**
     * When constructed will provide the functionality of actually handling the resize events needed for an undecorated
     * window in the handle function.
     */
    class ResizeListener implements EventHandler<MouseEvent> {
        private final Stage stage;
        private Cursor cursorEvent = Cursor.DEFAULT;
        private double startX = 0;
        private double startY = 0;

        // Max and min sizes for controlled stage
        private double minWidth;
        private double maxWidth;
        private double assignedMinHeight;
        private double assignedMaxHeight;

        public ResizeListener(Stage stage) { this.stage = stage; }

        public void setMinWidth(double minWidth) { this.minWidth = minWidth; }
        public void setMinHeight(double minHeight) { this.assignedMinHeight = minHeight; }

        public void setMaxWidth(double maxWidth) {  this.maxWidth = maxWidth; }
        public void setMaxHeight(double maxHeight) { this.assignedMaxHeight = maxHeight; }

        @Override
        public void handle(MouseEvent mouseEvent) {
            EventType<? extends MouseEvent> mouseEventType = mouseEvent.getEventType();
            Scene scene = stage.getScene();

            double mouseEventX = mouseEvent.getSceneX(),
                    mouseEventY = mouseEvent.getSceneY(),
                    sceneWidth = scene.getWidth(),
                    sceneHeight = scene.getHeight();

            int border = 4;
            if (MouseEvent.MOUSE_MOVED.equals(mouseEventType)) {
                if (mouseEventX < border +10 && mouseEventY > sceneHeight - border -10) {
                    cursorEvent = Cursor.SW_RESIZE;
                } else if (mouseEventX > sceneWidth - border -10 && mouseEventY > sceneHeight - border -10) {
                    cursorEvent = Cursor.SE_RESIZE;
                } else if (mouseEventX < border && mouseEventY > 26) {
                    cursorEvent = Cursor.W_RESIZE;
                } else if (mouseEventX > sceneWidth - border) {
                    cursorEvent = Cursor.E_RESIZE;
                } else if (mouseEventY > sceneHeight - border) {
                    cursorEvent = Cursor.S_RESIZE;
                } else {
                    cursorEvent = Cursor.DEFAULT;
                }
                scene.setCursor(cursorEvent);
            } else if (MouseEvent.MOUSE_EXITED.equals(mouseEventType) || MouseEvent.MOUSE_EXITED_TARGET.equals(mouseEventType)) {
                scene.setCursor(Cursor.DEFAULT);
            } else if (MouseEvent.MOUSE_PRESSED.equals(mouseEventType)) {
                startX = stage.getWidth() - mouseEventX;
                startY = stage.getHeight() - mouseEventY;
            } else if (MouseEvent.MOUSE_DRAGGED.equals(mouseEventType)) {
                if (!Cursor.DEFAULT.equals(cursorEvent)) {
                    if (!Cursor.W_RESIZE.equals(cursorEvent) && !Cursor.E_RESIZE.equals(cursorEvent)) {
                        double minHeight = stage.getMinHeight() > (border * 2) ? stage.getMinHeight() : (border * 2);
                        if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.N_RESIZE.equals(cursorEvent)
                                || Cursor.NE_RESIZE.equals(cursorEvent)) {
                            if (stage.getHeight() > minHeight || mouseEventY < 0) {
                                setStageHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight());
                                stage.setY(mouseEvent.getScreenY());
                            }
                        } else {
                            if (stage.getHeight() > minHeight || mouseEventY + startY - stage.getHeight() > 0) {
                                setStageHeight(Math.max(minHeight, mouseEventY + startY));
                            }
                        }
                    }

                    if (!Cursor.N_RESIZE.equals(cursorEvent) && !Cursor.S_RESIZE.equals(cursorEvent)) {
                        double minWidth = stage.getMinWidth() > (border * 2) ? stage.getMinWidth() : (border * 2);
                        if (Cursor.NW_RESIZE.equals(cursorEvent) || Cursor.W_RESIZE.equals(cursorEvent)
                                || Cursor.SW_RESIZE.equals(cursorEvent)) {
                            if (stage.getWidth() > minWidth || mouseEventX < 0) {
                                setStageWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth());
                                stage.setX(mouseEvent.getScreenX());
                            }
                        } else {
                            if (stage.getWidth() > minWidth || mouseEventX + startX - stage.getWidth() > 0) {
                                setStageWidth(Math.max(minWidth, mouseEventX + startX));
                            }
                        }
                    }
                }

            }
        }

        private void setStageWidth(double width) {
            width = Math.min(width, maxWidth);
            width = Math.max(width, minWidth);
            stage.setWidth(width);
        }

        private void setStageHeight(double height) {
            height = Math.min(height, assignedMaxHeight);
            height = Math.max(height, assignedMinHeight);
            stage.setHeight(height);
        }
    }
}
