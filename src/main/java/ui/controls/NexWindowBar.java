package ui.controls;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.ButtonBarSkin;
import javafx.scene.control.skin.ButtonSkin;
import lib.interfaces.StageReturnable;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import ui.skins.NexWindowBarSkin;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Top bar controller. Handles all tasks relating to the Top bar.
 */
public class NexWindowBar extends Control {


    // Fields
    private final AnchorPane layout = new AnchorPane();

    private Window stage; // Most cases this can be hard coded. But if a user changes the scene this will continue to work.
    private double xOffset = 0;
    private double yOffset = 0;

    private double prevWidth;
    private double prevHeight;
    private double prevX;
    private double prevY;

    final MFXButton btnMin = new MFXButton(), btnExit  = new MFXButton();

    // States
    private boolean isMaxable, isHelpable, isMovable, isProgressable, isExitable, isMinable;
    private final BooleanProperty isMaximisedProperty = new SimpleBooleanProperty(false);

    private ShutdownMethod shutdownMethod = ShutdownMethod.APPLICATION;
    private final MinimiseMethod minimiseMethod = MinimiseMethod.ICON;

    // Optionals
    private final MFXButton btnHelp  = new MFXButton(), btnMax  = new MFXButton();
    private final MFXProgressBar progressBar = new MFXProgressBar();
    private final Label progressState = new Label();
    private StageReturnable<?> helpStage;
    private final NexAlert helpMissing = new NexAlert(Alert.AlertType.ERROR,
            "Help Error",
            "Error accessing help menu!",
            """
                    The help menu could not be opened because this application couldn't find it.\s

                    This is most likely because the developer didn't implement one correctly.\s
                    Please let them know.""");

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
    ChangeListener<Boolean> maxListener = (obv, o, n) -> {
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
    private final EventHandler<MouseEvent> helpEvent = e -> {
        if (helpStage == null) {
            helpMissing.close();
            helpMissing.show();
        } else if (!helpStage.isReturned()) {
            this.getScene().getWindow().hide();
            helpStage.returnableStart((Stage) this.getScene().getWindow());
        }
    };

    // Constructors
    public NexWindowBar() {
        this(true, true, true, false, true);
    }

    public NexWindowBar(boolean isExitable, boolean isMaxable, boolean isMinable, boolean isProgressable, boolean  isMovable) {
        this(isExitable, isMaxable, isMinable, isProgressable, false, isMovable, null);
    }

    public NexWindowBar(boolean isExitable, boolean isMaxable, boolean isMinable, boolean isProgressable, boolean isHelpable, boolean  isMovable, StageReturnable<?> helpStage) {
        setUserExitable(isExitable);
        setUserMaxable(isMaxable);
        setUserMinable(isMinable);
        setUserProgressable(isProgressable);
        setUserHelpable(isHelpable);
        setUserMoveable(isMovable);
        this.helpStage = helpStage;

        updateControls(isExitable, isMaxable, isMinable, isProgressable, isHelpable, isMovable);
        //nexBuilder();
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
    public ShutdownMethod getShutdownMethod() { return shutdownMethod; }

    /**
     * Sets the shutdown method for the exit button assigned to this window bar.
     * The shutdown methods act af follows:
     * - WINDOW: Only closes the window/stage called from when set to this option.
     * - JAVAFX: Exits the JavaFX runtime which will close all stages but leave none JavaFX dependant threads running.
     * - APPLICATION: Shutdowns the entire application system. All threads will terminate regardless.
     */
    public void setShutdownMethod(ShutdownMethod method) { this.shutdownMethod = method; }

    /**
     * Sets the returnable Stage to set when the user presses the help button if enabled.
     * Changes are only visable if the help menu isn't already shown.
     * @param helpStage The stage implementing the StageReturnable interface of any type.
     */
    public void setHelpStage(StageReturnable<?> helpStage) {
        this.helpStage = helpStage;

    }

    /*/**
     * This function is used in setting the position more programmatically and conveniently for positioning within a
     * AnchorPane. This has therefore little to no effect when used outside of one.
     * @param position A position relating to the area in a AnchorPane. Note that Baseline is used for just setting the
     *                 Left right or center (both) anchoring without regard for top or bottom.
     */
    /* Will be added to a 2.0 when windowbar can be styled for different orientations.
    public void setPosition(Pos position) {
        switch (position) {
            case TOP_LEFT -> {
                AnchorPane.setLeftAnchor(this, 0d);
                AnchorPane.setTopAnchor(this, 0d);
                AnchorPane.setRightAnchor(this, null);
                AnchorPane.setBottomAnchor(this, null);
            }
            case TOP_CENTER -> {
                AnchorPane.setLeftAnchor(this, 0d);
                AnchorPane.setTopAnchor(this, 0d);
                AnchorPane.setRightAnchor(this, 0d);
                AnchorPane.setBottomAnchor(this, null);
            }
            case TOP_RIGHT -> {
                AnchorPane.setLeftAnchor(this, null);
                AnchorPane.setTopAnchor(this, 0d);
                AnchorPane.setRightAnchor(this, 0d);
                AnchorPane.setBottomAnchor(this, null);
            }
            case CENTER_LEFT -> {
                AnchorPane.setLeftAnchor(this, 0d);
                AnchorPane.setTopAnchor(this, 0d);
                AnchorPane.setRightAnchor(this, null);
                AnchorPane.setBottomAnchor(this, 0d);
            }
            case CENTER -> {
                AnchorPane.setLeftAnchor(this, 0d);
                AnchorPane.setTopAnchor(this, 0d);
                AnchorPane.setRightAnchor(this, 0d);
                AnchorPane.setBottomAnchor(this, 0d);
            }
            case CENTER_RIGHT -> {
                AnchorPane.setLeftAnchor(this, null);
                AnchorPane.setTopAnchor(this, 0d);
                AnchorPane.setRightAnchor(this, 0d);
                AnchorPane.setBottomAnchor(this, 0d);
            }
            case BOTTOM_LEFT -> {
                AnchorPane.setLeftAnchor(this, 0d);
                AnchorPane.setTopAnchor(this, null);
                AnchorPane.setRightAnchor(this, null);
                AnchorPane.setBottomAnchor(this, 0d);
            }
            case BOTTOM_CENTER -> {
                AnchorPane.setLeftAnchor(this, 0d);
                AnchorPane.setTopAnchor(this, null);
                AnchorPane.setRightAnchor(this, 0d);
                AnchorPane.setBottomAnchor(this, 0d);
            }
            case BOTTOM_RIGHT -> {
                AnchorPane.setLeftAnchor(this, null);
                AnchorPane.setTopAnchor(this, null);
                AnchorPane.setRightAnchor(this, 0d);
                AnchorPane.setBottomAnchor(this, 0d);
            }
            case BASELINE_LEFT -> {
                AnchorPane.setLeftAnchor(this, 0d);
                AnchorPane.setTopAnchor(this, null);
                AnchorPane.setRightAnchor(this, null);
                AnchorPane.setBottomAnchor(this, null);
            }
            case BASELINE_CENTER -> {
                AnchorPane.setLeftAnchor(this, 0d);
                AnchorPane.setTopAnchor(this, null);
                AnchorPane.setRightAnchor(this, 0d);
                AnchorPane.setBottomAnchor(this, null);
            }
            case BASELINE_RIGHT -> {
                AnchorPane.setLeftAnchor(this, null);
                AnchorPane.setTopAnchor(this, null);
                AnchorPane.setRightAnchor(this, 0d);
                AnchorPane.setBottomAnchor(this, null);
            }
        }
    }*/

    /**
     * Sets up some controls and styles things not styled from CSS. I.e. because they can't be.
     * @return
     */
   /* protected void nexBuilder() {
        this.getStyleClass().add("NexWindowBar");
        this.getStylesheets().add("/CSS/windowBar.css");

        this.setMaxHeight(28.0);
        this.minWidth(0);
        AnchorPane.setLeftAnchor(this, 0d);
        AnchorPane.setTopAnchor(this, 0d);
        AnchorPane.setRightAnchor(this, 0d);
        AnchorPane.setBottomAnchor(this, null);

        btnHelp.getStyleClass().add("window-button");
        btnHelp.setId("window-help");
        btnHelp.setText("?");
        btnHelp.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(btnHelp, 0D);
        AnchorPane.setLeftAnchor(btnHelp, 0D);
        AnchorPane.setTopAnchor(btnHelp, 0D);

        progressState.setId("window-state");
        progressState.setText("Set Me...");
        AnchorPane.setBottomAnchor(progressState,5d);
        AnchorPane.setLeftAnchor(progressState, 39d);
        AnchorPane.setTopAnchor(progressState,5d);

        progressBar.setId("window-progress");
        AnchorPane.setBottomAnchor(progressBar,0d);
        AnchorPane.setLeftAnchor(progressBar,28d);
        AnchorPane.setTopAnchor(progressBar,26d);

        btnMin.getStyleClass().add("window-button");
        btnMin.setId("window-min");
        btnMin.setText("_"); //_ Underscore
        btnMin.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(btnMin,0d);
        AnchorPane.setRightAnchor(btnMin,56d);
        AnchorPane.setTopAnchor(btnMin,0d);

        btnMax.getStyleClass().add("window-button");
        btnMax.setId("window-max");
        btnMax.setText("\uD83D\uDDD6"); // □
        btnMax.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(btnMax, 0d);
        AnchorPane.setRightAnchor(btnMax, 28d);
        AnchorPane.setTopAnchor(btnMax, 0d);

        btnExit.getStyleClass().add("window-button");
        btnExit.setId("window-exit");
        btnExit.setText("\u2715"); // ✕
        btnExit.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(btnExit, 0d);
        AnchorPane.setRightAnchor(btnExit, 0d);
        AnchorPane.setTopAnchor(btnExit, 0d);

        super.getChildren().add(root);
    }*/

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

    public MFXButton getButtonMin() {
        return btnMin;
    }

    public MFXButton getButtonMax() {
        return btnMax;
    }

    public MFXButton getButtonExit() {
        return btnExit;
    }

    public MFXButton getButtonHelp() {
        return btnHelp;
    }

    public MFXProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getProgressState() {
        return progressState;
    }

    @Override
    protected final ObservableList<Node> getChildren() {
        return layout.getChildren();
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new NexWindowBarSkin(this) {};
    }
}

