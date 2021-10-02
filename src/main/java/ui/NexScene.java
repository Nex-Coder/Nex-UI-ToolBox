package ui;

import ui.controls.NexWindowBar;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class NexScene extends Scene {
    private final String pathToCSS = "/CSS/Nex.css";

    public NexScene() {
        super(new AnchorPane(new NexWindowBar()));
        this.getStylesheets().add(pathToCSS);
        this.setFill(Color.TRANSPARENT);
    }

    public NexScene(Parent root) {
        super(root);
        this.getStylesheets().add(pathToCSS);
        this.setFill(Color.TRANSPARENT);
    }

    public NexScene(Parent root, double width, double height) {
        super(root, width, height);
        this.getStylesheets().add(pathToCSS);
        this.setFill(Color.TRANSPARENT);
    }

    public NexScene(Parent root, Paint fill) {
        super(root, fill);
        this.getStylesheets().add(pathToCSS);
        this.setFill(Color.TRANSPARENT);
    }

    public NexScene(Parent root, double width, double height, Paint fill) {
        super(root, width, height, fill);
        this.getStylesheets().add(pathToCSS);
        this.setFill(Color.TRANSPARENT);
    }

    public NexScene(Parent root, double width, double height, boolean depthBuffer) {
        super(root, width, height, depthBuffer);
        this.getStylesheets().add(pathToCSS);
        this.setFill(Color.TRANSPARENT);
    }

    public NexScene(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing) {
        super(root, width, height, depthBuffer, antiAliasing);
        this.getStylesheets().add(pathToCSS);
        this.setFill(Color.TRANSPARENT);
    }
}
