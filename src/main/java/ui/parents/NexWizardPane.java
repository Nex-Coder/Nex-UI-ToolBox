package ui.parents;

import lib.interfaces.Wizardable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.css.converter.BooleanConverter;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.*;

/**
 * <h1>NexWizardPane</h1>
 * <p>The constructed object of this class is a programmatic Control that stores and displays parents in a Wizard fashion.
 * This is similar to a TabPane which has the same functionality but with a UI control in mind. As such, this method
 * will just hold one displayed Parent node and then store the others with the intended purpose of switching between
 * the parents in a programmatic fashion or with another node/class.
 * <BR><BR>
 * It is important to note that if you remove a child from an instance of NexWizardPane that is considered <i>active</i>
 * then you must set a new active child to stop displaying it.</p>
 */
public class NexWizardPane extends Pane implements Wizardable<Node> {

    /*================================================================================================================*\
    || Fields
    \*================================================================================================================*/

    private final ObservableWizardList children = new ObservableWizardList();

    // Properties
    public final BooleanProperty wrapChildTraversalProperty() {
        if (wrapChildTraversal == null) {
            wrapChildTraversal = new StyleableBooleanProperty(false) {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<NexWizardPane, Boolean> getCssMetaData() {
                    return NexWizardPane.StyleableProperties.AUTOCHANGEACTIVECHILD;
                }

                @Override
                public Object getBean() {
                    return NexWizardPane.this;
                }

                @Override
                public String getName() {
                    return "wrapChildTraversal";
                }
            };
        }
        return wrapChildTraversal;
    }
    private BooleanProperty wrapChildTraversal = new SimpleBooleanProperty(false);

    public final BooleanProperty autoChangeActiveChildProperty() {
        if (autoChangeActiveChild == null) {
            autoChangeActiveChild = new StyleableBooleanProperty(false) {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<NexWizardPane, Boolean> getCssMetaData() {
                    return NexWizardPane.StyleableProperties.AUTOCHANGEACTIVECHILD;
                }

                @Override
                public Object getBean() {
                    return NexWizardPane.this;
                }

                @Override
                public String getName() {
                    return "autoChangeActiveChild";
                }
            };
        }
        return autoChangeActiveChild;
    }
    private BooleanProperty autoChangeActiveChild = new SimpleBooleanProperty(false);

    private final ReadOnlyIntegerWrapper activeIndexProperty = new ReadOnlyIntegerWrapper(-1);
    private final ReadOnlyObjectWrapper<Node> activeChildProperty = new ReadOnlyObjectWrapper<>(null);
    private final ReadOnlyBooleanProperty isActiveChildProperty = new ReadOnlyBooleanPropertyBase() {
        /**
         * Returns the {@code Object} that contains this property. If this property
         * is not contained in an {@code Object}, {@code null} is returned.
         *
         * @return the containing {@code Object} or {@code null}
         */
        @Override
        public Object getBean() {
            return NexWizardPane.this;
        }

        /**
         * Returns the name of this property. If the property does not have a name,
         * this method returns an empty {@code String}.
         *
         * @return the name or an empty {@code String}
         */
        @Override
        public String getName() {
            return "isActiveChild";
        }

        @Override
        public boolean get() {
            return (activeIndexProperty.get() > -1 && activeChildProperty.isNotNull().get());
        }

        /**
         * Get the wrapped value.
         *
         * @return The current value
         */
        @Override
        public Boolean getValue() {
            return get();
        }
    };

    /*================================================================================================================*\
    || Constructors
    \*================================================================================================================*/

    public NexWizardPane() {
        super();
    }

    /*================================================================================================================*\
    || Methods
    \*================================================================================================================*/

    public boolean previousActiveChild() {
        final int prevIndex = activeIndexProperty.get();
        final int index = prevIndex <= 0 ? -1 : prevIndex-1;

        if (index != -1) {
            final Node child = children.get(index);
            children.setWizard(child, index);
            return true;
        } else if (wrapChildTraversalProperty().get()) {
            final Node child = children.get(children.size()-1);
            children.setWizard(child, children.size()-1);
            return true;
        } else {
            return false;
        }
    }

    public boolean nextActiveChild() {
        final int prevIndex = activeIndexProperty.get();
        final int index = prevIndex >= children.size()-1 ? -1 : prevIndex+1;

        if (index != -1) {
            final Node child = children.get(index);
            children.setWizard(child, index);
            return true;
        } else if (wrapChildTraversalProperty().get()) {
            final Node child = children.get(0);
            children.setWizard(child, 0);
            return true;
        } else {
            return false;
        }
    }

    //Encapsulating method (for when auto activate child if false)
    public void setActiveChildIndex(int index) {
        children.setWizard(children.get(index), index);
    }

    /*================================================================================================================*\
    || Getters & Setters (For Fields)
    \*================================================================================================================*/

    public ReadOnlyIntegerProperty getActiveIndexProperty() {
        return activeIndexProperty.getReadOnlyProperty();
    }
    public int getActiveIndex() { return getActiveIndexProperty().get(); }

    public ReadOnlyObjectProperty<Node> getActiveChildProperty() {
        return activeChildProperty.getReadOnlyProperty();
    }
    public Node getActiveChild() { return getActiveChildProperty().get(); }

    public ReadOnlyBooleanProperty isChildActiveProperty() {
        return isActiveChildProperty;
    }

    public final void setWrapChildTraversal(boolean value) { wrapChildTraversalProperty().set(value); }
    public final boolean isWrapChildTraversal() { return wrapChildTraversal == null || wrapChildTraversal.get(); }

