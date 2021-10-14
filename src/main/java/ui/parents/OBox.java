package ui.parents;

import javafx.beans.property.*;
import javafx.css.*;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
import javafx.util.Callback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import static javafx.geometry.Orientation.*;

/**
 * <b>OBox</b> is a hybrid version of VBox and HBox which will lay out its children depending on current properties either
 * {@code VERTICAL} or {@code HORIZONTAL}. This can be done automatically (depending on the respective property & the
 * current width to height ratio of the layout area) or programmatically/manually.
 * <br><br>
 * Currently, <b>OBox</b> does support CSS of most properties and is currently also allowing most vertical/horizontal specific
 * properties to be individually changed (i.e. spacing). This sort of behaviour is ideal for situations where a single
 * set of children can require laying out in either orientation at any given moment. While you could get the same result
 * with just transferring children from a VBox to a HBox and vice versa, this isn't ideal and thus <b>OBox</b> now exists.
 */
public class OBox extends Pane {
    /*================================================================================================================*\
    || Lite Fields
    \*================================================================================================================*/

    private boolean biasDirty = true;
    private Orientation bias;
    private static final String MARGIN_CONSTRAINT = "OBox-margin";

    private static final double EPSILON = 1e-14;

    /*================================================================================================================*\
    || Properties
    \*================================================================================================================*/


    /**
     * This property object represents the {@code orientation} of the layout pane. Depending on the value the property
     * reflects will depend on which way the children are laid out within the pane.
     * <br><br>
     * For an orientation value of {@code HORIZONTAL} will set <i>layoutChildren</i> to orientate everything identically
     * to how {@link javafx.scene.layout.HBox} would lay out its children. oppositely, an orientation value of
     * {@code VERTICAL} wll instead layout its children identically to {@link javafx.scene.layout.VBox}.
     * <br><br>
     * This property can be changed automatically depending on the current width to height ratio at layout with the true
     * value set for {@link #autoOrientateProperty}.
     * @return Either {@code HORIZONTAL} or {@code VERTICAL} depending on what has been set and if {@link #autoOrientateProperty}
     * reflects {@code true}.
     */
    public final ObjectProperty<Orientation> orientationProperty() {
        return orientation;
    }

    private final ObjectProperty<Orientation> orientation = new StyleableObjectProperty<>(Orientation.VERTICAL) {
        @Override
        public void invalidated() {
            requestLayout();
        }

        @Override
        public CssMetaData<OBox, Orientation> getCssMetaData() {
            return StyleableProperties.ORIENTATION;
        }

        @Override
        public Object getBean() {
            return OBox.this;
        }

        @Override
        public String getName() {
            return "orientation";
        }
    };
    public final Orientation getOrientation() { return orientationProperty().get(); }
    public final void setOrientation(Orientation orientation) { orientationProperty().set(orientation); }
    public final boolean isOrientationVertical() { return orientationProperty().get() == Orientation.VERTICAL; }
    public final boolean isOrientationHorizontal() { return orientationProperty().get() == Orientation.HORIZONTAL; }

    /**
     * This property will either <i>enable</i> or <i>disable</i> the auto orientation behaviour.
     * <br><br>
     * If this property reflects {@code true} then the auto orientating behaviour is considered <i>enabled</i> and
     * the following will occur:
     *
     * <ul>
     *     <li>Current Width > Current Height - The orientation will be set to {@code HORIZONTAL} when currently {@code VERTICAL}</li>
     *     <li>Current Width < Current Height - The orientation will be set to {@code VERTICAL} when currently {@code HORIZONTAL}</li>
     *     <li>Current Width = Current Height - The orientation will be not changed and instead will be maintained.</li>
     * </ul>
     *
     * Otherwise, when this property reflects {@code false}, the orientation will reflect what has been programmatically/
     * manually set.
     * @return {@code True} for when autoOrientate is <i>enabled</i> for otherwise {@code False}.
     */
    public final BooleanProperty autoOrientateProperty() {
        return autoOrientate;
    }

    private final BooleanProperty autoOrientate = new StyleableBooleanProperty(false) {
        @Override
        public void invalidated() {
            requestLayout();
        }

        @Override
        public CssMetaData<OBox, Boolean> getCssMetaData() {
            return StyleableProperties.AUTOORIENTATE;
        }

        @Override
        public Object getBean() {
            return OBox.this;
        }

        @Override
        public String getName() {
            return "autoOrientate";
        }
    };

    public final boolean isAutoOrientate() { return autoOrientateProperty().get(); }
    public final void setAutoOrientate(boolean bool) { autoOrientateProperty().set(bool); }

    /*================================================================================================================*\
    ||  Constructors
    \*================================================================================================================*/

    /**
     * Creates a {@code OBox} (Orientation Box) layout.
     */
    public OBox() {
        super();
    }

    /**
     * Creates a {@code OBox} (Orientation Box) layout with an initial spacing set.
     * @param spacing The initial spacing value for both VSpacing and HSpacing.
     */
    public OBox(double spacing) {
        this();
        setSpacing(spacing);
    }

    /**
     * Creates a {@code OBox} (Orientation Box) layout with an initial set of children added.
     * @param children The initial children to add.
     */
    public OBox(Node... children) {
        super(children);
    }

