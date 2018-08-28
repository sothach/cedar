package org.seefin.cedar.webui.login;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import org.seefin.cedar.service.PartyService;
import org.seefin.cedar.webui.SpringContextHelper;
import org.seefin.cedar.webui.TaskBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This is the initial view presented to the user on arrival, consisting of a login
 * form for the user to enter their pre-existing username and password
 * On successful login, the view will be changed to the main view of the application,
 * the Task Board
 *
 * @author phillipsr
 */
public class CedarLoginView
        extends CustomComponent implements View, Button.ClickListener {
    static final String LOGINFORM_ID = "cedar.login.form";
    public static final String USERNAME_ID = "cedar.login.username";
    public static final String SUBMIT_ID = "cedar.login.submit";
    public static final String PASSWORD_ID = "cedar.login.password";
    static final String FEEDBACK_ID = "cedar.login.feedback";

    private static final String LOGIN_BUTTON_LABEL = "cedar.ui.login.button.label";
    private static final String INVALID_LOGIN_MESSAGE = "cedar.ui.login.invalid.message";
    private static final String LOGIN_CAPTION = "cedar.ui.login.caption";
    private static final String USERNAME_LABEL = "cedar.ui.login.username.label";
    private static final String PASSWORD_LABEL = "cedar.ui.login.password.label";
    private static final String ResourceBundleName = "org.seefin.cedar.webui.i18n.CedarWebUI";

    private static final Logger log = LoggerFactory.getLogger(CedarLoginView.class);

    public static final String NAME = "login";

    // fields default scope to allow test access (not optimal!)
    private final TextField user;
    private final PasswordField password;
    private final Label feedback;
    private final PartyService partyService;
    private final String InvalidLoginMessage;

    public CedarLoginView() {
        // we cannot set the user's locale, as we don't know
        // who they are yet, so use the platform default
        ResourceBundle labels = ResourceBundle.getBundle(
                ResourceBundleName, Locale.getDefault());

        partyService = getPartyService();

        InvalidLoginMessage = labels.getString(INVALID_LOGIN_MESSAGE);
        setSizeFull();

        user = createUserField(labels);
        password = createPasswordField(labels);
        final Button loginButton = createLoginButton(labels);
        feedback = createFeedbackField();

        // Add both to a panel
        VerticalLayout fields = new VerticalLayout(user, password, feedback, loginButton);
        fields.setCaption(labels.getString(LOGIN_CAPTION));
        fields.setStyleName(Reindeer.LABEL_H1);
        fields.setSpacing(true);
        fields.setMargin(new MarginInfo(true, true, true, false));
        fields.setSizeUndefined();
        fields.setId(LOGINFORM_ID);

        // The view root layout
        VerticalLayout viewLayout = new VerticalLayout(fields);
        viewLayout.setSizeFull();
        viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
        viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);
        setCompositionRoot(viewLayout);
    }

    private Label createFeedbackField() {
        Label result = new Label();
        result.setId(FEEDBACK_ID);
        result.setWidth("300px");
        result.setStyleName(Reindeer.LABEL_H2);
        result.setValue("");
        return result;
    }

    // Create login button
    private Button createLoginButton(ResourceBundle labels) {
        Button result = new Button(labels.getString(LOGIN_BUTTON_LABEL), this);
        result.setId(SUBMIT_ID);
        return result;
    }

    // Create the password input field
    private PasswordField createPasswordField(ResourceBundle labels) {
        PasswordField result = new PasswordField(labels.getString(PASSWORD_LABEL));
        result.setId(PASSWORD_ID);
        result.setWidth("300px");
        result.setRequired(true);
        result.setValue("");
        result.setNullRepresentation("");
        return result;
    }

    // Create the user input field
    private TextField createUserField(ResourceBundle labels) {
        TextField result = new TextField(labels.getString(USERNAME_LABEL));
        result.setId(USERNAME_ID);
        result.setWidth("300px");
        result.setRequired(true);
        result.setInputPrompt("Your username");
        result.setInvalidAllowed(false);
        return result;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        // focus the username field when user arrives to the login view
        user.focus();
    }

    /**
     * @return the party service bean, from the web application context
     */
    private PartyService
    getPartyService() {
        SpringContextHelper springHelper
                = new SpringContextHelper(
                VaadinServlet.getCurrent().getServletContext());
        return (PartyService) springHelper.getBean("partyService");
    }

    @Override
    public void buttonClick(ClickEvent event) {
        String username = user.getValue();
        String password = this.password.getValue();

        // Validate username and password with the party service
        if (partyService.isPasswordValid(username, password) == false) {
            log.debug("Invalid login attempt, user={}", username);
            // Wrong password clear the password field and refocuses it
            this.feedback.setValue(InvalidLoginMessage);
            this.password.setValue(null);
            this.password.focus();
            return;
        }
        log.debug("Successfully logged-in user={}", username);
        // Store the current user in the service session
        getSession().setAttribute("user", username);

        // Navigate to the task board view
        getUI().getNavigator().navigateTo(TaskBoard.NAME);
    }

}