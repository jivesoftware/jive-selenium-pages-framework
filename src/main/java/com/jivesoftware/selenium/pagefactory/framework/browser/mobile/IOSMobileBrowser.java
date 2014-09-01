package com.jivesoftware.selenium.pagefactory.framework.browser.mobile;

import com.jivesoftware.selenium.pagefactory.framework.actions.IOSSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


/**
 * Added by Shiran.Dadon
 * Known bug of Apple from Xcode 5 and iOS 7.1 Simulator - swipe is not working on simulator.
 * As a workaround, using a python script
 */

public class IOSMobileBrowser extends MobileBrowser {


    public IOSMobileBrowser(String baseTestUrl,
                            String platformName,
                            String platformVersion,
                            String deviceName,
                            String app,
                            TimeoutsConfig timeouts) throws JiveWebDriverException {
        super(baseTestUrl, timeouts, platformName, platformVersion, deviceName, app);
    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.iphone();
        desiredCapabilities.setCapability("platformName", platformName);
        desiredCapabilities.setCapability("platformVersion", platformVersion);
        desiredCapabilities.setCapability("deviceName", deviceName);
        desiredCapabilities.setCapability("app", app);
        desiredCapabilities.setCapability("rotatable", true);
        return desiredCapabilities;
    }

    @Override
    public IOSSeleniumActions getActions() {
        return new IOSSeleniumActions(this);
    }
    /**
     * Swipe from the right to left for a second
     */
    public void swipeLeft() {
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("direction", "left");
        webDriver.executeScript("mobile: scroll", scrollObject);
    }

    /**
     * Swipe from the left to right for a second
     */
    public void swipeRight() {
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("direction", "right");
        webDriver.executeScript("mobile: scroll", scrollObject);
    }

    /**
     * Swipe from the top to buttom for a second
     */
    public void dragDown() {
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("direction", "down");
        webDriver.executeScript("mobile: scroll", scrollObject);
    }

    /**
     * Swipe from the down to up for a second
     */
    public void dragUp() {
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("direction", "up");
        webDriver.executeScript("mobile: scroll", scrollObject);
    }

    /**
     *
     * @param startX - 0 is the left side of the smart-phone
     * @param endX
     * @param startY - 0 is the upper side of the smart-phone
     * @param endY
     * @param duration - in milliseconds
     * Will function only with real device
     */
    public void swipe(int startX, int endX, int startY, int endY, int duration) {
        webDriver.swipe(startX, startY, endX, endY, duration);
    }
}