    /**
     * Creates a {@code OBox} (Orientation Box) layout with an initial spacing & set of children set/added.
     * @param orientation The initial orientation. Only useful when VERTICAL is not desired.
     */
    public OBox(Orientation orientation) {
        this();
        setOrientation(orientation);
    }

    /**
     * Creates a {@code OBox} (Orientation Box) layout with an initial spacing & set of children set/added.
     * @param spacing The initial spacing value for both VSpacing and HSpacing.
     * @param children The initial children to add.
     */
    public OBox(double spacing, Node... children) {
        this(children);
        setSpacing(spacing);
    }

    /**
     * Creates a {@code OBox} (Orientation Box) layout with an initial spacing & set of children set/added.
     * @param spacing The initial spacing value for both VSpacing and HSpacing.
     * @param orientation The initial orientation. Only useful when VERTICAL is not desired.
     * @param children The initial children to add.
     */
    public OBox(double spacing, Orientation orientation, Node... children) {
        this(children);
        setSpacing(spacing);
        setOrientation(orientation);
    }

    /*================================================================================================================*\
    ||  Methods
    \*================================================================================================================*/

    /**
     * Should check first with autoOrientate is true, then current orientation, then if we need to switch
     * @param width the current width of this node.
     * @param height the current height of this node.
     */
    public void autoOrientateCheck(double width, double height) {
        if (autoOrientateProperty().get()) {
            switch ((int) Math.signum(width - height)) {
                case -1 -> {  if (orientationProperty().get().equals(HORIZONTAL)) orientationProperty().set(VERTICAL); } //>Height
                case 1 -> { if (orientationProperty().get().equals(VERTICAL)) orientationProperty().set(HORIZONTAL); }   //>Width
            }
        }
    }

    @Override protected void layoutChildren() {
        List<Node> managed = getManagedChildren();
        Insets insets = getInsets();
        Pos align = getAlignmentInternal();
        HPos hPos = getAlignmentInternal().getHpos();
        VPos vPos = getAlignmentInternal().getVpos();
        double width = getWidth();
        double height = getHeight();
        double top = snapSpaceY(insets.getTop());
        double left = snapSpaceX(insets.getLeft());
        double bottom = snapSpaceY(insets.getBottom());
        double right = snapSpaceX(insets.getRight());
        final double[][] actualAreaHeights = getAreaHeights(managed, width);
        final double[][] actualAreaWidths = getAreaWidths(managed, height);
        boolean shouldFillHeight = shouldFillHeight();

        autoOrientateCheck(width, height);

        if (orientationProperty().get().equals(HORIZONTAL)) {
            double space = snapSpaceX(getHSpacing());
            double contentWidth = adjustAreaWidths(managed, actualAreaWidths, width, height);
            double contentHeight = height - top - bottom;

            double x = left + computeXOffset(width - left - right, contentWidth, align.getHpos());
            double baselineOffset = -1;
            if (vPos == VPos.BASELINE) {
                baselineOffset = computeBaselineOffset(managed, contentHeight);
            }

            for (int i = 0, size = managed.size(); i < size; i++) {
                Node child = managed.get(i);
                Insets margin = getMargin(child);
                layoutInArea(child, x, top, actualAreaWidths[0][i], contentHeight,
                        baselineOffset, margin, true, shouldFillHeight,
                        hPos, vPos);
                x += actualAreaWidths[0][i] + space;
            }
        } else {
            double space = snapSpaceY(getVSpacing());
            boolean isFillWidth = isFillWidth();

            double contentWidth = width - left - right;
            double contentHeight = adjustAreaHeights(managed, actualAreaHeights, height, width);

            double y = top + computeYOffset(height - top - bottom, contentHeight, vPos);

            for (int i = 0, size = managed.size(); i < size; i++) {
                Node child = managed.get(i);
                layoutInArea(child, left, y, contentWidth, actualAreaHeights[0][i],
                        /* baseline shouldn't matter */actualAreaHeights[0][i],
                        getMargin(child), isFillWidth, true,
                        hPos, vPos);
                y += actualAreaHeights[0][i] + space;
            }
        }
    }

    /*================================================================================================================*\
    ||  Layout Utility Methods (Some Repurposed from Privates)
    \*================================================================================================================*/

    /**
     * Calculates the baseline offset for laying out the managed children based on the content Height given.
     * @param managed Current Managed nodes.
     * @param contentHeight The height of the content area.
     * @return Calculated baseline offset.
     */
    protected double computeBaselineOffset(List<Node> managed, double contentHeight) {
        final double[][] actualAreaWidths = getAreaWidths(managed, getHeight());
        double baselineComplement = getMinBaselineComplement();
        return getAreaBaselineOffset(managed, marginAccessor, i -> actualAreaWidths[0][i],
                contentHeight, baselineComplement);
    }

    /**
     * Convenience method for returning child margins.
     * @param child Child to return its margins.
     * @return The margins of the child given as insets.
     */
    private Insets getMargin(Node child) {
        return (Insets)getConstraint(child);
    }

