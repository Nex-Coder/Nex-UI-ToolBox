package lib.interfaces;

import javafx.scene.Node;

public interface Wizardable<N extends Node> {

    boolean previousActiveChild();

    boolean nextActiveChild();

    void setActiveChildIndex(int index);

    int getActiveIndex();

    N getActiveChild();
}
