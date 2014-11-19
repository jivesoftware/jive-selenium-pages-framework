package com.jivesoftware.selenium.pagefactory.framework.actions;

import com.jivesoftware.selenium.pagefactory.framework.browser.mobile.MobileBrowser;

public class MobileSeleniumActions<B extends MobileBrowser> extends BaseSeleniumActions<B> {

    public MobileSeleniumActions(B browser) {
        super(browser);
    }
}
