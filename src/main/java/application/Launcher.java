package application;

import javafx.application.Application;
import javafx.stage.Stage;

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
        public void start(Stage stage)  {
        }
    }

}


