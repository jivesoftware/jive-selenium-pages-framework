package com.jivesoftware.selenium.pagefactory.framework.actions;

import com.jivesoftware.selenium.pagefactory.framework.actions.BaseSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.browser.mobile.IOSMobileBrowser;
import com.jivesoftware.selenium.pagefactory.framework.browser.mobile.MobileBrowser;

/**
 * Created by Shiran Dadon on 8/11/14.
 *
 * Selenium Actions for Android Applications
 *
 * Currently, this is the same as BaseSeleniumActions, as we don't have any need to implement anything differently
 * for IOS.
 */
public class IOSSeleniumActions extends BaseSeleniumActions<IOSMobileBrowser> {

    public IOSSeleniumActions(IOSMobileBrowser browser) {
        super(browser);
    }
}
