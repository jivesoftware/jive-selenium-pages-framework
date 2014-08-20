package com.jivesoftware.selenium.pagefactory.framework.actions;


import com.jivesoftware.selenium.pagefactory.framework.browser.mobile.AndroidMobileBrowser;

/**
 * Created by Shiran Dadon on 8/11/14.
 *
 * Selenium Actions for Android Applications
 *
 * Currently, this is the same as BaseSeleniumActions, as we don't have any need to implement anything differently
 * for Android.
 */
public class AndroidSeleniumActions extends BaseSeleniumActions<AndroidMobileBrowser> {

    public AndroidSeleniumActions(AndroidMobileBrowser browser) {
        super(browser);
    }
}
