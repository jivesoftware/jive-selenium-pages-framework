package com.jivesoftware.selenium.pagefactory.framework.actions;

import com.jivesoftware.selenium.pagefactory.framework.browser.web.SafariBrowser;

/**
 * Selenium Actions for Chrome Browser.
 *
 * Currently, this is the same as BaseSeleniumActions, as we don't have any need to implement anything differently
 * for Chrome.
 */
public class SafariSeleniumActions extends BaseSeleniumActions<SafariBrowser> {
    public SafariSeleniumActions(SafariBrowser browser) {
        super(browser);
    }
}
