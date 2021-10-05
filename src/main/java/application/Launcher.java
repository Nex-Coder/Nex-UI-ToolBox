package application;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lib.abstracts.NexStage;
import ui.NexScene;
import ui.NexStageBase;
import ui.controls.NexWindowBar;
import ui.controls.WindowBar;
import ui.parents.OBox;

/**
 * Essentially the bootstrap to kickstart the main start class.
 */
class Launcher {
    /**
     * The first function called to start all our processes.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(MainStart.class);
    }

    /**
     * Starting point for applications.
     */
    public static class MainStart extends Application {
        @Override
        public void start(Stage stg)  {
            NexStage stage = new NexStageBase(400, 250);

            WindowBar pane = new WindowBar();

            pane.setBorder(new Border(new BorderStroke(Color.BLACK,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

            NexScene scene = new NexScene(pane);
            stage.setScene(scene);
            stage.show();

            //pane.setEnabled(false);
            // look into tray stuff https://github.com/dustinkredmond/FXTrayIcon
        }
    }

}


