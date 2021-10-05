package ui.skins;

import javafx.beans.InvalidationListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.checkerframework.checker.units.qual.A;
import ui.controls.NexWindowBar;

public class NexWindowBarSkin extends SkinBase<NexWindowBar> {


    private static final double GAP_SIZE = 10;

    private static final String CATEGORIZED_TYPES = "LRHEYNXBIACO"; //$NON-NLS-1$

    // pick an arbitrary number
    private static final double DO_NOT_CHANGE_SIZE = Double.MAX_VALUE - 100;


    private final AnchorPane layout = new AnchorPane();
    //private InvalidationListener buttonDataListener = o -> layoutButtons();

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    public NexWindowBarSkin(final NexWindowBar control) {
        super(control);
        getChildren().add(this.layout);
        layout.getChildren().add(new Label("Im WindowBar"));
        doDefaultLayouts(control);
    }


    protected void doDefaultLayouts(NexWindowBar control) {
        System.out.println("skin");
        control.getStyleClass().add("NexWindowBar");
        control.getStylesheets().add("/CSS/windowBar.css");

        control.setMaxHeight(28.0);
        control.minWidth(0);
        AnchorPane.setLeftAnchor(control, 0d);
        AnchorPane.setTopAnchor(control, 0d);
        AnchorPane.setRightAnchor(control, 0d);
        AnchorPane.setBottomAnchor(control, null);

        Button btnHelp = control.getButtonHelp();
        btnHelp.getStyleClass().add("window-button");
        btnHelp.setId("window-help");
        btnHelp.setText("?");
        btnHelp.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(btnHelp, 0D);
        AnchorPane.setLeftAnchor(btnHelp, 0D);
        AnchorPane.setTopAnchor(btnHelp, 0D);

        Label progressState = control.getProgressState();
        progressState.setId("window-state");
        progressState.setText("Set Me...");
        AnchorPane.setBottomAnchor(progressState,5d);
        AnchorPane.setLeftAnchor(progressState, 39d);
        AnchorPane.setTopAnchor(progressState,5d);

        ProgressBar progressBar = control.getProgressBar();
        progressBar.setId("window-progress");
        AnchorPane.setBottomAnchor(progressBar,0d);
        AnchorPane.setLeftAnchor(progressBar,28d);
        AnchorPane.setTopAnchor(progressBar,26d);

        Button btnMin = control.getButtonMin();
        btnMin.getStyleClass().add("window-button");
        btnMin.setId("window-min");
        btnMin.setText("_"); //_ Underscore
        btnMin.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(btnMin,0d);
        AnchorPane.setRightAnchor(btnMin,56d);
        AnchorPane.setTopAnchor(btnMin,0d);

        Button btnMax = control.getButtonMax();
        btnMax.getStyleClass().add("window-button");
        btnMax.setId("window-max");
        btnMax.setText("\uD83D\uDDD6"); // □
        btnMax.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(btnMax, 0d);
        AnchorPane.setRightAnchor(btnMax, 28d);
        AnchorPane.setTopAnchor(btnMax, 0d);

        Button btnExit = control.getButtonExit();
        btnExit.getStyleClass().add("window-button");
        btnExit.setId("window-exit");
        btnExit.setText("\u2715"); // ✕
        btnExit.setTextFill(Color.WHITE);
        AnchorPane.setBottomAnchor(btnExit, 0d);
        AnchorPane.setRightAnchor(btnExit, 0d);
        AnchorPane.setTopAnchor(btnExit, 0d);
        layout.getChildren().add(btnExit);
    }
}
