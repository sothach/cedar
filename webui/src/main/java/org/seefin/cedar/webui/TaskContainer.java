package org.seefin.cedar.webui;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CheckBox;
import org.seefin.cedar.model.TaskId;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.tasks.Task;
import org.seefin.cedar.model.tasks.Task.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author phillipsr
 */
class TaskContainer
        extends IndexedContainer {
    private static final Logger log = LoggerFactory.getLogger(TaskContainer.class);

    private Individual owner;
    private final String descriptionLabel;
    private final String createdLabel;
    private final String statusLabel;
    private final TaskBoard parent;

    TaskContainer(final TaskBoard parent) {
        super();
        this.parent = parent;
        descriptionLabel = parent.getResourceLabel(TaskBoard.getDescriptionLabel());
        createdLabel = parent.getResourceLabel(TaskBoard.getCreated());
        statusLabel = parent.getResourceLabel(TaskBoard.getUStatus());
    }

    protected void loadTasks(final String username) {
        this.addContainerProperty(TaskBoard.TASK_ID, TaskId.class, null);
        this.addContainerProperty(descriptionLabel, String.class, "");
        this.addContainerProperty(createdLabel, LocalDateTime.class, null);
        this.addContainerProperty(statusLabel, CheckBox.class, false);

        final Optional<Individual> user = parent.getPartyService().findPartyByUsername(username);
        if (!user.isPresent()) {
            // TODO: display error in view?
            log.debug("current user not found (username=" + username + ")");
            return;
        }
        owner = user.get();
        final List<Task> userTasks = parent.getTaskService().findTasksForUser(owner.getId());
        for (Task task : userTasks) {
            Object id = this.addItem();
            setTaskProperties(task, id);
        }
        log.debug("Loaded #{} tasks for UserId={}", userTasks.size(), owner.getId());
    }

    /**
     * Sets the properties of a given task into the table model
     *
     * @param task to be added
     * @param id   of the table row
     */
    @SuppressWarnings("unchecked")
    private void setTaskProperties(final Task task, final Object id) {
        this.getContainerProperty(id, TaskBoard.TASK_ID).setValue(task.getTaskId());
        this.getContainerProperty(id, descriptionLabel).setValue(task.getDescription());
        this.getContainerProperty(id, createdLabel).setValue(task.getCreateTime());
        this.getContainerProperty(id, statusLabel).setValue(buildCheckbox(task.getState(), id));
    }

    /**
     * @param state task-state to initialize check-box to
     * @param id    of the table row for this task
     * @return a new check-box, representing the task's state, and add a listener
     * to it, to update the task on change
     */
    @SuppressWarnings("deprecation")
    protected CheckBox buildCheckbox(final TaskState state, final Object id) {
        // TODO: this is ugly: is there a better way to add the check-box and its listener?
        final CheckBox result = new CheckBox();
        result.setImmediate(true); // ensure changes reported to server immediately
        result.setValue(state == TaskState.CHECKED);

        result.addListener((Property.ValueChangeListener) event -> parent.updateTask(id));
        return result;
    }

    /**
     * Instantiate a task from the properties of the supplied table item
     *
     * @param listItemId
     * @return a new task object, or null if currentItem does not have valid properties
     */
    protected Task getTaskFromItem(final Object listItemId) {
        final Item currentItem = this.getItem(listItemId);
        @SuppressWarnings("rawtypes")
        final Property value = currentItem.getItemProperty(TaskBoard.TASK_ID);
        if (value == null) {
            log.debug("current task does not (yet) have a value");
            return null;
        }
        final TaskId id = (TaskId) currentItem.getItemProperty(TaskBoard.TASK_ID).getValue();
        if (id == null) {
            log.debug("current task does not (yet) have an Id");
            return null;
        }
        final String description = (String) currentItem.getItemProperty(descriptionLabel).getValue();
        final LocalDateTime createTime = (LocalDateTime) currentItem.getItemProperty(createdLabel).getValue();
        final CheckBox cb = (CheckBox) currentItem.getItemProperty(statusLabel).getValue();
        final TaskState state = cb.getValue() ? TaskState.CHECKED : TaskState.UNCHECKED;

        return new Task(id, state, owner, description, createTime);
    }

    /**
     * Reset the filter for the task container
     *
     * @param searchTerm to filter by
     */
    protected void resetFilter(final String searchTerm) {
        /* Reset the filter for the contactContainer. */
        this.removeAllContainerFilters();
        this.addContainerFilter(
                new TaskFilter(searchTerm));
    }

    /*
     * A custom filter for searching names and companies in the
     * contactContainer.
     */
    private class TaskFilter implements Filter {
        private final String searchTerm;

        TaskFilter(final String term) {
            this.searchTerm = term.toLowerCase();
        }

        @Override
        public boolean
        passesFilter(final Object itemId, final Item item) {
            final String description = (String) item.getItemProperty(descriptionLabel).getValue();
            return description.toLowerCase().contains(searchTerm);
        }

        @Override
        public boolean appliesToProperty(Object id) {
            return true;
        }
    }

    /**
     * @return
     */
    protected Object addNewRow() {
        this.removeAllContainerFilters();
        return this.addItemAt(0);
    }

}
