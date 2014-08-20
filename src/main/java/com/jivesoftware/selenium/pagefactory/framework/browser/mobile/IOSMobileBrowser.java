package com.jivesoftware.selenium.pagefactory.framework.browser.mobile;

import com.jivesoftware.selenium.pagefactory.framework.actions.mobile.IOSSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IOSMobileBrowser extends MobileBrowser {

    private static final Logger logger = LoggerFactory.getLogger(AndroidMobileBrowser.class);

    public IOSMobileBrowser(String baseTestUrl,
                            String appiumVersion,
                            String platformName,
                            String platformVersion,
                            String deviceName,
                            String app,
                            TimeoutsConfig timeouts) throws JiveWebDriverException {
        super(baseTestUrl, timeouts, appiumVersion, platformName, platformVersion, deviceName, app);
    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.iphone();
        desiredCapabilities.setCapability("appium-version", appiumVersion);
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
}
