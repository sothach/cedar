package org.seefin.cedar.webui.taskboard;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seefin.cedar.webui.TaskBoard;
import org.seefin.cedar.webui.TaskEditor;
import org.seefin.cedar.webui.login.CedarLoginView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author phillipsr
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
public class TestTaskBoard {
    private static final int JS_WAIT_PERIOD = 7000;
    private static final String GOOD_PASSWORD = "abc123";
    private static final String GOOD_USERNAME = "test";

    @Value("${cedar.test.url}")
    private String testUrl;

    private WebClient webClient;

    @Before
    public void
    loadApp() {
        webClient = new WebClient(BrowserVersion.FIREFOX_17);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(true);
    }

    @After
    public void
    unloadApp() {
        webClient.closeAllWindows();
    }

    @Test
    public void
    testAddTask()
            throws Exception {
        HtmlPage page = webClient.getPage(testUrl);
        webClient.waitForBackgroundJavaScriptStartingBefore(JS_WAIT_PERIOD);

        HtmlInput username = page.getHtmlElementById(CedarLoginView.USERNAME_ID);
        HtmlPasswordInput password = page.getHtmlElementById(CedarLoginView.PASSWORD_ID);
        HtmlElement submit = page.getHtmlElementById(CedarLoginView.SUBMIT_ID);

        username.type(GOOD_USERNAME);
        password.type(GOOD_PASSWORD);
        submit.click();
        webClient.waitForBackgroundJavaScriptStartingBefore(1000);

        HtmlElement newTask = page.getHtmlElementById(TaskBoard.NEW_TASK_ID);
        page = newTask.click();
        webClient.waitForBackgroundJavaScriptStartingBefore(1000);

        HtmlTextArea description = page.getHtmlElementById(TaskEditor.DESCRIPTION_ID);
        HtmlElement update = page.getHtmlElementById(TaskEditor.UPDATE_ID);
        description.type("New Task Test: Description");

        page = update.click();
        webClient.waitForBackgroundJavaScriptStartingBefore(1000);

        HtmlInput search = page.getHtmlElementById(TaskBoard.SEARCH_ID);
        search.type("Test");
        page = search.click();
        webClient.waitForBackgroundJavaScriptStartingBefore(1000);

        HtmlElement delete = page.getHtmlElementById(TaskEditor.DELETE_ID);
        page = delete.click();
        webClient.waitForBackgroundJavaScriptStartingBefore(1000);

    }

}
