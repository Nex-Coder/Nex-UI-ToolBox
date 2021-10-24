package ui.skins;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lib.enums.WindowBarControl;
import ui.controls.WindowBar;
import ui.parents.OBox;

import java.util.Map;

import static javafx.geometry.Pos.CENTER;
import static javafx.scene.paint.Color.WHITE;

public class WindowBarSkin extends SkinBase<WindowBar> {

    private final AnchorPane layout;

    private final OBox leftGroup, rightGroup;

    private final Button btnMin, btnExit, btnHelp, btnMax, btnSettings;
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

        leftGroup = control.getLeftGroup();
        rightGroup = control.getRightGroup();
        btnMin = control.getButtonMin();
        btnMax = control.getButtonMax();
        btnExit = control.getButtonExit();
        btnHelp = control.getButtonHelp();
        btnSettings = control.getButtonSettings();
        progressBar = control.getProgressBar();
        progressState = control.getProgressState();

        Map<WindowBarControl, Boolean> states = control.getStates();
        states.keySet().stream().iterator().forEachRemaining((e) -> setControlEnabled(e, states.get(e)));

        defaultStyling(control);
        setClipping(true);

        control.sortGroup(true);
        control.sortGroup(false);
    }

    public void setControlEnabled(WindowBarControl control, boolean enable) {
        try {
            switch (control) {
                case PROGRESS_STATE -> setSkinNode(progressState, enable);
                case PROGRESS_BAR -> setSkinNode(progressBar, enable);
                case BUTTON_MIN -> setSkinNode(btnMin, enable, false);
                case BUTTON_MAX -> setSkinNode(btnMax, enable, false);
                case BUTTON_EXIT -> setSkinNode(btnExit, enable, false);
                case BUTTON_HELP -> setSkinNode(btnHelp, enable, true);
                case BUTTON_SETTINGS -> setSkinNode(btnSettings, enable, true);
            }
        } catch (IllegalArgumentException ignored) {}
    }

    private void setControlEnabled(boolean enable, WindowBarControl... control) throws IllegalArgumentException {
        for (WindowBarControl e : control) {
            setControlEnabled(e, enable);
        }
    }

    /**
     * Adds/removes a node directly to the layout safely.
     * @param n The node to add/remove to/from the layout.
     * @param enable If ture the node is added (enabled) otherwise it is removed (disabled).
     */
    private void setSkinNode(Node n, boolean enable) throws IllegalArgumentException {
        if (enable) layout.getChildren().add(n);
        else layout.getChildren().remove(n);
    }

    /**
     * Adds/removes a node directly to a layout group safely.
     * @param n The node to add/remove to/from the layout group.
     * @param enable If ture the node is added (enabled) otherwise it is removed (disabled).
     * @param left If ture the node is added/removed from the left group, otherwise the right group.
     */
    private void setSkinNode(Node n, boolean enable, boolean left) throws IllegalArgumentException {
        if (enable) (left ? leftGroup : rightGroup).getChildren().add(n);
        else (left ? leftGroup : rightGroup).getChildren().remove(n);
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

        AnchorPane.setTopAnchor(leftGroup, 0d);
        AnchorPane.setLeftAnchor(leftGroup, 0d);
        AnchorPane.setBottomAnchor(leftGroup, 0d);

        AnchorPane.setTopAnchor(rightGroup, 0d);
        AnchorPane.setRightAnchor(rightGroup, 0d);
        AnchorPane.setBottomAnchor(rightGroup, 0d);

        layout.getChildren().addAll(leftGroup, rightGroup);

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

        btnSettings.getStyleClass().add("window-button");
        setButtonDefaults(btnSettings,3.3, "M211.343,422.686C94.812,422.686,0,327.882,0,211.343C0,94.812,94.812,0,211.343,0 " +
                "s211.343,94.812,211.343,211.343C422.694,327.882,327.882,422.686,211.343,422.686z M211.343,16.257 " +
                "c-107.565,0-195.086,87.52-195.086,195.086s87.52,195.086,195.086,195.086c107.574,0,195.086-87.52,195.086-195.086 " +
                "S318.917,16.257,211.343,16.257z " +
                "M 210.057 165.1 c -24.789 0 -44.955 20.167 -44.955 44.955 s 20.166 44.955 44.955 44.955 c 24.789 0 44.955 -20.167 44.955 -44.955 S 234.845 165.1 210.057 165.1 z M 210.057 238.36 c -15.607 0 -28.305 -12.697 -28.305 -28.305 s 12.697 -28.305 28.305 -28.305 c 15.608 0 28.305 12.697 28.305 28.305 S 225.663 238.36 210.057 238.36 z M 357.365 243.342 l -25.458 -22.983 v -20.608 l 25.457 -22.981 c 2.614 -2.361 3.461 -6.112 2.112 -9.366 l -13.605 -32.846 c -1.348 -3.253 -4.588 -5.305 -8.115 -5.128 l -34.252 1.749 l -14.571 -14.571 l 1.749 -34.251 c 0.18 -3.518 -1.874 -6.769 -5.128 -8.116 L 252.707 60.635 c -3.253 -1.35 -7.005 -0.501 -9.365 2.111 l -22.984 25.458 h -20.606 L 176.77 62.746 c -2.361 -2.613 -6.112 -3.458 -9.365 -2.111 L 134.559 74.24 c -3.255 1.348 -5.308 4.599 -5.128 8.116 l 1.75 34.251 L 116.609 131.178 l -34.252 -1.749 c -3.506 -0.188 -6.768 1.874 -8.115 5.128 L 60.635 167.403 c -1.348 3.255 -0.502 7.005 2.112 9.366 l 25.457 22.981 v 20.608 L 62.749 243.341 c -2.614 2.361 -3.461 6.112 -2.112 9.366 l 13.605 32.846 c 1.348 3.255 4.603 5.321 8.115 5.128 l 34.252 -1.749 l 14.572 14.571 l -1.75 34.251 c -0.18 3.518 1.874 6.769 5.128 8.116 l 32.846 13.606 c 3.255 1.352 7.005 0.502 9.365 -2.111 l 22.984 -25.458 h 20.606 l 22.984 25.458 c 1.613 1.785 3.873 2.746 6.182 2.746 c 1.071 0 2.152 -0.208 3.183 -0.634 l 32.846 -13.606 c 3.255 -1.348 5.308 -4.599 5.128 -8.116 l -1.749 -34.251 l 14.571 -14.571 l 34.252 1.749 c 3.506 0.178 6.768 -1.874 8.115 -5.128 l 13.605 -32.846 C 360.825 249.453 359.979 245.703 357.365 243.342 z M 332.737 273.754 l -32.079 -1.639 c -2.351 -0.127 -4.646 0.764 -6.311 2.428 l -19.804 19.804 c -1.666 1.666 -2.547 3.958 -2.428 6.311 l 1.638 32.079 l -21.99 9.109 l -21.525 -23.843 c -1.578 -1.747 -3.824 -2.746 -6.179 -2.746 h -28.006 c -2.355 0 -4.601 0.998 -6.179 2.746 l -21.525 23.843 l -21.99 -9.109 l 1.639 -32.079 c 0.12 -2.353 -0.763 -4.646 -2.429 -6.311 l -19.803 -19.804 c -1.665 -1.665 -3.955 -2.55 -6.311 -2.428 l -32.079 1.639 l -9.109 -21.99 l 23.842 -21.525 c 1.748 -1.58 2.746 -3.824 2.746 -6.179 v -28.008 c 0 -2.355 -0.998 -4.601 -2.746 -6.179 l -23.842 -21.525 l 9.109 -21.99 l 32.079 1.639 c 2.354 0.124 4.646 -0.763 6.311 -2.428 l 19.803 -19.803 c 1.666 -1.666 2.549 -3.958 2.429 -6.313 l -1.639 -32.079 l 21.99 -9.109 l 21.525 23.842 c 1.578 1.747 3.824 2.746 6.179 2.746 h 28.006 c 2.355 0 4.601 -0.998 6.179 -2.746 l 21.525 -23.842 l 21.99 9.109 l -1.638 32.079 c -0.12 2.353 0.761 4.645 2.428 6.313 l 19.804 19.803 c 1.666 1.665 3.959 2.564 6.311 2.428 l 32.079 -1.639 l 9.109 21.99 l -23.843 21.525 c -1.748 1.58 -2.746 3.824 -2.746 6.179 v 28.008 c 0 2.355 0.998 4.601 2.746 6.179 l 23.843 21.525 L 332.737 273.754 z M 210.057 131.357 c -43.394 0 -78.698 35.305 -78.698 78.698 c 0 43.394 35.304 78.698 78.698 78.698 c 43.394 0 78.698 -35.305 78.698 -78.698 C 288.754 166.661 253.45 131.357 210.057 131.357 z M 210.057 272.103 c -34.214 0 -62.048 -27.834 -62.048 -62.048 c 0 -34.214 27.834 -62.048 62.048 -62.048 c 34.214 0 62.048 27.834 62.048 62.048 C 272.105 244.269 244.269 272.103 210.057 272.103 z");
        btnSettings.setId("window-settings");
        btnSettings.setAlignment(CENTER);
        mouseHoverColourEffect(btnSettings, "#565050", "#474343");
        AnchorPane.setBottomAnchor(btnSettings, 0d);
        AnchorPane.setLeftAnchor(btnSettings, 0d);
        AnchorPane.setTopAnchor(btnSettings, 0d);
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