    /**
     * Convenience method for returning child constraints.
     * @param node Child to return its constraints
     * @return The constraints of the child
     */
    static Object getConstraint(Node node) {
        if (node.hasProperties()) {
            return node.getProperties().get(OBox.MARGIN_CONSTRAINT);
        }
        return null;
    }

    private double getPrefBaselineComplement() {
        if (Double.isNaN(prefBaselineComplement)) {
            if (getAlignmentInternal().getVpos() == VPos.BASELINE) {
                prefBaselineComplement = getPrefBaselineComplement(getManagedChildren());
            } else {
                prefBaselineComplement = -1;
            }
        }
        return prefBaselineComplement;
    }
    private double prefBaselineComplement = Double.NaN;

    private final Callback<Node, Insets> marginAccessor = this::getMargin;

    private double[][] getAreaWidths(List<Node> managed, double height) {
        double[][] tempW = getTempArray(managed.size(), true);
        final double insideHeight = height == -1? -1 : height -
                snapSpaceY(getInsets().getTop()) - snapSpaceY(getInsets().getBottom());
        final boolean shouldFillHeight = shouldFillHeight();
        for (int i = 0, size = managed.size(); i < size; i++) {
            Node child = managed.get(i);
            Insets margin = getMargin(child);
            tempW[0][i] = computeChildPrefAreaWidth(child, getPrefBaselineComplement(), margin, insideHeight, shouldFillHeight);
        }
        return tempW;
    }
    private double[][] getTempArray(int size, boolean width) {
        if (width) {
            if (tempWArray == null) {
                tempWArray = new double[2][size];
            } else if (tempWArray[0].length < size) {
                tempWArray = new double[2][Math.max(tempWArray.length * 3, size)];
            }
            return tempWArray;
        } else {
            if (tempHArray == null) {
                tempHArray = new double[2][size];
            } else if (tempHArray[0].length < size) {
                tempHArray = new double[2][Math.max(tempHArray.length * 3, size)];
            }
            return tempHArray;
        }
    }
    private double[][] tempWArray, tempHArray;
    private double getMinBaselineComplement() {
        if (Double.isNaN(minBaselineComplement)) {
            if (getAlignmentInternal().getVpos() == VPos.BASELINE) {
                minBaselineComplement = getMinBaselineComplement(getManagedChildren());
            } else {
                minBaselineComplement = -1;
            }
        }
        return minBaselineComplement;
    }
    private double minBaselineComplement = Double.NaN;

