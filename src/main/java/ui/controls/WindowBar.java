package ui.controls;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import lib.WindowBarControl;
import lib.interfaces.StageReturnable;
import ui.skins.WindowBarSkin;

import java.util.HashMap;
import java.util.Map;

import static lib.WindowBarControl.*;

public class WindowBar extends Control {
    private Window stage; // Most cases this can be hard coded. But if a user changes the scene this will continue to work.
    private double xOffset = 0;
    private double yOffset = 0;

    private double prevWidth;
    private double prevHeight;
    private double prevX;
    private double prevY;

    private final Button btnMin = new Button(), btnExit = new Button(), btnHelp = new Button(), btnMax = new Button();
    private final ProgressBar progressBar = new ProgressBar();
    private final Label progressState = new Label();

    // States
    private boolean isMaxable, isHelpable, isMovable, isProgressable, isExitable, isMinable;
    private final BooleanProperty isMaximisedProperty = new SimpleBooleanProperty(false);

    private NexWindowBar.ShutdownMethod shutdownMethod = NexWindowBar.ShutdownMethod.APPLICATION;
    private final NexWindowBar.MinimiseMethod minimiseMethod = NexWindowBar.MinimiseMethod.ICON;

    private StageReturnable<?> helpStage;

    // EventHandlers
    private final EventHandler<MouseEvent> preDragEvent = e -> {
        stage = this.getScene().getWindow();
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    };
    private final EventHandler<MouseEvent> dragEvent = e -> {
        stage.setX(e.getScreenX() - xOffset);
        stage.setY(e.getScreenY() - yOffset);
        isMaximisedProperty.set(false);
    };

    private final ChangeListener<Boolean> maxListener;

    private final EventHandler<MouseEvent> maxEvent = e -> {
        Window stage = this.getScene().getWindow();
        Rectangle2D bounds = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).get(0).getVisualBounds();

