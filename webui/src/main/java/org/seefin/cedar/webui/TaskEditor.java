package org.seefin.cedar.webui;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * @author phillipsr
 */
public class TaskEditor {
    public static final String DELETE_ID = "cedar.task.editor.delete";
    public static final String UPDATE_ID = "cedar.task.editor.update";
    public static final String DESCRIPTION_ID = "cedar.task.editor.description";

    private final FormLayout editorLayout = new FormLayout();
    private final FieldGroup editorFields = new FieldGroup();
    private final Button deleteTaskButton;
    private final Button updateTaskButton;

    /**
     * Instantiate a task editor to work with the parent
     * task board
     *
     * @param parent of this editor
     */
    TaskEditor(final TaskBoard parent) {
        editorLayout.setMargin(true);
        editorLayout.setVisible(false);
        deleteTaskButton = new Button(parent.getResourceLabel(TaskBoard.DELETE_TASK_BUTTON_LABEL));
        deleteTaskButton.setId(DELETE_ID);
        updateTaskButton = new Button(parent.getResourceLabel(TaskBoard.UPDATE_BUTTON_LABEL));
        updateTaskButton.setId(UPDATE_ID);
        initEditor(parent);
    }

    /**
     * Set-up the editor field, using the loaded resource bundle in the parent
     */
    private void
    initEditor(final TaskBoard parent) {
        addButtonListeners(parent);

        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.addComponent(deleteTaskButton);
        controlPanel.addComponent(updateTaskButton);
        editorLayout.addComponent(controlPanel);

        String descriptionLabel = parent.getResourceLabel(TaskBoard.DESC);
        TextArea field = new TextArea(descriptionLabel);
        field.setId(DESCRIPTION_ID);
        editorLayout.addComponent(field);
        field.setMaxLength(2000); // TODO: temp fix for Bug #1
        field.setWidth("100%");
        field.setHeight("100%");
        // use a FieldGroup to connect multiple components to a data source at once
        editorFields.bind(field, descriptionLabel);

        // write changes automatically without calling commit()
        editorFields.setBuffered(false);
    }

    private void
    addButtonListeners(final TaskBoard parent) {
        deleteTaskButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                parent.deleteCurrentTask();
            }
        });

        updateTaskButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                parent.updateCurrentTask();
            }
        });
    }

    /**
     * @return this editor component
     */
    public Component getLayout() {
        return editorLayout;
    }

    /**
     * When a task is selected from the task list, show in editor on
     * the right This done by the FieldGroup that binds all the fields
     * to the corresponding Properties of the task at once
     *
     * @param item
     */
    public void setItemDataSource(Item item) {
        editorFields.setItemDataSource(item);
        editorLayout.setVisible(item != null);
    }

}