    double computeChildPrefAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
        final boolean snap = isSnapToPixel();
        double left = margin != null? snapSpaceX(margin.getLeft(), snap) : 0;
        double right = margin != null? snapSpaceX(margin.getRight(), snap) : 0;
        double bc = -1;
        if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) {
            double top = margin != null? snapSpaceY(margin.getTop(), snap) : 0;
            double bottom = margin != null? snapSpaceY(margin.getBottom(), snap) : 0;
            bc = getBaseLineOffsetCompute(child, baselineComplement, height, fillHeight, top, bottom);
        }
        return left + snapSizeX(boundedSize(child.minWidth(bc), child.prefWidth(bc), child.maxWidth(bc))) + right;
    }

    private double getBaseLineOffsetCompute(Node child, double baselineComplement, double height, boolean fillHeight, double top, double bottom) {
        double alt;
        double bo = child.getBaselineOffset();
        final double contentHeight = bo == BASELINE_OFFSET_SAME_AS_HEIGHT && baselineComplement != -1 ?
                height - top - bottom - baselineComplement :
                height - top - bottom;
        if (fillHeight) {
            alt = snapSizeY(boundedSize(
                    child.minHeight(-1), contentHeight,
                    child.maxHeight(-1)));
        } else {
            alt = snapSizeY(boundedSize(
                    child.minHeight(-1),
                    child.prefHeight(-1),
                    Math.min(child.maxHeight(-1), contentHeight)));
        }
        return alt;
    }

    static double boundedSize(double min, double pref, double max) {
        double a = Math.max(pref, min);
        double b = Math.max(min, max);
        return Math.min(a, b);
    }

    private static double _getSnapScaleXimpl(Scene scene) {
        if (scene == null) return 1.0;
        Window window = scene.getWindow();
        if (window == null) return 1.0;
        return window.getRenderScaleX();
    }

    private static double getSnapScaleY(Node n) {
        return _getSnapScaleYimpl(n.getScene());
    }
    private static double _getSnapScaleYimpl(Scene scene) {
        if (scene == null) return 1.0;
        Window window = scene.getWindow();
        if (window == null) return 1.0;
        return window.getRenderScaleY();
    }

    private double getSnapScaleX() {
        return _getSnapScaleXimpl(getScene());
    }

    private double getSnapScaleY() {
        return _getSnapScaleYimpl(getScene());
    }

    private static double scaledRound(double value, double scale) {
        return Math.round(value * scale) / scale;
    }

    private double snapSpaceX(double value, boolean snapToPixel) {
        return snapToPixel ? scaledRound(value, getSnapScaleX()) : value;
    }
    private double snapSpaceY(double value, boolean snapToPixel) {
        return snapToPixel ? scaledRound(value, getSnapScaleY()) : value;
    }

    private static double snapSpace(double value, boolean snapToPixel, double snapScale) {
        return snapToPixel ? scaledRound(value, snapScale) : value;
    }

    double getAreaBaselineOffset(List<Node> children, Callback<Node, Insets> margins,
                                 Function<Integer, Double> positionToWidth,
                                 double areaHeight, double minComplement) {
        return getAreaBaselineOffset(children, margins, positionToWidth, areaHeight, fillHeight.get(), minComplement, isSnapToPixel());
    }

    static double getAreaBaselineOffset(List<Node> children, Callback<Node, Insets> margins,
                                        Function<Integer, Double> positionToWidth,
                                        double areaHeight, final boolean fillHeight, double minComplement, boolean snapToPixel) {
        return getAreaBaselineOffset(children, margins, positionToWidth, areaHeight, t -> fillHeight, minComplement, snapToPixel);
    }

    /**
     * Returns the baseline offset of provided children, with respect to the minimum complement, computed
     * by from the same set of children.
     * @param children the children with baseline alignment
     * @param margins their margins (callback)
     * @param positionToWidth callback for children widths (can return -1 if no bias is used)
     * @param areaHeight height of the area to layout in
     * @param fillHeight callback to specify children that has fillHeight constraint
     * @param minComplement minimum complement
     */
    static double getAreaBaselineOffset(List<Node> children, Callback<Node, Insets> margins,
                                        Function<Integer, Double> positionToWidth,
                                        double areaHeight, Function<Integer, Boolean> fillHeight, double minComplement, boolean snapToPixel) {
        double b = 0;
        double snapScaleV = 0.0;
        for (int i = 0;i < children.size(); ++i) {
            Node n = children.get(i);

            if (snapToPixel && i == 0) snapScaleV = getSnapScaleY(n.getParent());
            Insets margin = margins.call(n);
            double top = margin != null ? snapSpace(margin.getTop(), snapToPixel, snapScaleV) : 0;
            double bottom = (margin != null ? snapSpace(margin.getBottom(), snapToPixel, snapScaleV) : 0);
            final double bo = n.getBaselineOffset();
            if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT) {
                double alt = -1;
                if (n.getContentBias() == Orientation.HORIZONTAL) {
                    alt = positionToWidth.apply(i);
                }
                if (fillHeight.apply(i)) {

                    b = Math.max(b, top + boundedSize(n.minHeight(alt), areaHeight - minComplement - top - bottom,
                            n.maxHeight(alt)));
                } else {

                    b = Math.max(b, top + boundedSize(n.minHeight(alt), n.prefHeight(alt),
                            Math.min(n.maxHeight(alt), areaHeight - minComplement - top - bottom)));
                }
            } else {
                b = Math.max(b, top + bo);
            }
        }
        return b;
    }

    /**
     * Return the minimum complement of baseline
     */
    static double getMinBaselineComplement(List<Node> children) {
        return getBaselineComplement(children, true);
    }

    /**
     * Return the preferred complement of baseline
     */
    static double getPrefBaselineComplement(List<Node> children) {
        return getBaselineComplement(children, false);
    }

    private static double getBaselineComplement(List<Node> children, boolean min) {
        double bc = 0;
        for (Node n : children) {
            final double bo = n.getBaselineOffset();
            if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT) {
                continue;
            }
            if (n.isResizable()) {
                bc = Math.max(bc, (min ? n.minHeight(-1) : n.prefHeight(-1)) - bo);
            } else {
                bc = Math.max(bc, n.getLayoutBounds().getHeight() - bo);
            }
        }
        return bc;
    }


    static double computeXOffset(double width, double contentWidth, HPos hpos) {
        return switch (hpos) {
            case LEFT -> 0;
            case CENTER -> (width - contentWidth) / 2;
            case RIGHT -> width - contentWidth;
        };
    }

    static double computeYOffset(double height, double contentHeight, VPos vpos) {
        return switch (vpos) {
            case BASELINE, TOP -> 0;
            case CENTER -> (height - contentHeight) / 2;
            case BOTTOM -> height - contentHeight;
        };
    }

    /**
     * The amount of horizontally orientated space between each child in the OBox.
     * @return the amount of horizontal space between each child in the OBox
     */
    public final DoubleProperty hSpacingProperty() {
        if (hSpacing == null) {
            hSpacing = new StyleableDoubleProperty() {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<OBox, Number> getCssMetaData () {
                    return OBox.StyleableProperties.HSPACING;
                }

                @Override
                public Object getBean() {
                    return OBox.this;
                }

                @Override
                public String getName() {
                    return "Horizontal Spacing";
                }
            };
        }
        return hSpacing;
    }

    private DoubleProperty hSpacing;
    public final void setHSpacing(double value) { hSpacingProperty().set(value); }
    public final double getHSpacing() { return hSpacing == null || hSpacing.get() < 0 ? 0 : hSpacing.get(); }

    /**
     * The amount of vertically orientated space between each child in the OBox.
     * @return the amount of vertical space between each child in the OBox
     */
    public final DoubleProperty vSpacingProperty() {
        if (vSpacing == null) {
            vSpacing = new StyleableDoubleProperty() {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<OBox, Number> getCssMetaData () {
                    return OBox.StyleableProperties.VSPACING;
                }

                @Override
                public Object getBean() {
                    return OBox.this;
                }

                @Override
                public String getName() {
                    return "Vertical Spacing";
                }
            };
        }
        return vSpacing;
    }

    private DoubleProperty vSpacing;
    public final void setVSpacing(double value) { vSpacingProperty().set(value); }
    public final double getVSpacing() { return vSpacing == null ? 0 : vSpacing.get(); }

    /**
     * Convenience method for setting both Horizontally & Vertically orientated spacing properties.
     * @param value The spacing amount to set for both {@code VSpacing} and {@code HSpacing}.
     */
    public final void setSpacing(double value) { setVSpacing(value); setHSpacing(value);}

    /**
     * Convenience method for getting both {@code VSpacing} (Index 0) and {@code HSpacing} (Index 1).
     * @return A double array of size 2 with {@code VSpacing} in index 0 and {@code HSpacing} in index 1.
     */
    public final double[] getSpacing() { return new double[] {getVSpacing(), getHSpacing()}; }

    /**
     * Convenience method to check if both spacing values are identical.
     * @return {@code True} if spacing values are identical & {@code false} otherwise
     */
    public final boolean isSpacingIdentical() { return getVSpacing() == getHSpacing(); }

    /**
     * The overall alignment of children within the OBox's width and height.
     * @return the overall alignment of children within the OBox's width and
     * height
     */
    public final ObjectProperty<Pos> alignmentProperty() {
        if (alignment == null) {
            alignment = new StyleableObjectProperty<>(Pos.TOP_LEFT) {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<OBox, Pos> getCssMetaData() {
                    return OBox.StyleableProperties.ALIGNMENT;
                }

                @Override
                public Object getBean() {
                    return OBox.this;
                }

                @Override
                public String getName() {
                    return "alignment";
                }
            };
        }
        return alignment;
    }

    private ObjectProperty<Pos> alignment;
    public final void setAlignment(Pos value) { alignmentProperty().set(value); }
    public final Pos getAlignment() { return alignment == null ? Pos.TOP_LEFT : alignment.get(); }
    private Pos getAlignmentInternal() {
        Pos localPos = getAlignment();
        return localPos == null ? Pos.TOP_LEFT : localPos;
    }

    /**
     * Whether resizable children will be resized to fill the full height of the OBox
     * or be resized to their preferred height and aligned according to the <code>alignment</code>
     * vpos value.   Note that if the OBox vertical alignment is set to BASELINE, then this
     * property will be ignored and children will be resized to their preferred heights.
     * @return true if resizable children will be resized to fill the full
     * height of the OBox
     */
    public final BooleanProperty fillHeightProperty() {
        if (fillHeight == null) {
            fillHeight = new StyleableBooleanProperty(true) {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<OBox, Boolean> getCssMetaData() {
                    return OBox.StyleableProperties.FILL_HEIGHT;
                }

                @Override
                public Object getBean() {
                    return OBox.this;
                }

                @Override
                public String getName() {
                    return "fillHeight";
                }
            };
        }
        return fillHeight;
    }

    private BooleanProperty fillHeight = new SimpleBooleanProperty(false);
    public final void setFillHeight(boolean value) { fillHeightProperty().set(value); }
    public final boolean isFillHeight() { return fillHeight == null || fillHeight.get(); }

    private boolean shouldFillHeight() {
        return isFillHeight() && getAlignmentInternal().getVpos() != VPos.BASELINE;
    }


    /**
     * Whether resizable children will be resized to fill the full width of the OBox
     * or be resized to their preferred width and aligned according to the <code>alignment</code>
     * hpos value.   Note that if the OBox vertical alignment is set to BASELINE, then this
     * property will be ignored and children will be resized to their preferred widths.
     * @return true if resizable children will be resized to fill the full
     * width of the OBox
     */
    public final BooleanProperty fillWidthProperty() {
        if (fillWidth == null) {
            fillWidth = new StyleableBooleanProperty(true) {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public CssMetaData<OBox, Boolean> getCssMetaData() {
                    return OBox.StyleableProperties.FILL_WIDTH;
                }

                @Override
                public Object getBean() {
                    return OBox.this;
                }

                @Override
                public String getName() {
                    return "fillWidth";
                }
            };
        }
        return fillWidth;
    }

    private BooleanProperty fillWidth = new SimpleBooleanProperty(true);
    public final void setFillWidth(boolean value) { fillWidthProperty().set(value); }
    public final boolean isFillWidth() { return fillWidth == null || fillWidth.get(); }

    /**
     *
     * @return null unless one of its children has a content bias.
     */
    @Override public Orientation getContentBias() {
        if (biasDirty) {
            bias = null;
            final List<Node> children = getManagedChildren();
            for (Node child : children) {
                Orientation contentBias = child.getContentBias();
                if (contentBias != null) {
                    bias = contentBias;
                    if (contentBias == Orientation.HORIZONTAL) {
                        break;
                    }
                }
            }
            biasDirty = false;
        }
        return bias;
    }

    private double adjustAreaWidths(List<Node>managed, double[][] areaWidths, double width, double height) {
        Insets insets = getInsets();
        double top = snapSpaceY(insets.getTop());
        double bottom = snapSpaceY(insets.getBottom());

        double contentWidth = sum(areaWidths[0], managed.size()) + (managed.size()-1)*snapSpaceX(getHSpacing());
        double extraWidth = width -
                snapSpaceX(insets.getLeft()) - snapSpaceX(insets.getRight()) - contentWidth;

        if (extraWidth != 0) {
            final double refHeight = shouldFillHeight() && height != -1? height - top - bottom : -1;
            double remaining = growOrShrinkAreaWidths(managed, areaWidths, Priority.ALWAYS, extraWidth, refHeight);
            remaining = growOrShrinkAreaWidths(managed, areaWidths, Priority.SOMETIMES, remaining, refHeight);
            contentWidth += (extraWidth - remaining);
        }
        return contentWidth;
    }
    private static double sum(double[] array, int size) {
        int i = 0;
        double res = 0;
        while (i != size) {
            res += array[i++];
        }
        return res;
    }
    private double growOrShrinkAreaWidths(List<Node>managed, double[][] areaWidths, Priority priority, double extraWidth, double height) {
        final boolean shrinking = extraWidth < 0;
        int adjustingNumber = 0;

        double[] usedAxis = areaWidths[0];
        double[] tempW = areaWidths[1];
        final boolean shouldFillHeight = shouldFillHeight();

        if (shrinking) {
            adjustingNumber = managed.size();
            for (int i = 0, size = managed.size(); i < size; i++) {
                final Node child = managed.get(i);
                tempW[i] = computeChildMinAreaWidth(child, getMinBaselineComplement(), getMargin(child), height, shouldFillHeight);
            }
        } else {
            for (int i = 0, size = managed.size(); i < size; i++) {
                final Node child = managed.get(i);
                if (getHGrow(child) == priority) {
                    tempW[i] = computeChildMaxAreaWidth(child, getMinBaselineComplement(), getMargin(child), height, shouldFillHeight);
                    adjustingNumber++;
                } else {
                    tempW[i] = -1;
                }
            }
        }

        return computeParimeterSnapping(true, extraWidth, adjustingNumber, managed.size(), tempW, usedAxis);
    }
    public static Priority getHGrow(Node child) {
        return (Priority)getConstraint(child, "obox-hgrow");
    }
    double computeChildMinAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
        final boolean snap = isSnapToPixel();
        double left = margin != null? snapSpaceX(margin.getLeft(), snap) : 0;
        double right = margin != null? snapSpaceX(margin.getRight(), snap) : 0;
        double bc = -1;
        if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) {
            double top = margin != null? snapSpaceY(margin.getTop(), snap) : 0;
            double bottom = (margin != null? snapSpaceY(margin.getBottom(), snap) : 0);
            bc = getBaseLineOffsetCompute(child, baselineComplement, height, fillHeight, top, bottom);
        }
        return left + snapSizeX(child.minWidth(bc)) + right;
    }
    double computeChildMaxAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
        double max = child.maxWidth(-1);
        if (max == Double.MAX_VALUE) {
            return max;
        }
        final boolean snap = isSnapToPixel();
        double left = margin != null? snapSpaceX(margin.getLeft(), snap) : 0;
        double right = margin != null? snapSpaceX(margin.getRight(), snap) : 0;
        double bc = -1;
        if (height != -1 && child.isResizable() && child.getContentBias() == Orientation.VERTICAL) {
            double top = margin != null? snapSpaceY(margin.getTop(), snap) : 0;
            double bottom = (margin != null? snapSpaceY(margin.getBottom(), snap) : 0);
            bc = getBaseLineOffsetCompute(child, baselineComplement, height, fillHeight, top, bottom);
            max = child.maxWidth(bc);
        }

        return left + snapSizeX(boundedSize(child.minWidth(bc), max, Double.MAX_VALUE)) + right;
    }
    double snapPortionX(double value) {
        return snapPortionX(value, isSnapToPixel());
    }

    private double snapPortionX(double value, boolean snapToPixel) {
        if (!snapToPixel || value == 0) return value;
        return snapper(value, getSnapScaleX());
    }

    private double[][] getAreaHeights(List<Node> managed, double width) {
        double[][] tempH = getTempArray(managed.size(), false);
        final double insideWidth = width == -1? -1 : width -
                snapSpaceX(getInsets().getLeft()) - snapSpaceX(getInsets().getRight());
        final boolean isFillWidth = isFillWidth();
        for (int i = 0, size = managed.size(); i < size; i++) {
            Node child = managed.get(i);
            Insets margin = getMargin(child);
            if (insideWidth != -1 && isFillWidth) {
                tempH[0][i] = computeChildPrefAreaHeight(child, margin, insideWidth);
            } else {
                tempH[0][i] = computeChildPrefAreaHeight(child, margin, -1);
            }
        }
        return tempH;
    }
    private double adjustAreaHeights(List<Node>managed, double[][] areaHeights, double height, double width) {
        Insets insets = getInsets();
        double left = snapSpaceX(insets.getLeft());
        double right = snapSpaceX(insets.getRight());

        double contentHeight = sum(areaHeights[0], managed.size()) + (managed.size()-1)*snapSpaceY(getVSpacing());
        double extraHeight = height -
                snapSpaceY(insets.getTop()) - snapSpaceY(insets.getBottom()) - contentHeight;

        if (extraHeight != 0) {
            final double refWidth = isFillWidth()&& width != -1? width - left - right : -1;
            double remaining = growOrShrinkAreaHeights(managed, areaHeights, Priority.ALWAYS, extraHeight, refWidth);
            remaining = growOrShrinkAreaHeights(managed, areaHeights, Priority.SOMETIMES, remaining, refWidth);
            contentHeight += (extraHeight - remaining);
        }

        return contentHeight;
    }

    double computeChildMinAreaHeight(Node child, Insets margin, double width) {
        final boolean snap = isSnapToPixel();
        double top =margin != null? snapSpaceY(margin.getTop(), snap) : 0;
        double bottom = margin != null? snapSpaceY(margin.getBottom(), snap) : 0;

        double alt = -1;
        if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) {
            double left = margin != null? snapSpaceX(margin.getLeft(), snap) : 0;
            double right = margin != null? snapSpaceX(margin.getRight(), snap) : 0;
            alt = snapSizeX(width != -1? boundedSize(child.minWidth(-1), width - left - right, child.maxWidth(-1)) :
                    child.maxWidth(-1));
        }
        return top + snapSizeY(child.minHeight(alt)) + bottom;
    }
    double computeChildPrefAreaHeight(Node child, Insets margin, double width) {
        final boolean snap = isSnapToPixel();
        double top = margin != null? snapSpaceY(margin.getTop(), snap) : 0;
        double bottom = margin != null? snapSpaceY(margin.getBottom(), snap) : 0;

        double alt = -1;
        if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) {
            double left = margin != null ? snapSpaceX(margin.getLeft(), snap) : 0;
            double right = margin != null ? snapSpaceX(margin.getRight(), snap) : 0;
            alt = snapSizeX(boundedSize(
                    child.minWidth(-1), width != -1 ? width - left - right
                            : child.prefWidth(-1), child.maxWidth(-1)));
        }

        return top + snapSizeY(boundedSize(child.minHeight(alt), child.prefHeight(alt), child.maxHeight(alt))) + bottom;
    }
    private double growOrShrinkAreaHeights(List<Node>managed, double[][] areaHeights, Priority priority, double extraHeight, double width) {
        final boolean shrinking = extraHeight < 0;
        int adjustingNumber = 0;

        double[] usedAxis = areaHeights[0];
        double[] tempH = areaHeights[1];

        if (shrinking) {
            adjustingNumber = managed.size();
            for (int i = 0, size = managed.size(); i < size; i++) {
                final Node child = managed.get(i);
                tempH[i] = computeChildMinAreaHeight(child, getMargin(child), width);
            }
        } else {
            for (int i = 0, size = managed.size(); i < size; i++) {
                final Node child = managed.get(i);
                if (getVgrow(child) == priority) {
                    tempH[i] = computeChildMaxAreaHeight(child, getMargin(child), width);
                    adjustingNumber++;
                } else {
                    tempH[i] = -1;
                }
            }
        }

        return computeParimeterSnapping(false, extraHeight, adjustingNumber, managed.size(), tempH, usedAxis);
    }
    double computeChildMaxAreaHeight(Node child, Insets margin, double width) {
        double max = child.maxHeight(-1);
        if (max == Double.MAX_VALUE) {
            return max;
        }

        final boolean snap = isSnapToPixel();
        double top = margin != null? snapSpaceY(margin.getTop(), snap) : 0;
        double bottom = margin != null? snapSpaceY(margin.getBottom(), snap) : 0;
        double alt = -1;
        if (child.isResizable() && child.getContentBias() == Orientation.HORIZONTAL) {
            double left = margin != null? snapSpaceX(margin.getLeft(), snap) : 0;
            double right = margin != null? snapSpaceX(margin.getRight(), snap) : 0;
            alt = snapSizeX(width != -1? boundedSize(child.minWidth(-1), width - left - right, child.maxWidth(-1)) :
                    child.minWidth(-1));
            max = child.maxHeight(alt);
        }
        return top + snapSizeY(boundedSize(child.minHeight(alt), max, Double.MAX_VALUE)) + bottom;
    }

    public static Priority getVgrow(Node child) {
        return (Priority)getConstraint(child, "obox-vgrow");
    }
    double snapPortionY(double value) {
        return snapPortionY(value, isSnapToPixel());
    }

    private double snapPortionY(double value, boolean snapToPixel) {
        if (!snapToPixel || value == 0) return value;
        return snapper(value, getSnapScaleY());
    }

    private static Object getConstraint(Node node, Object key) {
        if (node.hasProperties()) {
            return node.getProperties().get(key);
        }
        return null;
    }

    private double snapper(double value, double scale) {
        value *= scale;
        if (value > 0) {
            value = Math.max(1, Math.floor(value + EPSILON));
        } else {
            value = Math.min(-1, Math.ceil(value - EPSILON));
        }
        return value / scale;
    }

    private double computeParimeterSnapping(boolean x, double extraParimeter, double adjustingNumber, double managedSize, double[] ChildMinArea, double[] usedAxis) {
        double available = extraParimeter;
        outer:while (Math.abs(available) > 1 && adjustingNumber > 0) {
            final double portion = x ? snapPortionX(available / adjustingNumber) : snapPortionY(available / adjustingNumber);
            for (int i = 0; i < managedSize; i++) {
                if (ChildMinArea[i] == -1) {
                    continue;
                }
                final double limit = ChildMinArea[i] - usedAxis[i];
                final double change = Math.abs(limit) <= Math.abs(portion)? limit : portion;
                usedAxis[i] += change;
                available -= change;
                if (Math.abs(available) < 1) {
                    break outer;
                }
                if (Math.abs(change) < Math.abs(portion)) {
                    ChildMinArea[i] = -1;
                    adjustingNumber--;
                }
            }
        }

        return available;
    }

    /*================================================================================================================*\
    || CSS
    \*================================================================================================================*/

    /**
     * Super-lazy instantiation pattern from Bill Pugh. (Thanks!)
     */
    private static class StyleableProperties {

        private static final CssMetaData<OBox,Pos> ALIGNMENT =
                new CssMetaData<>("-fx-alignment",
                        new EnumConverter<>(Pos.class),
                        Pos.TOP_CENTER) {

                    @Override
                    public boolean isSettable(OBox node) {
                        return node.alignment == null || !node.alignment.isBound();
                    }

                    @Override
                    public StyleableObjectProperty<Pos> getStyleableProperty(OBox node) {
                        return (StyleableObjectProperty<Pos>)node.alignmentProperty();
                    }

                };

        private static final CssMetaData<OBox,Orientation> ORIENTATION =
                new CssMetaData<>("-fx-orientation",
                        new EnumConverter<>(Orientation.class),
                        Orientation.VERTICAL) {

                    @Override
                    public boolean isSettable(OBox node) {
                        return node.orientationProperty() == null || !node.orientationProperty().isBound();
                    }

                    @Override
                    public StyleableObjectProperty<Orientation> getStyleableProperty(OBox node) {
                        return (StyleableObjectProperty<Orientation>)node.orientationProperty();
                    }
                };

        private static final CssMetaData<OBox,Boolean> AUTOORIENTATE =
                new CssMetaData<>("-fx-auto-orientate",
                        BooleanConverter.getInstance(), Boolean.FALSE) {

                    @Override
                    public boolean isSettable(OBox node) {
                        return !node.autoOrientate.isBound();
                    }

                    @Override
                    public StyleableBooleanProperty getStyleableProperty(OBox node) {
                        return (StyleableBooleanProperty)node.autoOrientateProperty();
                    }

                };

        private static final CssMetaData<OBox,Boolean> FILL_HEIGHT =
                new CssMetaData<>("-fx-fill-height",
                        BooleanConverter.getInstance(), Boolean.TRUE) {

                    @Override
                    public boolean isSettable(OBox node) {
                        return node.fillHeight == null ||
                                !node.fillHeight.isBound();
                    }

                    @Override
                    public StyleableBooleanProperty getStyleableProperty(OBox node) {
                        return (StyleableBooleanProperty)node.fillHeightProperty();
                    }

                };

        private static final CssMetaData<OBox,Boolean> FILL_WIDTH =
                new CssMetaData<>("-fx-fill-width",
                        BooleanConverter.getInstance(), Boolean.TRUE) {

                    @Override
                    public boolean isSettable(OBox node) {
                        return node.fillWidth == null ||
                                !node.fillWidth.isBound();
                    }

                    @Override
                    public StyleableBooleanProperty getStyleableProperty(OBox node) {
                        return (StyleableBooleanProperty)node.fillWidthProperty();
                    }

                };

        private static final CssMetaData<OBox,Number> HSPACING =
                new CssMetaData<>("-fx-spacing-horizontal",
                        SizeConverter.getInstance(), 0.0){

                    @Override
                    public boolean isSettable(OBox node) {
                        return node.hSpacing == null || !node.hSpacing.isBound();
                    }

                    @Override
                    public StyleableDoubleProperty getStyleableProperty(OBox node) {
                        return (StyleableDoubleProperty)node.hSpacingProperty();
                    }

                };

        private static final CssMetaData<OBox,Number> VSPACING =
                new CssMetaData<>("-fx-spacing-vertical",
                        SizeConverter.getInstance(), 0.0){

                    @Override
                    public boolean isSettable(OBox node) {
                        return node.vSpacing == null || !node.vSpacing.isBound();
                    }

                    @Override
                    public StyleableDoubleProperty getStyleableProperty(OBox node) {
                        return (StyleableDoubleProperty)node.vSpacingProperty();
                    }

                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<>(Pane.getClassCssMetaData());
            styleables.add(ALIGNMENT);
            styleables.add(ORIENTATION);
            styleables.add(AUTOORIENTATE);
            styleables.add(FILL_HEIGHT);
            styleables.add(FILL_WIDTH);
            styleables.add(HSPACING);
            styleables.add(VSPACING);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return OBox.StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}