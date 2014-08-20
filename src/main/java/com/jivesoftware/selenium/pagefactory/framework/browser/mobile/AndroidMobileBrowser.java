package com.jivesoftware.selenium.pagefactory.framework.browser.mobile;

import com.jivesoftware.selenium.pagefactory.framework.actions.AndroidSeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class AndroidMobileBrowser extends MobileBrowser {

    private String appPackage;
    private String appActivity;

    public AndroidMobileBrowser(String baseTestUrl,
                                String platformName,
                                String platformVersion,
                                String deviceName,
                                String app,
                                String appPackage,
                                String appActivity,
                                TimeoutsConfig timeouts) throws JiveWebDriverException {
        super(baseTestUrl, timeouts, platformName, platformVersion, deviceName, app);
        this.appPackage = appPackage;
        this.appActivity = appActivity;
    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
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
