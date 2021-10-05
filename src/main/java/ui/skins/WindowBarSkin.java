package ui.skins;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import ui.controls.WindowBar;

public class WindowBarSkin extends SkinBase<WindowBar> {

    private final AnchorPane layout;

    private final MFXButton btnMin = new MFXButton(), btnExit  = new MFXButton(), btnHelp = new MFXButton(), btnMax = new MFXButton();
    private final MFXProgressBar progressBar = new MFXProgressBar();
    private final Label progressState = new Label("State Undefined...");

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected WindowBarSkin(WindowBar control) {
        super(control);
        layout = new AnchorPane();
        layout.getChildren().add(progressState);
        getChildren().add(layout);
        defaultStyling();
    }

    public void setControlEnabled(WindowBarControl control, boolean enable) {
        try {
            switch (control) {
                case PROGRESS_STATE -> {
                    if (enable) layout.getChildren().add(progressState);
                    else layout.getChildren().remove(progressState);
                }
                case PROGRESS_BAR -> {
                    if (enable) layout.getChildren().add(progressBar);
                    else layout.getChildren().remove(progressBar);
                }
                case BUTTON_MIN -> {
                    if (enable) layout.getChildren().add(btnMin);
                    else layout.getChildren().remove(btnMin);
                }
                case BUTTON_MAX -> {
                    if (enable) layout.getChildren().add(btnMax);
                    else layout.getChildren().remove(btnMax);
                }
                case BUTTON_EXIT -> {
                    if (enable) layout.getChildren().add(btnExit);
                    else layout.getChildren().remove(btnExit);
                }
                case BUTTON_HELP -> {
                    if (enable) layout.getChildren().add(btnHelp);
                    else layout.getChildren().remove(btnHelp);
                }
            }
        } catch (IllegalArgumentException ignored) {}
    }

    public MFXButton getButtonMin() {
        return btnMin;
    }

    public MFXButton getButtonExit () {
        return btnExit;
    }

    public MFXButton getButtonHelp() {
        return btnHelp;
    }

    public MFXButton getButtonMax() {
        return btnMax;
    }

    public MFXProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getProgressState() {
        return progressState;
    }

    public void defaultStyling() {
        layout.getStyleClass().add("NexWindowBar");
        layout.getStylesheets().add("/CSS/windowBar.css");

        layout.setMaxHeight(28.0);
        layout.minWidth(0);
        AnchorPane.setLeftAnchor(layout, 0d);
        AnchorPane.setTopAnchor(layout, 0d);
        AnchorPane.setRightAnchor(layout, 0d);
        AnchorPane.setBottomAnchor(layout, null);

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
        layout.getChildren().add(btnExit);
    }

    public enum WindowBarControl {
        PROGRESS_STATE,
        PROGRESS_BAR,
        BUTTON_MIN,
        BUTTON_MAX,
        BUTTON_EXIT,
        BUTTON_HELP
    }
}
