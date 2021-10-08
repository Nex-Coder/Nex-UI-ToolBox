package ui.skins;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lib.WindowBarControl;
import ui.controls.WindowBar;

import java.util.Map;

import static javafx.geometry.Pos.CENTER;
import static javafx.scene.paint.Color.WHITE;

public class WindowBarSkin extends SkinBase<WindowBar> {

    private final AnchorPane layout;

    private final Button btnMin, btnExit, btnHelp, btnMax;
    private final ProgressBar progressBar;
    private final Label progressState;

    ChangeListener<Number> clippingListener;

    /**
     * Constructor for all SkinBase instances.
     *  @param control The control for which this Skin should attach to.
     * */
    protected WindowBarSkin(WindowBar control) {
        super(control);
        layout = new AnchorPane();
        getChildren().add(layout);

        btnMin = control.getButtonMin();
        btnMax = control.getButtonMax();
        btnExit = control.getButtonExit();
        btnHelp = control.getButtonHelp();
        progressBar = control.getProgressBar();
        progressState = control.getProgressState();

        Map<WindowBarControl, Boolean> states = control.getStates();
        states.keySet().stream().iterator().forEachRemaining((e) -> setControlEnabled(e, states.get(e)));

        defaultStyling(control);
        setClipping(true);
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

    public Button getButtonMin() {
        return btnMin;
    }

    public Button getButtonExit () {
        return btnExit;
    }

    public Button getButtonHelp() {
        return btnHelp;
    }

    public Button getButtonMax() {
        return btnMax;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getProgressState() {
        return progressState;
    }

    public void defaultStyling(WindowBar control) {
        layout.getStyleClass().add("NexWindowBar");
        layout.setStyle("-fx-background-color: #474343;");

        control.setMaxHeight(28.0);
        layout.maxHeightProperty().bind(control.maxHeightProperty());
        AnchorPane.setLeftAnchor(control, 0d);
        AnchorPane.setTopAnchor(control, 0d);
        AnchorPane.setRightAnchor(control, 0d);
        AnchorPane.setBottomAnchor(control, null);

        btnHelp.getStyleClass().add("window-button");
        btnHelp.setId("window-help");
        setButtonDefaults(btnHelp, 3,"""
			M211.343,422.686C94.812,422.686,0,327.882,0,211.343C0,94.812,94.812,0,211.343,0
			s211.343,94.812,211.343,211.343C422.694,327.882,327.882,422.686,211.343,422.686z M211.343,16.257
			c-107.565,0-195.086,87.52-195.086,195.086s87.52,195.086,195.086,195.086c107.574,0,195.086-87.52,195.086-195.086
			S318.917,16.257,211.343,16.257z
			M192.85,252.88l-0.569-7.397c-1.707-15.371,3.414-32.149,17.647-49.227
			c12.811-15.078,19.923-26.182,19.923-38.985c0-14.51-9.112-24.182-27.044-24.467c-10.242,0-21.622,3.414-28.735,8.819
			l-6.828-17.924c9.388-6.828,25.605-11.38,40.692-11.38c32.726,0,47.52,20.2,47.52,41.83c0,19.346-10.811,33.295-24.483,49.511
			c-12.51,14.794-17.07,27.312-16.216,41.83l0.284,7.397H192.85V252.88z M186.583,292.718c0-10.526,7.121-17.923,17.078-17.923
			c9.966,0,16.785,7.397,16.785,17.924c0,9.957-6.544,17.639-17.07,17.639C193.419,310.349,186.583,302.667,186.583,292.718z""");
        mouseHoverColourEffect(btnHelp, "#565050", "#474343");
        AnchorPane.setBottomAnchor(btnHelp, 0D);
        AnchorPane.setLeftAnchor(btnHelp, 0D);
        AnchorPane.setTopAnchor(btnHelp, 0D);

        progressState.setId("window-state");
        progressState.setText("Set Me...");
        progressState.setAlignment(CENTER);
        progressState.setTextAlignment(TextAlignment.CENTER);
        progressState.setPrefHeight(18);
        progressState.setPrefWidth(132);
        progressState.setMaxHeight(18);
        progressState.setMaxWidth(150);
        progressState.setTextFill(WHITE);
        AnchorPane.setBottomAnchor(progressState,5d);
        AnchorPane.setLeftAnchor(progressState, 39d);
        AnchorPane.setTopAnchor(progressState,5d);

        progressBar.setId("window-progress");
        progressBar.setMaxHeight(2);
        progressBar.setMaxWidth(150);
        AnchorPane.setBottomAnchor(progressBar,0d);
        AnchorPane.setLeftAnchor(progressBar,28d);
        AnchorPane.setTopAnchor(progressBar,26d);

        btnMin.getStyleClass().add("window-button");
        btnMin.setId("window-min");
        setButtonDefaults(btnMin, 1.6, "M 10 0 z M 2 17 h 16 v 2 H 2 v -2 z");
        mouseHoverColourEffect(btnMin, "#565050", "#474343");
        AnchorPane.setBottomAnchor(btnMin,0d);
        AnchorPane.setRightAnchor(btnMin,56d);
        AnchorPane.setTopAnchor(btnMin,0d);

        btnMax.getStyleClass().add("window-button");
        btnMax.setId("window-max");
        setButtonDefaults(btnMax, 1.6,"M4 21h16c1.103 0 2-.897 2-2V5c0-1.103-.897-2-2-2H4c-1.103 0-2 .897-2 2v14c0 1.103.897 2 2 2zm0-2V7h16l.001 12H4z");
        mouseHoverColourEffect(btnMax, "#565050", "#474343");
        AnchorPane.setBottomAnchor(btnMax, 0d);
        AnchorPane.setRightAnchor(btnMax, 28d);
        AnchorPane.setTopAnchor(btnMax, 0d);

        btnExit.getStyleClass().add("window-button");
        setButtonDefaults(btnExit,1.6, "M5.72 5.72a.75.75 0 011.06 0L12 10.94l5.22-5.22a.75.75 0 111.06 1.06L13.06 12l5.22 5.22a.75.75 0 11-1.06 1.06L12 13.06l-5.22 5.22a.75.75 0 01-1.06-1.06L10.94 12 5.72 6.78a.75.75 0 010-1.06z");
        btnExit.setId("window-exit");
        btnExit.setAlignment(CENTER);
        mouseHoverColourEffect(btnExit, "#ff3932", "#c10700");
        AnchorPane.setBottomAnchor(btnExit, 0d);
        AnchorPane.setRightAnchor(btnExit, 0d);
        AnchorPane.setTopAnchor(btnExit, 0d);
    }

    public void mouseHoverColourEffect(final Node node, String hoverColour, String defaultColour) {
        node.setStyle("-fx-background-color: " + defaultColour); // #474343
        node.setOnMouseEntered(mouseEvent -> node.setStyle("-fx-background-color: " + hoverColour));
        node.setOnMouseExited(mouseEvent -> node.setStyle("-fx-background-color: " + defaultColour));
    }

    private void setButtonDefaults(Button btn, double svgSizeFactor, String svgPath) {
        final int size = 28;
        btn.setMinHeight(size);
        btn.setMinWidth(size);
        btn.setPrefHeight(size);
        btn.setPrefWidth(size);
        btn.setMinHeight(size);
        btn.setMinWidth(size);
        btn.setStyle("-fx-border-width: 0");
        btn.setStyle("-fx-border-insets: 0");
        btn.setStyle("-fx-border-style: none");
        btn.setBorder(null);
        btn.setStyle("-fx-background-radius: 0");


        SVGPath svg = new SVGPath();
        svg.setContent(svgPath);
        final Region svgShape = new Region();
        svgShape.setShape(svg);

        svgShape.minWidthProperty().bind((Bindings.subtract(btn.minWidthProperty(), btn.getMinWidth() / svgSizeFactor)));
        svgShape.minHeightProperty().bind((Bindings.subtract(btn.minHeightProperty(), btn.getMinHeight() / svgSizeFactor)));
        svgShape.prefWidthProperty().bind((Bindings.subtract(btn.prefWidthProperty(), btn.getPrefWidth() / svgSizeFactor)));
        svgShape.prefHeightProperty().bind((Bindings.subtract(btn.prefHeightProperty(), btn.getPrefHeight() / svgSizeFactor)));
        svgShape.maxWidthProperty().bind((Bindings.subtract(btn.maxWidthProperty(), btn.getMaxWidth() / svgSizeFactor)));
        svgShape.maxHeightProperty().bind((Bindings.subtract(btn.maxHeightProperty(), btn.getMaxHeight() / svgSizeFactor)));
        svgShape.setStyle("-fx-background-color: white;");

        btn.setPickOnBounds(true);
        btn.setGraphic(svgShape);

        btn.setFont(Font.font("Arial", 3));
        btn.setTextFill(WHITE);
    }

    public void setClipping(boolean enable) {
        if (enable) {
            try {
                layout.heightProperty().removeListener(clippingListener);
            } catch (NullPointerException | IllegalArgumentException ignored) {}
            generateClipListener();
            layout.heightProperty().addListener(clippingListener);
        } else {
            try {
                layout.heightProperty().removeListener(clippingListener);
            } catch (NullPointerException | IllegalArgumentException ignored) {}
        }
    }

    private void generateClipListener() {
        clippingListener =  (obv, o, n) -> layout.setClip(new Rectangle(Double.MAX_VALUE, n.doubleValue()));
    }
}
