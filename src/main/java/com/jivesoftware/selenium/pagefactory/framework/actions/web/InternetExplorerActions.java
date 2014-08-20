package com.jivesoftware.selenium.pagefactory.framework.actions.web;

import com.jivesoftware.selenium.pagefactory.framework.browser.web.WebBrowser;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SeleniumActions class for InternetExplorer.
 * There is only one workaround, due to a bug in Selenium 2.42.
 */
public class InternetExplorerActions extends BaseWebSeleniumActions {
    private final static Logger logger = LoggerFactory.getLogger(InternetExplorerActions.class);

    public InternetExplorerActions(WebBrowser browser) {
        super(browser);
    }

    //Workaround for http://code.google.com/p/selenium/issues/detail?id=7524, just for IE
    @Override
    public void verifyElementInvisible(By locator, TimeoutType timeout) {
        try {
            super.verifyElementInvisible(locator, timeout);
        } catch (WebDriverException e) {
            logger.debug("WebDriverException in InternetExplorerActions#verifyElementInvisible: " + e.getMessage(), e);
            // The issue happens when the element is removed from the DOM, so just try again and it should work
            super.verifyElementInvisible(locator, timeout);
        }
    }

}
