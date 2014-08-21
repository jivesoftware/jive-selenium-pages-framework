package com.jivesoftware.selenium.pagefactory.framework;

import com.jivesoftware.selenium.pagefactory.framework.browser.LocalBrowserBuilder;
import com.jivesoftware.selenium.pagefactory.framework.browser.web.WebBrowser;
import com.jivesoftware.selenium.pagefactory.framework.browser.web.WebBrowserType;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutType;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

/**
 * Created by charles.capps on 8/21/14.
 *
 * Basic Functional tests for creating different web browsers.
 */
public class CreateWebBrowserTests {

    @Test
    public void openGoogleChrome() throws Exception {
        WebBrowser chromeBrowser = createMinimalChrome();
        chromeBrowser.openPageByURL("http://google.com");

        chromeBrowser.getActions().verifyElementVisible(
                By.cssSelector("form[action='/search']"), // The google search form
                TimeoutType.DEFAULT);

        chromeBrowser.quit();
    }


    private WebBrowser createMinimalChrome() throws JiveWebDriverException {
        return LocalBrowserBuilder.getBuilder(WebBrowserType.CHROME, "http://google.com")
                .withWebDriverPath(TestSystemProps.WEB_DRIVER_PATH)
                .build();
    }
}
