package org.seefin.cedar.webui.login;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seefin.cedar.webui.TaskBoard;
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
public class TestLogin {
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
    testLoginSuccess()
            throws Exception {
        HtmlPage page = webClient.getPage(testUrl);
        webClient.waitForBackgroundJavaScriptStartingBefore(JS_WAIT_PERIOD);

        HtmlInput username = page.getHtmlElementById(CedarLoginView.USERNAME_ID);
        HtmlPasswordInput password = page.getHtmlElementById(CedarLoginView.PASSWORD_ID);
        HtmlElement submit = page.getHtmlElementById(CedarLoginView.SUBMIT_ID);

        username.type(GOOD_USERNAME);
        password.type(GOOD_PASSWORD);

        page = submit.click();
        webClient.waitForBackgroundJavaScriptStartingBefore(1000);

        HtmlElement feedback = page.getHtmlElementById(TaskBoard.LOGOUT_ID);
        page = feedback.click();
        webClient.waitForBackgroundJavaScriptStartingBefore(1000);

        Assert.assertNotNull(page.getHtmlElementById(CedarLoginView.LOGINFORM_ID));
    }

    @Test
    public void
    testLoginFailure()
            throws Exception {
        HtmlPage page = webClient.getPage(testUrl);
        webClient.waitForBackgroundJavaScriptStartingBefore(JS_WAIT_PERIOD);

        HtmlInput username = page.getHtmlElementById(CedarLoginView.USERNAME_ID);
        HtmlPasswordInput password = page.getHtmlElementById(CedarLoginView.PASSWORD_ID);
        HtmlElement submit = page.getHtmlElementById(CedarLoginView.SUBMIT_ID);

        username.type("xxxx");
        password.type("zzzz");

        submit.click();
        webClient.waitForBackgroundJavaScriptStartingBefore(1000);
        HtmlElement feedback = page.getHtmlElementById(CedarLoginView.FEEDBACK_ID);
        Assert.assertEquals("invalid username/password", feedback.getTextContent());
    }

}
