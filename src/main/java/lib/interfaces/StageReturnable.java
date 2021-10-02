package lib.interfaces;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.stage.Stage;

/**
 * This interface is designed to guarantee that the implementor can receive a request to start (work) & then return from
 * it if completed or requested. Once completed it will return data if required to using returnData().
 *
 * The implementor should internally call returnToCaller() when actually returning but this method should also be able
 * to accommodate external calls to force the implementation to return with or without data. If done this way, data
 * shall reasonably be expected to be null/invalid in normal behavior. returnToCaller() ideally shall not be used
 * externally however.
 *
 * The returnProperty shall reflect if the implementor has signaled or called for a return with being started again.
 * It is intended to be used by a listener from the caller for listening and responding to return signals
 * programmatically. However, in typical JavaFX application it is safe to ignore as long as returnToCaller() is hiding
 * it's implementor & showing its caller as expected.
 * @param <R>
 */
public interface StageReturnable<R> {

    /**
     * Starts (or rather shows) the implementation stage.
     * @param caller The calling stage (or return to stage). This stage will be shown after completion calling return
     *               returnToCaller() either internally or externally.
     * @return The state property of whether the stage has had returnToCaller called. True for return.
     */
    ReadOnlyBooleanProperty returnableStart(Stage caller);

    /**
     * Closes the implementor and signals return as true. Afterwards the calling stage is shown.
     * @return Any data associated with the completion of the implementation.
     */
    R returnToCaller();

    /**
     * Gets current stage set as caller to return to for reference. Cannot be set unless calling returnableStart().
     * @return The current stage set as caller.
     */
    Stage getCurrentCaller();

    /**
     * Gets the current data set for return. This data can be fetched early but should be expected to be failed if the
     * returnProperty is true.
     * @return Any data currently set for returning to caller.
     */
    R returnData();

    /**
     * The read only return property which reflects the state in which the implementor is in regarding returning to
     * caller.
     *
     * If the return property reflects true then a call to returnToCaller has already been made. Otherwise the has been
     * no start or returnableStart has been called since a return.
     * @return The read only returnProperty object.
     */
    ReadOnlyBooleanProperty returnProperty();

    /**
     * The current value of the returnProperty in primative form.
     * @return A boolean true for it has returned and false otherwise (started again or never started).
     */
    default boolean isReturned() { return returnProperty().get();}
}
