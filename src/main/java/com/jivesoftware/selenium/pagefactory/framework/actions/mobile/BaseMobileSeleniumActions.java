package com.jivesoftware.selenium.pagefactory.framework.actions.mobile;

import com.jivesoftware.selenium.pagefactory.framework.actions.BaseSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.browser.mobile.MobileBrowser;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Shiran Dadon on 8/11/14.
 */
public abstract class BaseMobileSeleniumActions extends BaseSeleniumActions<MobileBrowser> {

    protected BaseMobileSeleniumActions(MobileBrowser browser) {
        super(browser);
    }

}
