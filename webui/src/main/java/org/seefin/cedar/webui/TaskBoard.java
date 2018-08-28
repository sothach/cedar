package org.seefin.cedar.webui;

import com.vaadin.annotations.Title;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.tasks.Task;
import org.seefin.cedar.service.PartyService;
import org.seefin.cedar.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This is the main window layout of the task board app, displayed after a successful login; it renders
 * the sub-components (list and editor panels) based on the details of the current user
 */
@Title("Cedar Task Board")
@Configurable(preConstruction = true)
public class TaskBoard
        extends CustomComponent implements View {
    public static final String LOGOUT_ID = "cedar.taskboard.logout";
    public static final String NEW_TASK_ID = "cedar.taskboard.newTask";
    public static final String SEARCH_ID = "cedar.taskboard.search";

    static final String CEDAR_TASKBOARD_TITLE = "cedar.ui.taskboard.title";
    static final String UPDATE_BUTTON_LABEL = "cedar.ui.taskboard.button.label.update";
    static final String DELETE_TASK_BUTTON_LABEL = "cedar.ui.taskboard.button.label.delete";
    static final String LOGOUT_BUTTON_LABEL = "cedar.ui.taskboard.button.label.logout";
    static final String NEW_TASK_BUTTON_LABEL = "cedar.ui.taskboard.button.label.newTask";
    static final String SEARCH_TASKS_PROMPT = "cedar.ui.taskboard.search.prompt";
    static final String ENTER_TASK_DESCRIPTION = "cedar.ui.taskboard.edit.caption";
    static final String DESC = "cedar.ui.taskboard.edit.description";
    static final String CREATED = "cedar.ui.taskboard.edit.created";
    static final String STATUS = "cedar.ui.taskboard.edit.status";
    private static final String ResourceBundleName = "org.seefin.cedar.webui.i18n.CedarWebUI";

    private static final Logger log = LoggerFactory.getLogger(TaskBoard.class);

    public static final String NAME = "TaskBoard"; // for navigation purposes

    /* user interface components are stored in session */
    private TextField searchField = new TextField();
    private Button newTaskButton;
    private Button logoutButton;
    private TaskEditor editor;
    private TaskTable taskPanel;
    private ResourceBundle labels;

    static final String TASK_ID = "TaskId"; // (hidden field)

    private String DescriptionLabel;
    private String CreatedLabel;
    private String StatusLabel;
    private String[] visibleColumns;
    private String DateTimePattern;

    private TaskContainer taskContainer;

    private PartyService partyService;
    private TaskService taskService;
    private String AppTitle;

    public TaskBoard() {
    }

    /**
     * {@inheritDoc}
     * <p/>Use the logged-in user's locale settings to initialize the interface
     * element appropriately, ex-ject the services out of the Spring context
     */
    @Override
    public void enter(ViewChangeEvent event) {
        loadServices();

        // get logged-in user details and initialize view accordingly
        final Individual currentUser = getUser(String.valueOf(getSession().getAttribute("user")));

        labels = loadResources(currentUser);

        setSizeFull();

        taskContainer = new TaskContainer(this);
        taskContainer.loadTasks(String.valueOf(getSession().getAttribute("user")));
        initButtons(currentUser); // create buttons + listeners (do this before layout of main window)

        editor = new TaskEditor(this);
        taskPanel = new TaskTable(this, taskContainer);
        Component layout = createPagelayout();
        setCompositionRoot(layout);

        initSearch();
    }

    /**
     * load the resources appropriate to the current users settings,
     * and initialize text string
     *
     * @param user whose locale preference to use
     * @return the resource bundle
     */
    private ResourceBundle
    loadResources(Individual user) {
        ResourceBundle result = ResourceBundle.getBundle(
                ResourceBundleName, new Locale(user.getLocale()));
        AppTitle = result.getString(CEDAR_TASKBOARD_TITLE);
        DescriptionLabel = result.getString(DESC);
        CreatedLabel = result.getString(CREATED);
        StatusLabel = result.getString(STATUS);
        visibleColumns = new String[]{DescriptionLabel, CreatedLabel, StatusLabel};
        DateTimePattern = "MS"; // DateTimeFormatter.patternForStyle("MS", new Locale(user.getLocale()));

        return result;
    }

    /**
     * @return the user-locale configured date/time formatting pattern
     */
    String getDateTimePattern() {
        return DateTimePattern;
    }

    /**
     * pull the required services in from the Spring context
     */
    private void loadServices() {
        SpringContextHelper springHelper
                = new SpringContextHelper(
                VaadinServlet.getCurrent().getServletContext());
        partyService = (PartyService) springHelper.getBean("partyService");
        taskService = (TaskService) springHelper.getBean("taskService");
    }

    /**
     * Create the widgets needed in this view and assemble into
     * the main layout, setting sizes and styles as required
     *
     * @return the main window
     */
    private Component
    createPagelayout() {
        VerticalSplitPanel mainWindow = new VerticalSplitPanel();
        mainWindow.setSplitPosition(52, Unit.PIXELS);
        mainWindow.setLocked(true);
        Component topBarLayout = buildTopbar();

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        mainWindow.addComponent(topBarLayout);
        mainWindow.addComponent(splitPanel);

        // Build the component tree
        VerticalLayout leftLayout = new VerticalLayout();
        splitPanel.addComponent(leftLayout);
        splitPanel.addComponent(editor.getLayout());
        leftLayout.addComponent(taskPanel);
        HorizontalLayout bottomLeftLayout = new HorizontalLayout();
        leftLayout.addComponent(bottomLeftLayout);
        bottomLeftLayout.addComponent(searchField);
        bottomLeftLayout.addComponent(newTaskButton);

        // Set the contents in the left of the split panel to use all the space
        leftLayout.setSizeFull();
        leftLayout.setExpandRatio(taskPanel, 1);
        taskPanel.setSizeFull();

        bottomLeftLayout.setWidth("100%");
        searchField.setWidth("100%");
        bottomLeftLayout.setExpandRatio(searchField, 1);

        return mainWindow;
    }

    /**
     * @return the top-bar of the main window: title + logged-in user info + logout button
     */
    private Component buildTopbar() {
        AbsoluteLayout topBarLayout = new AbsoluteLayout();
        topBarLayout.setWidth("100%");

        final Label appTitle = new Label(AppTitle);
        appTitle.setStyleName(Reindeer.LABEL_H1);
        topBarLayout.addComponent(appTitle, "top:10px; left:10px");

        topBarLayout.addComponent(logoutButton, "top:10px; right:10px");

        return topBarLayout;
    }

    private void initSearch() {
        searchField.setId(SEARCH_ID);
        // show a subtle prompt in the search field
        searchField.setInputPrompt(labels.getString(SEARCH_TASKS_PROMPT));
        // send the text over the wire as soon as user stops writing for a moment
        searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);
        searchField.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(final TextChangeEvent event) {
                taskContainer.resetFilter(event.getText());
            }
        });
    }

    private void
    initButtons(Individual currentUser) {
        newTaskButton = new Button(labels.getString(NEW_TASK_BUTTON_LABEL));
        newTaskButton.setId(NEW_TASK_ID);
        newTaskButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // add a new row in the beginning of the list and select it
                Object taskId = taskContainer.addNewRow();
                taskPanel.createNewTask(taskId);
            }
        });

        logoutButton = new Button(
                labels.getString(LOGOUT_BUTTON_LABEL) + " " + currentUser.getName());
        logoutButton.setId(LOGOUT_ID);
        logoutButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // "Logout" the user
                getSession().setAttribute("user", null);
                // Refresh this view, should redirect to login view
                getUI().getNavigator().navigateTo("");
            }
        });

    }

    void deleteCurrentTask() {
        Object listItemId = taskPanel.getValue();
        Task currentTask = taskContainer.getTaskFromItem(listItemId);
        if (currentTask != null) {
            taskService.updateTask(currentTask.delete());
        }
        taskPanel.removeItem(listItemId);
    }

    /**
     * Save the task specified: either update or create a new task
     *
     * @param listItemId
     */
    void updateTask(Object listItemId) {
        Task updateTask = taskContainer.getTaskFromItem(listItemId);
        Optional<Task> existingTask = taskService.findTaskById(updateTask.getTaskId());
        if (existingTask.isPresent()) {
            log.debug("Updating task={}", updateTask);
            taskService.updateTask(updateTask);
        } else {
            log.debug("Inserting task={}", updateTask);
            taskService.saveTask(updateTask);
        }
    }

    /**
     * save or update current task in the table
     */
    void updateCurrentTask() {
        updateTask(taskPanel.getValue());
    }

    private Individual
    getUser(String username) {
        Optional<Individual> party = partyService.findPartyByUsername(
                String.valueOf(getSession().getAttribute("user")));
        if (party.isPresent() == false) {
            log.debug("user not found: [" + username + "]");
            return null;
        }
        return party.get();
    }

    /**
     * Make resource string available to child components
     *
     * @param key of the resource request
     * @return the value associated with the key in the bundle
     */
    String getResourceLabel(String key) {
        return labels.getString(key);
    }

    /**
     * @return the task container holding the task list
     */
    TaskContainer getTaskContainer() {
        return taskContainer;
    }

    /**
     * @return the columns names to be displayed
     */
    String[] getVisibleColumns() {
        return visibleColumns;
    }

    /**
     * @return the task editor
     */
    TaskEditor getEditor() {
        return editor;
    }

    /**
     * @return the party service
     */
    PartyService getPartyService() {
        return partyService;
    }

    /**
     * @return the task service
     */
    TaskService getTaskService() {
        return taskService;
    }

}