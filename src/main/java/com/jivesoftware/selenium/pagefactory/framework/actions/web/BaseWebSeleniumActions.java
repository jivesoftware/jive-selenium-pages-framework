package com.jivesoftware.selenium.pagefactory.framework.actions.web;

import com.google.common.base.Function;
import com.jivesoftware.selenium.pagefactory.framework.actions.BaseSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.actions.SeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.browser.web.WebBrowser;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutType;
import com.jivesoftware.selenium.pagefactory.framework.pages.BaseTopLevelPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;


/**
 * Created by Shiran Dadon on 8/11/14.
 */
public abstract class BaseWebSeleniumActions extends BaseSeleniumActions<WebBrowser>{

    protected BaseWebSeleniumActions(WebBrowser browser) {
        super(browser);
    }
}
