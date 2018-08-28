package org.seefin.cedar.model.tasks;

import org.seefin.cedar.model.PartyId;
import org.seefin.cedar.model.TaskId;
import org.seefin.cedar.model.parties.Individual;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A task represents a 'to-do' item, and consists of a text description (what is to be done),
 * a creation date/time, an owner (who is to do it) and a state (is it done/not done/removed)
 * <p>
 * What do we know about a Task?
 * - User can check/uncheck any task on their list
 * - User can add/remove task
 * - Each �task� contains a text description and date when the entry was created.
 *
 * @author phillipsr
 */
public final class Task
        implements Serializable {
    public enum TaskState {
        UNCHECKED, CHECKED, DELETED
    }

    private final TaskId id;
    private final String description;
    private final LocalDateTime createTime;
    private final Individual owner;
    private final TaskState state;

    /**
     * Create a new task with the supplied text, for the owner
     *
     * @param owner       of the task
     * @param description of the task
     * @throws IllegalArgumentException if the supplied description is
     *                                  null or an empty string
     */
    public Task(Individual owner, String description) {
        this(new TaskId(), TaskState.UNCHECKED, owner, description, LocalDateTime.now());
    }

    /**
     * Instantiate a pre-existing task from its values
     *
     * @param id          of the tasks
     * @param state       of the task
     * @param owner       of the task
     * @param description text for the task
     * @param created     date and time task was created
     */
    public Task(TaskId id, TaskState state, Individual owner, String description, LocalDateTime created) {
        if (id == null) {
            throw new IllegalArgumentException("task id cannot be null");
        }
        if (state == null) {
            throw new IllegalArgumentException("task state cannot be null");
        }
        if (owner == null) {
            throw new IllegalArgumentException("task owner cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("description cannot be null");
        }
        if (created == null) {
            throw new IllegalArgumentException("created time cannot be null");
        }
        this.owner = owner;
        this.description = description;
        this.createTime = created;
        this.id = id;
        this.state = state;
    }

    /**
     * @return this task's unique Id
     */
    public TaskId getTaskId() {
        return id;
    }

    /**
     * @return this tasks text description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the date and time this task was created
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * @return the task's owner
     */
    public Individual getOwner() {
        return owner;
    }

    /**
     * @return the current state of this task
     */
    public TaskState getState() {
        return state;
    }

    /**
     * Change the state of the task to <code>CHECKED</code>
     *
     * @return a copy of this task with the state changed
     * @throws IllegalArgumentException if this task is <code>DELETED</code>
     */
    public Task check() {
        if (state == TaskState.DELETED) {
            throw new IllegalArgumentException("Cannot check a deleted task");
        }
        return new Task(id, TaskState.CHECKED, owner, description, createTime);
    }

    /**
     * Change the state of the task to <code>UNCHECKED</code>
     *
     * @return a copy of this task with the state changed
     * @throws IllegalArgumentException if this task is <code>DELETED</code>
     */
    public Task uncheck() {
        if (state == TaskState.DELETED) {
            throw new IllegalArgumentException("Cannot check a deleted task");
        }
        return new Task(id, TaskState.UNCHECKED, owner, description, createTime);
    }

    /**
     * Change the state of the task to <code>DELETED</code><b/>
     * Note: this method is idempotent on an already-deleted task
     *
     * @return a copy of this task with the state changed
     */
    public Task delete() {
        return new Task(id, TaskState.DELETED, owner, description, createTime);
    }

    /**
     * Provides useful info for logging purposes<p/>
     * {@inheritDoc}
     */
    @Override
    public String
    toString() {
        return "Task(id=" + id + ",description=" + description
                + ",created=" + createTime + ",owner=" + owner + ",state=" + state + ")";
    }

    /**
     * @return the Id of this task's owner
     */
    public PartyId getOwnerId() {
        return owner.getId();
    }

}
