package org.seefin.cedar.model;

/**
 * Type representing the unique identity of a task
 *
 * @author phillipsr
 */
public final class TaskId extends GUID {
    // this is effectively a type alias for GUID

    /**
     * Instantiate a new TaskId from a random value<b/>
     * see {@link org.seefin.cedar.model.GUID}
     */
    public TaskId() {
        super();
    }

    /**
     * Instantiate a TaskId from the supplied external format value<b/>
     * see {@link org.seefin.cedar.model.GUID}
     *
     * @param stringForm representing the TaskId
     */
    public TaskId(String stringForm) {
        super(stringForm);
    }
}
