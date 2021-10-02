package ui.controls;

import javafx.beans.DefaultProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class NexAlert extends Alert {

    public NexAlert(AlertType alertType) {
        super(alertType);
        this.initTheme();
    }

    public NexAlert(AlertType alertType,
                    String contentText,
                    ButtonType... buttons) {
        super(alertType, contentText, buttons);
        this.initTheme();
    }

    public NexAlert(AlertType alertType,
                    String title,
                    String header,
                    String contentText) {
        super(alertType);
        this.initTheme();

        this.setTitle(title);
        this.setHeaderText(header);
        this.setContentText(contentText);
    }

    public NexAlert(AlertType alertType,
                    String title,
                    String header,
                    String contentText,
                    ButtonType... buttons) {
        super(alertType, contentText, buttons);
        this.initTheme();

        this.setTitle(title);
        this.setHeaderText(header);
        this.setContentText(contentText);

    }

    public void initTheme() {
        DialogPane dialogPane = this.getDialogPane();
        dialogPane.getStylesheets().add( getClass().getResource("/CSS/alert.css").toExternalForm());
        dialogPane.getStyleClass().add("NexAlert");
    }
}
