package org.seefin.cedar.webui;

import com.vaadin.annotations.Title;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;
import org.seefin.cedar.model.parties.Individual;
import org.seefin.cedar.model.tasks.Task;
import org.seefin.cedar.service.PartyService;
import org.seefin.cedar.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Locale;
import java.util.Objects;
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

    private static final String ResourceBundleName = "org.seefin.cedar.webui.i18n.CedarWebUI";
    private static final String CEDAR_TASKBOARD_TITLE = "cedar.ui.taskboard.title";
    private static final String LOGOUT_BUTTON_LABEL = "cedar.ui.taskboard.button.label.logout";
    private static final String NEW_TASK_BUTTON_LABEL = "cedar.ui.taskboard.button.label.newTask";
    private static final String SEARCH_TASKS_PROMPT = "cedar.ui.taskboard.search.prompt";
    private static final String UPDATE_BUTTON_LABEL = "cedar.ui.taskboard.button.label.update";
    private static final String DELETE_TASK_BUTTON_LABEL = "cedar.ui.taskboard.button.label.delete";
    private static final String ENTER_TASK_DESCRIPTION = "cedar.ui.taskboard.edit.caption";
    private static final String DESC = "cedar.ui.taskboard.edit.description";
    private static final String CREATED = "cedar.ui.taskboard.edit.created";
    private static final String STATUS = "cedar.ui.taskboard.edit.status";

    static String getUpdateButtonLabel() { return UPDATE_BUTTON_LABEL; }
    static String getDeleteButtonLabel() { return DELETE_TASK_BUTTON_LABEL; }
    static String getEnterTaskDescription() { return ENTER_TASK_DESCRIPTION; }
    static String getDescriptionLabel() { return DESC; }
    static String getCreated() { return CREATED; }
    static String getUStatus() { return STATUS; }

    private static final Logger log = LoggerFactory.getLogger(TaskBoard.class);

    public static final String NAME = "TaskBoard"; // for navigation purposes

    /* user interface components are stored in session */
    private final TextField searchField = new TextField();
    private Button newTaskButton;
    private Button logoutButton;
    private TaskEditor editor;
    private TaskTable taskPanel;
    private ResourceBundle labels;

    static final String TASK_ID = "TaskId"; // (hidden field)

    private String[] visibleColumns;
    private String DateTimePattern;

    private TaskContainer taskContainer;

    private PartyService partyService;
    private TaskService taskService;
    private String AppTitle;

    /**
     * {@inheritDoc}
     * <p/>Use the logged-in user's locale settings to initialize the interface
     * element appropriately, ex-ject the services out of the Spring context
     */
    @Override
    public void enter(final ViewChangeEvent event) {
        loadServices();

        // get logged-in user details and initialize view accordingly
        final Individual currentUser = getUser(String.valueOf(getSession().getAttribute("user")));

        labels = loadResources(Objects.requireNonNull(currentUser));

        setSizeFull();

        taskContainer = new TaskContainer(this);
        taskContainer.loadTasks(String.valueOf(getSession().getAttribute("user")));
        initButtons(currentUser); // create buttons + listeners (do this before layout of main window)

        editor = new TaskEditor(this);
        taskPanel = new TaskTable(this, taskContainer);
        final Component layout = createPagelayout();
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
    loadResources(final Individual user) {
        final ResourceBundle result = ResourceBundle.getBundle(
                ResourceBundleName, new Locale(user.getLocale()));
        AppTitle = result.getString(CEDAR_TASKBOARD_TITLE);
        final String descriptionLabel = result.getString(DESC);
        final String createdLabel = result.getString(CREATED);
        final String statusLabel = result.getString(STATUS);
        visibleColumns = new String[]{descriptionLabel, createdLabel, statusLabel};
        DateTimePattern = "MS"; // DateTimeFormatter.patternForStyle("MS", new Locale(user.getLocale()));

        return result;
    }

    /**
     * @return the user-locale configured date/time formatting pattern
     */
    protected String getDateTimePattern() {
        return DateTimePattern;
    }

    /**
     * pull the required services in from the Spring context
     */
    private void loadServices() {
        final SpringContextHelper springHelper
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
        final VerticalSplitPanel mainWindow = new VerticalSplitPanel();
        mainWindow.setSplitPosition(52, Unit.PIXELS);
        mainWindow.setLocked(true);
        final Component topBarLayout = buildTopbar();

        final HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        mainWindow.addComponent(topBarLayout);
        mainWindow.addComponent(splitPanel);

        // Build the component tree
        final VerticalLayout leftLayout = new VerticalLayout();
        splitPanel.addComponent(leftLayout);
        splitPanel.addComponent(editor.getLayout());
        leftLayout.addComponent(taskPanel);
        final HorizontalLayout bottomLeftLayout = new HorizontalLayout();
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
        final AbsoluteLayout topBarLayout = new AbsoluteLayout();
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
        searchField.addTextChangeListener((TextChangeListener) event -> taskContainer.resetFilter(event.getText()));
    }

    private void
    initButtons(final Individual currentUser) {
        newTaskButton = new Button(labels.getString(NEW_TASK_BUTTON_LABEL));
        newTaskButton.setId(NEW_TASK_ID);
        newTaskButton.addClickListener((ClickListener) event -> {
            // add a new row in the beginning of the list and select it
            final Object taskId = taskContainer.addNewRow();
            taskPanel.createNewTask(taskId);
        });

        logoutButton = new Button(
                labels.getString(LOGOUT_BUTTON_LABEL) + " " + currentUser.getName());
        logoutButton.setId(LOGOUT_ID);
        logoutButton.addClickListener((ClickListener) event -> {
            // "Logout" the user
            getSession().setAttribute("user", null);
            // Refresh this view, should redirect to login view
            getUI().getNavigator().navigateTo("");
        });

    }

    protected void deleteCurrentTask() {
        final Object listItemId = taskPanel.getValue();
        final Task currentTask = taskContainer.getTaskFromItem(listItemId);
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
    protected void updateTask(final Object listItemId) {
        final Task updateTask = taskContainer.getTaskFromItem(listItemId);
        final Optional<Task> existingTask = taskService.findTaskById(updateTask.getTaskId());
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
    protected void updateCurrentTask() {
        updateTask(taskPanel.getValue());
    }

    private Individual
    getUser(final String username) {
        final Optional<Individual> party = partyService.findPartyByUsername(
                String.valueOf(getSession().getAttribute("user")));
        if (!party.isPresent()) {
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
    protected String getResourceLabel(final String key) {
        return labels.getString(key);
    }

    /**
     * @return the task container holding the task list
    protected  */
    protected TaskContainer getTaskContainer() {
        return taskContainer;
    }

    /**
     * @return the columns names to be displayed
     */
    protected String[] getVisibleColumns() {
        return visibleColumns;
    }

    /**
     * @return the task editor
     */
    protected TaskEditor getEditor() {
        return editor;
    }

    /**
     * @return the party service
     */
    protected PartyService getPartyService() {
        return partyService;
    }

    /**
     * @return the task service
     */
    protected TaskService getTaskService() {
        return taskService;
    }

}