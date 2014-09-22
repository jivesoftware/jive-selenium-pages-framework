package com.jivesoftware.selenium.pagefactory.framework.browser.mobile;

import com.jivesoftware.selenium.pagefactory.framework.actions.AndroidSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AndroidMobileBrowser extends MobileBrowser {

    private String appPackage;
    private String appActivity;
    private static final Logger logger = LoggerFactory.getLogger(AndroidMobileBrowser.class);
    public AndroidMobileBrowser(String baseTestUrl,
                                String browserName,
                                String platformName,
                                String platformVersion,
                                String deviceName,
                                String newCommandTimeout,
                                String automationName,
                                String version,
                                String autoLaunch,
                                String app,
                                String appPackage,
                                String appActivity,
                                TimeoutsConfig timeouts) throws JiveWebDriverException {
        super(baseTestUrl, timeouts, browserName, platformName, platformVersion, deviceName,
                newCommandTimeout, automationName, version, autoLaunch, app);
        this.appPackage = appPackage;
        this.appActivity = appActivity;
    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(CapabilityType.BROWSER_NAME, browserName);
        desiredCapabilities.setCapability("platformName", platformName);
        desiredCapabilities.setCapability("platformVersion", platformVersion);
        desiredCapabilities.setCapability("deviceName", deviceName);
        desiredCapabilities.setCapability("newCommandTimeout", newCommandTimeout);
        desiredCapabilities.setCapability("automationName", automationName);
        desiredCapabilities.setCapability("version", version);
        desiredCapabilities.setCapability("autoLaunch", autoLaunch);
        desiredCapabilities.setCapability("app", app);
        desiredCapabilities.setCapability("appPackage", appPackage);
        desiredCapabilities.setCapability("appWaitActivity", appActivity);
        desiredCapabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
        return desiredCapabilities;
    }

    @Override
    public AndroidSeleniumActions getActions() {
        return new AndroidSeleniumActions(this);
    }

    public String getAppPackage() {
        return appPackage;
    }

    public String getAppActivity() {
        return appActivity;
    }

    @Override
    public void scrollToTop() {
        logger.error("Method ScrollToTop is not yet implemented");
    }
}
