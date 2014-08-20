package com.jivesoftware.selenium.pagefactory.framework.browser.mobile;

import com.jivesoftware.selenium.pagefactory.framework.actions.mobile.AndroidSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AndroidMobileBrowser extends MobileBrowser {

    private static final Logger logger = LoggerFactory.getLogger(AndroidMobileBrowser.class);

    private String appPackage;
    private String appActivity;

    public AndroidMobileBrowser(String baseTestUrl,
                                String appiumVersion,
                                String platformName,
                                String platformVersion,
                                String deviceName,
                                String app,
                                String appPackage,
                                String appActivity,
                                TimeoutsConfig timeouts) throws JiveWebDriverException {
        super(baseTestUrl, timeouts, appiumVersion, platformName, platformVersion, deviceName, app);
        this.appPackage = appPackage;
        this.appActivity = appActivity;
    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability("appium-version", appiumVersion);
        desiredCapabilities.setCapability("platformName", platformName);
        desiredCapabilities.setCapability("platformVersion", platformVersion);
        desiredCapabilities.setCapability("deviceName", deviceName);
        desiredCapabilities.setCapability("app", app);
        desiredCapabilities.setCapability("appPackage", appPackage);
        desiredCapabilities.setCapability("appActivity", appActivity);
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
}
