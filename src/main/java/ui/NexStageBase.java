package ui;

import javafx.animation.*;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import lib.abstracts.NexStage;

import java.util.concurrent.atomic.AtomicBoolean;

public class NexStageBase extends NexStage {
    ResizeListener resizeListener;
    ReadOnlyBooleanWrapper returnedWrapper = new ReadOnlyBooleanWrapper(false);
    Stage caller;

    /**
     * Creates a new instance of NexStageBase {@code Stage}.
     *
     * @throws IllegalStateException if this constructor is called on a thread
     * other than the JavaFX Application Thread.
     */
    public NexStageBase() {
        super();
        enableResize();
    }

    public NexStageBase(String title, Image icon) {
        super(title, icon);
        enableResize();

        this.getIcons().add(icon);
        this.setTitle(title);
    }

    public NexStageBase(int minWidth, int minHeight) {
        super(minWidth, minHeight);
        enableResize();
    }

    public NexStageBase(int minWidth, int minHeight, int width, int height) {
        super(minWidth, minHeight, width, height);
        enableResize();
    }

    public NexStageBase(String title, Image icon, int minWidth, int minHeight) {
        super(title, icon, minWidth, minHeight);
        enableResize();
    }

    public NexStageBase(String title, Image icon, int minWidth, int minHeight, int width, int height) {
        super(title, icon, minWidth, minHeight, width, height);
        enableResize();
    }

    @Override
    protected void transitionIn() {
        FadeTransition trans = new FadeTransition(Duration.millis(1500), this.getScene().getRoot());
        trans.setFromValue(0.0);
        trans.setToValue(1.0);
        trans.play();
    }

    @Override
    protected void transitionOut() {
        FadeTransition trans = new FadeTransition(Duration.millis(1500), this.getScene().getRoot());
        trans.setFromValue(1.0);
        trans.setToValue(0.0);
        trans.play();
    }

    @Override
    protected void animationShake() {
        final AtomicBoolean x = new AtomicBoolean(), y = new AtomicBoolean();

        Timeline tX = new Timeline(new KeyFrame(Duration.millis(55), e -> {
            if (x.get()) {
                this.setX(this.getX() - 10);
                x.set(false);
            } else {
                this.setX(this.getX() + 10);
                x.set(true);
            }
        }));

        tX.setCycleCount(20); // this * frame_duration = total_duration. If total is odd, start placement is lost by +10
        tX.play();
    }

    @Override
    protected void animationHalfFade(boolean in) {
        FadeTransition trans = new FadeTransition(Duration.millis(1500), this.getScene().getRoot());
        trans.setFromValue(in ? 0.5 : 1);
        trans.setToValue(in ? 1 : 0.5);
        trans.play();
    }

    @Override
    protected void animationIconFlash() {

    }

    @Override
    protected void animationIconNotification() {

    }

    @Override
    protected void animationNone() {

    }

    @Override
    public void enableResize() {
        if (!isResizable()) {
            resizeListener = this.addResizeListener(this);
            setResizable(true);
        }
    }

    @Override
    public void disableResize() {
        if (isResizable()) {
            this.removeEventHandler(MouseEvent.MOUSE_PRESSED, resizeListener);
            setResizable(false);
        }
    }
}
