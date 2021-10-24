package application;

import javafx.application.Application;
import javafx.stage.Stage;
import lib.abstracts.NexStage;
import ui.NexScene;
import ui.NexStageBase;
import ui.controls.WindowBar;

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

            WindowBar w = new WindowBar(true, true, true, true, true, true, false);

            NexScene scene = new NexScene(w);
            stage.setScene(scene);
            stage.show();

            w.setHelpEventHandler(e -> {});
            w.setUserHelpable(true);

            //pane.setEnabled(false);
            // look into tray stuff https://github.com/dustinkredmond/FXTrayIcon
            // I did some research and the plugin abstracts AWT Tray logic for a JavaFX rendition. Best bet unless we use AWT raw.
        }
    }

}


