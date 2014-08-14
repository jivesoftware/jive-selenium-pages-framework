package com.jivesoftware.selenium.pagefactory.framework.browser;

import com.jivesoftware.selenium.pagefactory.framework.actions.SeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;

/**
 * <p>Represents a RemoteBrowser, i.e. running a Browser on a Selenium Node controlled by a Selenium Hub.
 * To create an instance, pass in the "delegate" browser and the URL to the Selenium Hub.
 * Example Selenium Hub URL: http://hub.my.company.com:4444/wd/hub</p>
 *
 * See <a href="http://code.google.com/p/selenium/wiki/Grid2">http://code.google.com/p/selenium/wiki/Grid2</a>
 */
public class RemoteBrowser extends Browser {
    protected Browser delegate;
    protected String seleniumHubURL;
    private static final Logger logger = LoggerFactory.getLogger(RemoteBrowser.class);


    public RemoteBrowser(Browser delegate, String seleniumHubURL) {
        super(delegate.getBaseTestUrl(),
                delegate.getTimeouts(),
                delegate.getWebDriverPath(),
                delegate.getBrowserBinaryPath(),
                delegate.getBrowserVersion(),
                delegate.getBrowserLocale(),
                delegate.getStartWindowWidth(),
                delegate.getStartWindowHeight());
        this.delegate = delegate;
        this.seleniumHubURL = seleniumHubURL;
    }

    @Override
    public BrowserType getBrowserType() {
        return delegate.getBrowserType();
    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        return delegate.getDesiredCapabilities();
    }

    @Override
    protected WebDriver createWebDriver() throws JiveWebDriverException {
        try {
            RemoteWebDriver driver = new RemoteWebDriver(new URL(seleniumHubURL), delegate.getDesiredCapabilities());
            Level level = getLogLevel();
            driver.setLogLevel(level);
            driver.setFileDetector(new LocalFileDetector()); // Allow to upload local files to remote webdriver
            // https://code.google.com/p/selenium/source/browse/java/client/src/org/openqa/selenium/remote/LocalFileDetector.java
            return driver;
        } catch (MalformedURLException e) {
            throw new JiveWebDriverException("Invalid Selenium Hub URL given: " + seleniumHubURL, e);
        }
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public SeleniumActions getActions() {
        SeleniumActions actions = delegate.getActions();
        actions.setBrowser(this);  //We are running remotely, so the Actions should use the RemoteBrowser and RemoteWebDriver
        return actions;
    }

    /**
     * Get the Browser logs (console logs) from the Remote Browser.
     * Added more logging to debug a 5 minute gap in time we saw in a recent failed test run.
     * The issue is probably unrelated to this, but it can't hurt to log more data so we can rule it out.
     *
     * @return
     */
    @Nullable
    public LogEntries getBrowserLogEntries() {
        if (delegate.getBrowserType() == BrowserType.IE) {
            logger.info("IE does not support getting Browser Logs remotely. Returning null from getBrowserLogEntries");
            return null;
        }
        try {
            if (webDriver == null) {
                logger.info("The web driver was null in getBrowserLogEntries. Returning null.");
                return null;
            }
            logger.debug("Getting the available log types from remote Selenium node...");
            Set<String> availableLogTypes = webDriver.manage().logs().getAvailableLogTypes();

            logger.debug("Found available log types: {}", String.valueOf(availableLogTypes));

            if (availableLogTypes == null || !availableLogTypes.contains(LogType.BROWSER)) {
                logger.info("{} log type not allowed. Returning null.", LogType.BROWSER);
                return null;
            }
            logger.debug("Fetching logs from remote server...");

            LogEntries logs = webDriver.manage().logs().get(LogType.BROWSER);

            logger.info("Success getting remote logs!");

            return logs;
        } catch (Exception e) {
            // If some error occurs making the HTTP request to get logs, just return null.
            logger.info("Error retrieving remote logs: " + e.getMessage());
            return null;
        }
    }
}
