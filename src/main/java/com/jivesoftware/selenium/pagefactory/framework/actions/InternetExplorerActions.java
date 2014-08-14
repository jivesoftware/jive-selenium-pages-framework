package com.jivesoftware.selenium.pagefactory.framework.actions;

import com.jivesoftware.selenium.pagefactory.framework.browser.Browser;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutType;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SeleniumActions class for InternetExplorer.
 * There is only one workaround, due to a bug in Selenium 2.42.
 */
public class InternetExplorerActions extends BaseSeleniumActions {
    private final static Logger logger = LoggerFactory.getLogger(InternetExplorerActions.class);

    public InternetExplorerActions(Browser browser) {
        super(browser);
    }

    //Workaround for http://code.google.com/p/selenium/issues/detail?id=7524, just for IE
    @Override
    public void verifyElementInvisible(String css, TimeoutType timeout) {
        try {
            super.verifyElementInvisible(css, timeout);
        } catch (WebDriverException e) {
            logger.debug("WebDriverException in InternetExplorerActions#verifyElementInvisible: " + e.getMessage(), e);
            // The issue happens when the element is removed from the DOM, so just try again and it should work
            super.verifyElementInvisible(css, timeout);
        }
    }

}
