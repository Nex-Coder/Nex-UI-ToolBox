package ui.controls;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import lib.enums.WindowBarControl;
import ui.parents.OBox;
import ui.skins.WindowBarSkin;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static lib.enums.WindowBarControl.*;

public class WindowBar extends Control {

    /*================================================================================================================*\
    || Properties
    \*================================================================================================================*/
    private Window stage;

    private double xOffset, yOffset, prevWidth, prevHeight, prevX, prevY;


    private final OBox leftGroup = new OBox(Orientation.HORIZONTAL), rightGroup = new OBox(Orientation.HORIZONTAL);
    private final Button btnMin = new Button(), btnExit = new Button(), btnHelp = new Button(), btnMax = new Button(), btnSettings = new Button();
    private final ProgressBar progressBar = new ProgressBar();
    private final Label progressState = new Label();

    private LinkedList<WindowBarControl> leftOrder, rightOrder;


    // States
    private boolean isMinable, isMaxable, isExitable, isMovable = false, isProgressable, isHelpable, isSettingsable;

    private final BooleanProperty isMaximisedProperty = new SimpleBooleanProperty(false);

    private ShutdownMethod shutdownMethod = ShutdownMethod.APPLICATION;
    private final MinimiseMethod minimiseMethod = MinimiseMethod.ICON;

    // EventHandlers
    private final EventHandler<MouseEvent> preDragEvent = e -> {
        stage = this.getScene().getWindow();
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }, dragEvent = e -> {
        stage.setX(e.getScreenX() - xOffset);
        stage.setY(e.getScreenY() - yOffset);
        isMaximisedProperty.set(false);
    }, maxEvent = e -> {
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
    }, exitEvent = e -> {
        // Maybe create an interface that supplies exit handling to make code cleaner?
        switch (shutdownMethod) {
            case WINDOW -> this.getScene().getWindow().hide();
            case JAVAFX ->  Platform.exit();
            case APPLICATION -> System.exit(0);
        }
    }, minEvent = e -> {
        switch (minimiseMethod) {
            case ICON -> ((Stage) this.getScene().getWindow()).setIconified(true);
            case TRAY -> ((Stage) this.getScene().getWindow()).setIconified(true); // TODO Dependant on completion of NexTrayPane (& a SendToTray Interface). Then finish constructors and test/docs
        }
    };

    private EventHandler<MouseEvent> helpEvent, settingsEvent;

    private final ChangeListener<Boolean> maxListener;

    /*================================================================================================================*\
    ||  Constructors
    \*================================================================================================================*/

    public WindowBar() {
        this(true, true, true, true, false, false, false);
    }

    public WindowBar(boolean isMinable,
                     boolean isMaxable,
                     boolean isExitable,
                     boolean isMovable,
                     boolean isProgressable,
                     boolean isHelpable,
                     boolean isSettingsable) {
        super();

        maxListener = createMaxChangeListener();

        setUserMoveable(isMovable);

        this.isMinable = isMinable;
        this.isMaxable = isMaxable;
        this.isExitable = isExitable;
        this.isProgressable = isProgressable;
        this.isHelpable = isHelpable;
        this.isSettingsable = isSettingsable;

        btnMin.setOnMouseClicked(minEvent);
        btnMax.setOnMouseClicked(maxEvent);
        btnExit.setOnMouseClicked(exitEvent);
        btnSettings.setOnMouseClicked(settingsEvent);
        btnHelp.setOnMouseClicked(helpEvent);

        setDefaultGroupOrders();
    }

    /*================================================================================================================*\
    ||  Methods
    \*================================================================================================================*/

    /* Ordering */

    public void sortGroup(boolean left) {
        if (left) {
            for (WindowBarControl windowBarControl : leftOrder) {
                switch (windowBarControl) {
                    case BUTTON_HELP -> btnHelp.toFront();
                    case BUTTON_SETTINGS -> btnSettings.toFront();
                }
            }
        } else {
            for (WindowBarControl windowBarControl : rightOrder) {
                switch (windowBarControl) {
                    case BUTTON_MIN -> btnMin.toFront();
                    case BUTTON_MAX -> btnMax.toFront();
                    case BUTTON_EXIT -> btnExit.toFront();
                }
                System.out.println("sorted: " + windowBarControl);
            }
        }
    }

    /* Events & Listener */
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

    public void setHelpEventHandler(EventHandler<MouseEvent> eventHandler) {
        try {
            btnHelp.removeEventHandler(MouseEvent.MOUSE_CLICKED, helpEvent);
        } catch (NullPointerException | IllegalArgumentException ignored) {}

        helpEvent = eventHandler;
    }

    public void setSettingsEventHandler(EventHandler<MouseEvent> eventHandler) {
        try {
            btnHelp.removeEventHandler(MouseEvent.MOUSE_CLICKED, settingsEvent);
        } catch (NullPointerException | IllegalArgumentException ignored) {}

        settingsEvent = eventHandler;
    }

