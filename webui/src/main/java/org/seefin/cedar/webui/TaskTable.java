package org.seefin.cedar.webui;

import com.vaadin.data.Property;
import com.vaadin.ui.Table;
import org.seefin.cedar.model.TaskId;
import org.seefin.cedar.model.tasks.Task.TaskState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Specialized table, for displaying Tasks
 *
 * @author phillipsr
 */
public class TaskTable
        extends Table {
    private final String descriptionLabel;
    private final String descriptionCaption;
    private final String createdLabel;
    private final String statusLabel;
    private final DateTimeFormatter dataTimeFormatter;
    private final TaskContainer taskContainer;

    TaskTable(final TaskBoard parent, final TaskContainer taskContainer) {
        super();
        this.taskContainer = taskContainer;
        descriptionCaption = parent.getResourceLabel(TaskBoard.ENTER_TASK_DESCRIPTION);
        descriptionLabel = parent.getResourceLabel(TaskBoard.DESC);
        createdLabel = parent.getResourceLabel(TaskBoard.CREATED);
        statusLabel = parent.getResourceLabel(TaskBoard.STATUS);
        dataTimeFormatter = DateTimeFormatter.ofPattern(parent.getDateTimePattern());

        this.setContainerDataSource(parent.getTaskContainer());
        this.setVisibleColumns(parent.getVisibleColumns());
        this.setSelectable(true);
        this.setMultiSelect(false);
        this.setImmediate(true);

        this.setColumnExpandRatio(descriptionLabel, 1);
		
		/*
		this.addGeneratedColumn(statusLabel, new Table.ColumnGenerator() {
			@Override
			public Component generateCell ( Table source, Object itemId, Object columnId)
			{
				final Task task = taskContainer.getTaskFromItem ( itemId);

				CheckBox cb = new CheckBox ();
				cb.setImmediate ( true);
				cb.setValue ( task.getState () == TaskState.CHECKED);
				return cb;
			}
		});
		*/

        this.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Object listItemId = TaskTable.super.getValue();
                parent.getEditor().setItemDataSource(TaskTable.super.getItem(listItemId));
            }

        });
    }

    @Override
    protected String formatPropertyValue(
            Object rowId, Object colId, Property<?> property) {
        Object v = property.getValue();
        if (v instanceof LocalDateTime) {
            return dataTimeFormatter.format((LocalDateTime) v);
        }
        return super.formatPropertyValue(rowId, colId, property);
    }

    @SuppressWarnings("unchecked")
    void createNewTask(Object taskId) {
        this.getContainerProperty(taskId, TaskBoard.TASK_ID).setValue((new TaskId()));
        this.getContainerProperty(taskId, descriptionLabel).setValue(descriptionCaption);
        this.getContainerProperty(taskId, createdLabel).setValue(LocalDateTime.now());
        this.getContainerProperty(taskId, statusLabel).setValue(
                taskContainer.buildCheckbox(TaskState.UNCHECKED, taskId));
        // choose the newly created contact to edit it
        this.select(taskId);
    }

}