    public final void setAutoChangeActiveChild(boolean value) { autoChangeActiveChildProperty().set(value); }
    public final boolean isAutoChangeActiveChild() { return autoChangeActiveChild == null || autoChangeActiveChild.get(); }


    @Override
    public ObservableList<Node> getChildren() {
        return children;
    }

    /*================================================================================================================*\
    || Inner Classes
    \*================================================================================================================*/

    private class ObservableWizardList extends SimpleListProperty<Node> {
        private Node activeChild;

        public ObservableWizardList() {
            super(FXCollections.observableArrayList());
        }

        private void emptyWizard() {
            NexWizardPane.super.getChildren().clear();
        }

        private void setWizard(Node node, int index) {
            try {
                NexWizardPane.super.getChildren().set(0, node);
            } catch (IndexOutOfBoundsException ignored) {
                NexWizardPane.super.getChildren().add(node);
            } finally {
                activeChild = node;
                activeIndexProperty.set(index);
            }
        }

        private void setWizardInternal(Node node, int index) {
            if (autoChangeActiveChildProperty().get()) {
                setWizard(node, index);
            }
        }

        @Override
        public boolean add(Node element) {
            boolean rtn = super.add(element);
            setWizardInternal(element, indexOf(element));
            return rtn;
        }

        @Override
        public void add(int i, Node element) {
            super.add(i, element);
            setWizardInternal(element, indexOf(element));
        }

        @Override
        public boolean addAll(Node... elements) {
            boolean rtn = super.addAll(elements);
            Node element = elements[elements.length - 1];
            setWizardInternal(element, indexOf(element));
            return rtn;
        }

        @Override
        public boolean addAll(Collection<? extends Node> collection) {
            boolean rtn = super.addAll(collection);

            final Iterator<? extends Node> itr = collection.iterator();
            Node element = itr.next();
            while (itr.hasNext()) {
                element = itr.next();
            }

            setWizardInternal(element, indexOf(element));
            return rtn;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Node> collection) {
            boolean rtn = super.addAll(i, collection);

            final Iterator<? extends Node> itr = collection.iterator();
            Node element = itr.next();
            while (itr.hasNext()) {
                element = itr.next();
            }

            setWizardInternal(element, indexOf(element));
            return rtn;
        }

        @Override
        public void clear() {
            super.clear();
            NexWizardPane.super.getChildren().clear();
        }

        @Override
        public boolean remove(Object element) {
            if (activeChild.equals(element)) {
                emptyWizard();
            }
            return super.remove(element);
        }

        @Override
        public Node remove(int i) {
            if (activeIndexProperty.get() == i) {
                emptyWizard();
            }
            return super.remove(i);
        }

        @Override
        public void remove(int from, int to) {
            if (activeIndexProperty.get() >= from && activeIndexProperty.get() <= to) {
                emptyWizard();
            }
            super.remove(from, to);
        }

        @Override
        public boolean removeAll(Node... elements) {
            if (Arrays.stream(elements).anyMatch(s -> s.equals(activeChild))) {
                emptyWizard();
            }
            return super.removeAll(elements);
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            if (collection.contains(activeChild)) {
                emptyWizard();
            }
            return super.remove(collection);
        }

        @Override
        public Node set(int i, Node element) {
            if (element.equals(activeChild)) {
                emptyWizard();
                setWizard(element, i);
            } else {
                setWizardInternal(element, i);
            }

            return super.set(i, element);
        }

        @Override
        public boolean setAll(Node... elements) {
            emptyWizard();
            boolean rtn = super.setAll(elements);
            Node element = elements[elements.length - 1];
            setWizardInternal(element, elements.length - 1);
            return rtn;
        }

        @Override
        public boolean setAll(Collection<? extends Node> collection) {
            emptyWizard();
            boolean rtn = super.setAll(collection);
            final Iterator<? extends Node> itr = collection.iterator();

            Node element = itr.next();
            while (itr.hasNext()) {
                element = itr.next();
            }

            setWizardInternal(element, collection.size());
            return rtn;
        }
    }

    /*================================================================================================================*\
    || CSS
    \*================================================================================================================*/

    /**
     * Super-lazy instantiation pattern from Bill Pugh. (Thanks!)
     */
    private static class StyleableProperties {


        private static final CssMetaData<NexWizardPane,Boolean> AUTOCHANGEACTIVECHILD =
                new CssMetaData<>("-fx-auto-change-active-child",
                        BooleanConverter.getInstance(), Boolean.FALSE) {

                    @Override
                    public boolean isSettable(NexWizardPane node) {
                        return node.autoChangeActiveChild == null ||
                                !node.autoChangeActiveChild.isBound();
                    }

                    @Override
                    public StyleableBooleanProperty getStyleableProperty(NexWizardPane node) {
                        return (StyleableBooleanProperty)node.autoChangeActiveChildProperty();
                    }

                };

        private static final CssMetaData<NexWizardPane,Boolean> WRAPCHILDTRAVERSAL =
                new CssMetaData<>("-fx-wrap-child-traversal",
                        BooleanConverter.getInstance(), Boolean.FALSE) {

                    @Override
                    public boolean isSettable(NexWizardPane node) {
                        return node.wrapChildTraversal == null ||
                                !node.wrapChildTraversal.isBound();
                    }

                    @Override
                    public StyleableBooleanProperty getStyleableProperty(NexWizardPane node) {
                        return (StyleableBooleanProperty)node.autoChangeActiveChildProperty();
                    }

                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<>(Pane.getClassCssMetaData());
            styleables.add(AUTOCHANGEACTIVECHILD);
            styleables.add(WRAPCHILDTRAVERSAL);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return NexWizardPane.StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}