        if (isMaximisedProperty.get()) {
            stage.setWidth(prevWidth);
            stage.setHeight(prevHeight);
            stage.setX(prevX);
            stage.setY(prevY);
            isMaximisedProperty.set(false);
        } else {
            prevX = stage.getX();
            prevY = stage.getY();
            prevWidth = stage.getWidth();
            prevHeight = stage.getHeight();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            isMaximisedProperty.set(true);
        }
    };

    private final EventHandler<MouseEvent> exitEvent = e -> {
        // Maybe create an interface that supplies exit handling to make code cleaner?
        switch (shutdownMethod) {
            case WINDOW -> this.getScene().getWindow().hide();
            case JAVAFX ->  Platform.exit();
            case APPLICATION -> System.exit(0);
        }
    };

    private final EventHandler<MouseEvent> minEvent = e -> {
        switch (minimiseMethod) {
            case ICON -> ((Stage) this.getScene().getWindow()).setIconified(true);
            case TRAY -> ((Stage) this.getScene().getWindow()).setIconified(true); // TODO Dependant on completion of NexTrayPane (& a SendToTray Interface). Then finish constructors and test/docs
        }
    };

    private EventHandler<MouseEvent> helpEvent = e -> {
        if (helpStage == null) {
            //helpMissing.close();
            //helpMissing.show();
        } else if (!helpStage.isReturned()) {
            this.getScene().getWindow().hide();
            helpStage.returnableStart((Stage) this.getScene().getWindow());
        }
    };

    public WindowBar() {
        super();
        maxListener = createMaxChangeListener();

        setUserMinable(true);
        setUserMaxable(true);
        setUserExitable(true);
        setUserHelpable(true);
        setUserProgressable(true);
    }

    protected ChangeListener<Boolean> createMaxChangeListener() {
        return (obv, o, n) -> {
            Window stage = this.getScene().getWindow();

            if (!n) { // If new is false, restore prev stage size & pos. Adjust xOffset.
                stage.setWidth(prevWidth);
                stage.setHeight(prevHeight);
                stage.setX(prevX);
                stage.setY(prevY);
                xOffset = prevWidth / 2;
                btnMax.setText("\uD83D\uDDD6");
            } else {
                btnMax.setText("\uD83D\uDDD7");
            }
        };
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new WindowBarSkin(this) {};
    }

    public Map<WindowBarControl, Boolean> getStates() {
        Map<WindowBarControl, Boolean> stateMap = new HashMap<>();
        stateMap.put(BUTTON_MIN, isMinable);
        stateMap.put(BUTTON_MAX, isMaxable);
        stateMap.put(BUTTON_EXIT, isExitable);
        stateMap.put(BUTTON_HELP, isHelpable);
        stateMap.put(PROGRESS_BAR, isProgressable);
        stateMap.put(PROGRESS_STATE, isProgressable);
        return stateMap;
    }


    public void setHelpEventHandler(EventHandler<MouseEvent> eventHandler) {
        try {
            btnHelp.removeEventHandler(MouseEvent.MOUSE_CLICKED, helpEvent);
        } catch (NullPointerException | IllegalArgumentException ignored) {}

        helpEvent = eventHandler;
        setUserHelpable(isHelpable());
    }

    private void updateControls(boolean isExitable, boolean isMaxable, boolean isMinable, boolean isProgressable, boolean isHelpable, boolean  isMovable) {
        setUserMoveable(isMovable);
        setUserMaxable(isMaxable);
        setUserHelpable(isHelpable);
        setUserMinable(isMinable);
        setUserExitable(isExitable);
        setUserProgressable(isProgressable);
    }

    /**
     * Enables event handlers for dragging the stage via this NexWindowBar if not already enabled.
     * Will also disable the event handler unconditionally if given a false value.
     * @param enable True to enable otherwise false to disable.
     */
    public void setUserMoveable(boolean enable) {
        if (enable && !isMovable) {
            this.setOnMousePressed(preDragEvent);
            this.setOnMouseDragged(dragEvent);
            isMovable = true;
        } else if (!enable) {
            this.removeEventHandler(MouseEvent.MOUSE_PRESSED, preDragEvent);
            this.removeEventHandler(MouseEvent.MOUSE_DRAGGED, dragEvent);
            isMovable = false;
        }
    }

    /**
     * Enables event handlers for maximizing via this NexWindowBar if not already enabled.
     * Will also disable the event handler unconditionally if given a false value.
     * @param enable True to enable otherwise false to disable.
     */
    public void setUserMaxable(boolean enable) {
        if (enable && !isMaxable) {
            this.getChildren().add(btnMax);
            btnMax.setOnMouseClicked(maxEvent);
            isMaximisedProperty.addListener(maxListener);
            isMaxable = true;
        } else if (!enable) {
            this.getChildren().remove(btnMax);
            btnMax.removeEventHandler(MouseEvent.MOUSE_CLICKED, maxEvent);
            isMaximisedProperty.removeListener(maxListener);
            isMaxable = false;
        }
    }

    public void setUserExitable(boolean enable) {
        if (enable && !isExitable) {
            this.getChildren().add(btnExit);
            btnExit.setOnMouseClicked(exitEvent);
            isExitable = true;
        } else if (!enable) {
            this.getChildren().remove(btnExit);
            btnExit.removeEventHandler(MouseEvent.MOUSE_CLICKED, exitEvent);
            isExitable = false;
        }
    }

    public void setUserMinable(boolean enable) {
        if (enable && !isMinable) {
            this.getChildren().add(btnMin);
            btnMin.setOnMouseClicked(minEvent);
            isMinable = true;
        } else if (!enable) {
            this.getChildren().remove(btnMin);
            btnMin.removeEventHandler(MouseEvent.MOUSE_CLICKED, minEvent);
            isMinable = false;
        }
    }

    public void setUserHelpable(boolean enable) {
        if (enable && !isHelpable) {
            this.getChildren().add(btnHelp);
            btnHelp.setOnMouseClicked(helpEvent);
            isHelpable = true;
        } else if (!enable) {
            this.getChildren().remove(btnHelp);
            btnHelp.removeEventHandler(MouseEvent.MOUSE_CLICKED, helpEvent);
            isHelpable = false;
        }
    }

    /**
     * Allows the user to view any data that the has been appropriately bound. This will show a small progress bar and
     * status label. If false, the nodes are removed from the pane.
     *
     * Note when enabled: When this is enabled and its controls are visible, the visible parts may be covered by the
     * other buttons visible on this windowBar when resized to a width less than 256px. Either enforce a minimum
     * scene/stage width of 256px or disable resizing.
     * @param enable True for enabled progress display, false otherwise.
     */
    public void setUserProgressable(boolean enable) {
        if (enable && !isProgressable) {
            this.getChildren().addAll(progressBar, progressState);
            isProgressable = true;
        } else if (!enable) {
            this.getChildren().removeAll(progressBar, progressState);
            isProgressable = false;
        }
    }

    /**
     * States if the help button is enabled.
     * @return True for user help options available, false otherwise.
     */
    public boolean isHelpable() { return isHelpable; }

    /**
     * States if the stage is user minimisable/iconifable.
     * @return True for user minimisable/iconifable stage controls, false otherwise.
     */
    public boolean isMinable() { return isMinable; }

    /**
     * States if the stage can be maximised or not.
     * @return True for user maximisable stage, false otherwise/
     */
    public boolean isMaxable() { return isMaxable; }

    /**
     * States if the stage is currently maximised.
     * @return True for stage maximised, false otherwise.
     */
    public boolean isMaximized() { return isMaximisedProperty.get(); }

    /**
     * States if the stage is is movable.
     * @return True for user movable stage controls, false otherwise.
     */
    public boolean isMovable() { return isMovable; }

    /**
     * States if the progress display is toggled.
     * @return True for is progress showing and false otherwise.
     */
    public boolean isProgressable() { return isProgressable;  }

    /**
     * Retrieves the shutdown method for the exit button assigned to this window bar.
     * @return The shutdown method.
     */
    public NexWindowBar.ShutdownMethod getShutdownMethod() { return shutdownMethod; }

    /**
     * Sets the shutdown method for the exit button assigned to this window bar.
     * The shutdown methods act af follows:
     * - WINDOW: Only closes the window/stage called from when set to this option.
     * - JAVAFX: Exits the JavaFX runtime which will close all stages but leave none JavaFX dependant threads running.
     * - APPLICATION: Shutdowns the entire application system. All threads will terminate regardless.
     */
    public void setShutdownMethod(NexWindowBar.ShutdownMethod method) { this.shutdownMethod = method; }

    /**
     * Sets the returnable Stage to set when the user presses the help button if enabled.
     * Changes are only visable if the help menu isn't already shown.
     * @param helpStage The stage implementing the StageReturnable interface of any type.
     */
    public void setHelpStage(StageReturnable<?> helpStage) {
        this.helpStage = helpStage;

    }

    /**
     * There are 3 configured ways of exiting:
     * WINDOW: Only closes the window/stage called from when set to this option.
     * JAVAFX: Exits the JavaFX runtime which will close all stages but leave none JavaFX dependant threads running.
     * APPLICATION: Shutdowns the entire application system. All threads will terminate regardless.
     */
    public enum ShutdownMethod {
        WINDOW,
        JAVAFX,
        APPLICATION
    }

    public enum MinimiseMethod {
        ICON,
        TRAY,
        THEABISWHERENOTHINGRETURNSFROM
    }


    public Button getButtonMin() {
        return btnMin;
    }

    public Button getButtonMax() {
        return btnMax;
    }

    public Button getButtonExit() {
        return btnExit;
    }

    public Button getButtonHelp() {
        return btnHelp;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getProgressState() {
        return progressState;
    }

}
