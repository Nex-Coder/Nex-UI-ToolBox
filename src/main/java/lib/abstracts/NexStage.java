package lib.abstracts;

import lib.interfaces.UndecoratedResizable;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class NexStage extends Stage implements UndecoratedResizable {
    private double lastWidth, lastHeight;

    /*--- General Methods ----*/

    // Constructors
    public NexStage() {
        defaultStaging();
    }

    public NexStage(String title) {
        defaultStaging();

        this.setTitle(title);
    }

    public NexStage(String title, Image icon) {
        defaultStaging();

        this.setTitle(title);
        this.getIcons().add(icon);
    }

    public NexStage(int minWidth, int minHeight) {
        defaultStaging();

        this.setMinWidth(minWidth);
        this.setMinHeight(minHeight);
        this.setWidth(minWidth);
        this.setHeight(minHeight);
    }

    public NexStage(int minWidth, int minHeight, int width, int height) {
        defaultStaging();

        this.setMinWidth(minWidth);
        this.setMinHeight(minHeight);
        this.setMinWidth(width);
        this.setMinHeight(height);
    }

    public NexStage(String title, Image icon, int minWidth, int minHeight) {
        defaultStaging();

        this.setTitle(title);
        this.getIcons().add(icon);

        this.setMinWidth(minWidth);
        this.setMinHeight(minHeight);
        this.setWidth(minWidth);
        this.setHeight(minHeight);
    }

    public NexStage(String title, Image icon, int minWidth, int minHeight, int width, int height) {
        defaultStaging();

        this.setTitle(title);
        this.getIcons().add(icon);

        this.setMinWidth(minWidth);
        this.setMinHeight(minHeight);
        this.setWidth(minWidth);
        this.setHeight(minHeight);
        this.setMinWidth(width);
        this.setMinHeight(height);
    }

    private void defaultStaging() {
        this.setResizable(false);

        this.initStyle(StageStyle.TRANSPARENT);

        this.getIcons().add(new Image("file:src/main/resources/nex.png"));
        this.setTitle("NexUI");

        this.setMinWidth(640);
        this.setMinHeight(360);
        this.setLastWidth(640);
        this.setLastHeight(360);
        this.setWidth(640);
        this.setHeight(360);

        this.onShownProperty().addListener((obv, o, n) -> {if (isShowing()) {transitionIn();}}); //trans in if shown.
    }

    public void resizeToMin() { this.setWidth(this.getMinWidth()); this.setHeight(this.getMinHeight());}
    public void resizeToPref() {}
    public void resizeToMax() { this.setWidth(this.getMaxWidth()); this.setHeight(this.getMaxHeight());}

    /*---- Transitions ----*/
    protected abstract void transitionIn();
    protected abstract void transitionOut();

    public void transition(boolean in) {
        if (in) {
            transitionIn();
        } else {
            transitionOut();
        }
    };

    /*---- Animations ----*/
    protected abstract void animationShake();
    protected abstract void animationHalfFade(boolean in);
    protected abstract void animationIconFlash();
    protected abstract void animationIconNotification();
    protected abstract void animationNone();

    public void animate(Animation animation) {
        switch (animation) {
            case SHAKE -> animationShake();
            case HALF_FADE_IN -> animationHalfFade(true);
            case HALF_FADE_OUT -> animationHalfFade(false);
            case ICON_FLASH -> animationIconFlash();
            case ICON_NOTIFICATION -> animationIconNotification();
            case NONE -> animationNone();
        }
    };

    public enum Animation {
        SHAKE,
        HALF_FADE_IN,
        HALF_FADE_OUT,
        ICON_FLASH,
        ICON_NOTIFICATION,
        NONE,
    }

    /*---- Stage Overrides ----*/
    @Override
    public void close() {
        transitionOut();
        super.close();
    }

    @Override
    public void hide() {
        transitionOut();
        super.hide();
    }

    /*---- Get / Set ----*/

    public double getLastHeight() { return lastHeight; }
    public void setLastHeight(double height) { lastHeight = height; }

    public double getLastWidth() { return lastWidth; }
    public void setLastWidth(double Width) { lastWidth = Width; }
}