    /* Node/Control States */
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
        } else if (!enable && isMovable) {
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
        isMaxable = setNodeEnabled(enable, isMaxable, BUTTON_MAX);
    }

    public void setUserExitable(boolean enable) {
        isExitable = setNodeEnabled(enable, isExitable, BUTTON_EXIT);
    }

    public void setUserMinable(boolean enable) {
        isMinable = setNodeEnabled(enable, isMinable, BUTTON_MIN);
    }

    public void setUserHelpable(boolean enable) throws IllegalStateException {
        isHelpable = setNodeEnabled(enable, isHelpable, BUTTON_HELP);
    }

    public void setUserSettingsable(boolean enable) {
        isSettingsable = setNodeEnabled(enable, isSettingsable, BUTTON_SETTINGS);
    }

    private boolean setNodeEnabled(boolean enable, boolean isEnabled, WindowBarControl nodeType) {
        if (enable && !isEnabled) {
            ((WindowBarSkin) getSkin()).setControlEnabled(nodeType, true);
            sortGroup(false);
            return true;
        } else if (!enable && isEnabled) {
            ((WindowBarSkin) getSkin()).setControlEnabled(nodeType, false);
            return false;
        }
        return isEnabled;
    }

    private boolean setNodeEnabled(boolean enable, boolean isEnabled, WindowBarControl nodeType, Node node, EventHandler<MouseEvent> event) {
        if (enable && !isEnabled) {
            ((WindowBarSkin) getSkin()).setControlEnabled(nodeType, true);
            node.setOnMouseClicked(event);
            sortGroup(false);
            return true;
        } else if (!enable && isEnabled) {
            ((WindowBarSkin) getSkin()).setControlEnabled(nodeType, false);
            node.removeEventHandler(MouseEvent.MOUSE_CLICKED, event);
            return false;
        }
        return isEnabled;
    }

    /**
     * Allows the user to view any data that the has been appropriately bound. This will show a small progress bar and
     * status label. If false, the nodes are removed from the pane.
     * <br><br>
     * Note when enabled: When this is enabled and its controls are visible, the visible parts may be covered by the
     * other buttons visible on this windowBar when resized to a width less than 256px. Either enforce a minimum
     * scene/stage width of 256px or disable resizing.
     * @param enable True for enabled progress display, false otherwise.
     */
    public void setUserProgressable(boolean enable) {
        if (enable && !isProgressable) {
            getChildren().addAll(progressBar, progressState);
            isProgressable = true;
        } else if (!enable && isProgressable) {
            getChildren().removeAll(progressBar, progressState);
            isProgressable = false;
        }
    }
    /* Restore Defaults */
    public void setDefaultGroupOrders() {
        leftOrder = new LinkedList<>();
        leftOrder.add(BUTTON_SETTINGS); leftOrder.add(BUTTON_HELP);

        rightOrder = new LinkedList<>();
        rightOrder.add(BUTTON_MIN); rightOrder.add(BUTTON_MAX); rightOrder.add(BUTTON_EXIT);
    }

    public void setDefaultMinEvent() {
        btnMin.setOnMouseClicked(minEvent);
    }

    public void setDefaultMaxEvent() {
        btnMax.setOnMouseClicked(maxEvent);
    }

    public void setDefaultExitEvent() {
        btnExit.setOnMouseClicked(exitEvent);
    }

    public void setDefaultHelpEvent() {
        btnHelp.setOnMouseClicked(helpEvent);
    }

    public void setDefaultSettingsEvent() {
        btnSettings.setOnMouseClicked(settingsEvent);
    }

    /* Overrides */
    @Override protected Skin<?> createDefaultSkin() {
        WindowBarSkin skin = new WindowBarSkin(this) {};

        return skin;
    }

    /*================================================================================================================*\
    ||  Getters & Setters (No Functionality)
    \*================================================================================================================*/

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

    public Button getButtonSettings() {
        return btnSettings;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public OBox getLeftGroup() { return leftGroup; }

    public OBox getRightGroup() { return rightGroup; }

    public Label getProgressState() {
        return progressState;
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
     * States if the stage is movable.
     * @return True for user movable stage controls, false otherwise.
     */
    public boolean isMovable() { return isMovable; }

    /**
     * States if the stage is settings-able.
     * @return True for user movable stage controls, false otherwise.
     */
    public boolean isSettingsable() { return isSettingsable; }

    /**
     * States if the progress display is toggled.
     * @return True for is progress showing and false otherwise.
     */
    public boolean isProgressable() { return isProgressable;  }

    public Map<WindowBarControl, Boolean> getStates() {
        Map<WindowBarControl, Boolean> stateMap = new HashMap<>();
        stateMap.put(BUTTON_MIN, isMinable);
        stateMap.put(BUTTON_MAX, isMaxable);
        stateMap.put(BUTTON_EXIT, isExitable);
        stateMap.put(BUTTON_HELP, isHelpable);
        stateMap.put(BUTTON_SETTINGS, isSettingsable);
        stateMap.put(PROGRESS_BAR, isProgressable);
        stateMap.put(PROGRESS_STATE, isProgressable);
        return stateMap;
    }

    /**
     * Retrieves the shutdown method for the exit button assigned to this window bar.
     * @return The shutdown method.
     */
    public ShutdownMethod getShutdownMethod() { return shutdownMethod; }

    /**
     * Sets the shutdown method for the exit button assigned to this window bar.
     * The shutdown methods act af follows:
     * <ul>
     * <li> WINDOW: Only closes the window/stage called from when set to this option. </li>
     * <li> JAVAFX: Exits the JavaFX runtime which will close all stages but leave none JavaFX dependant threads running. </li>
     * <li> APPLICATION: Shutdowns the entire application system. All threads will terminate regardless. </li>
     * </ul>
     */
    public void setShutdownMethod(ShutdownMethod method) { this.shutdownMethod = method; }

    /*================================================================================================================*\
    || Enum
    \*================================================================================================================*/

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

}
