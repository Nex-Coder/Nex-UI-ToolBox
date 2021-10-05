package ui.controls;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import lib.interfaces.StageReturnable;
import ui.skins.WindowBarSkin;

public class WindowBar extends Control {
    private Window stage; // Most cases this can be hard coded. But if a user changes the scene this will continue to work.
    private double xOffset = 0;
    private double yOffset = 0;

    private double prevWidth;
    private double prevHeight;
    private double prevX;
    private double prevY;

    private final MFXButton btnMin, btnExit, btnHelp, btnMax;
    private final MFXProgressBar progressBar;
    private final Label progressState;

    // States
    private boolean isMaxable, isHelpable, isMovable, isProgressable, isExitable, isMinable;
    private final BooleanProperty isMaximisedProperty = new SimpleBooleanProperty(false);

    private NexWindowBar.ShutdownMethod shutdownMethod = NexWindowBar.ShutdownMethod.APPLICATION;
    private final NexWindowBar.MinimiseMethod minimiseMethod = NexWindowBar.MinimiseMethod.ICON;

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

    private ChangeListener<Boolean> maxListener;

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

    public WindowBar() {
        super();
        setSkin(createDefaultSkin());
        WindowBarSkin skin = (WindowBarSkin) getSkin();
        skin.setControlEnabled(WindowBarSkin.WindowBarControl.PROGRESS_STATE, true);
        skin.setControlEnabled(WindowBarSkin.WindowBarControl.PROGRESS_BAR, true);
        skin.setControlEnabled(WindowBarSkin.WindowBarControl.BUTTON_EXIT, true);
        skin.setControlEnabled(WindowBarSkin.WindowBarControl.BUTTON_HELP, true);
        skin.setControlEnabled(WindowBarSkin.WindowBarControl.BUTTON_MAX, true);
        skin.setControlEnabled(WindowBarSkin.WindowBarControl.BUTTON_MIN, true);

        this.progressBar = skin.getProgressBar();
        this.progressState = skin.getProgressState();
        this.btnExit = skin.getButtonExit();
        this.btnMin = skin.getButtonMin();
        this.btnMax = skin.getButtonMax();
        this.btnHelp = skin.getButtonHelp();

        maxListener = createMaxChangeListener();
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

}
