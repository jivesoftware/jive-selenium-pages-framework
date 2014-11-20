package com.jivesoftware.selenium.pagefactory.framework.browser.mobile;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.interactions.HasTouchScreen;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.remote.RemoteTouchScreen;

import java.net.URL;

/**
 * Created by amir on 11/18/14.
 */
public class SwipeableWebDriver extends AppiumDriver implements HasTouchScreen {
    private RemoteTouchScreen touch;

    public SwipeableWebDriver(URL remoteAddress, Capabilities desiredCapabilities) {
        super(remoteAddress, desiredCapabilities);
        touch = new RemoteTouchScreen(getExecuteMethod());
    }

    @Override
    public TouchScreen getTouch() {
        return touch;
    }
}
