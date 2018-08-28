package org.seefin.cedar.webui.login;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import org.seefin.cedar.webui.TaskBoard;

/**
 * Cedar app UI: presents login view, and adds
 * the main window view, ready for a successful login
 *
 * @author phillipsr
 */
public class CedarLoginUI
        extends UI {
    @Override
    protected void init(VaadinRequest request) {

        setLocale(request.getLocale()); // set the default locale from the request
        new Navigator(this, this);

        // initial login view where the user can login to the application
        getNavigator().addView(""/*CedarLoginView.NAME*/, CedarLoginView.class);

        // add application main view 
        getNavigator().addView(TaskBoard.NAME, TaskBoard.class);

        // view change handler ensures the user is always redirected
        // to this login view, if the user is not already logged in
        getNavigator().addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                // check if a user has logged in
                boolean isLoggedIn = getSession().getAttribute("user") != null;
                boolean isLoginView = event.getNewView() instanceof CedarLoginView;

                // always redirect to login view if user is not logged in
                if (isLoggedIn == false && isLoginView == false) {
                    getNavigator().navigateTo(""); //CedarLoginView.NAME);
                    return false;
                }
                // cancel if someone tries to access to login view when already logged in
                return !isLoggedIn || !isLoginView;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                // nix zu tun
            }
        });
    }
}