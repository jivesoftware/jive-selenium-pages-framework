package com.jivesoftware.selenium.pagefactory.framework.browser;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;

import java.util.logging.Level;

/**
 * Create browser from standard parameters.
 *
 * This is the easiest way to create a Browser. A Browser has everything your tests need to interact with a web page,
 * but it's also highly recommended that you implement Page classes.
 *
 * You can call Browser.getActions() to get the SeleniumActions instance for the current browser.
 *
 */
public class BrowserFactory {

    /**
     * Create a Remote Browser that communicates with a Selenium HUB.
     * @param browserType    - CHROME, FIREFOX, or IE.
     * @param baseTestUrl    - Base URL of the Web App you are testing against
     * @param seleniumHubURL - Selenium Grid HUB URL, e.g. http://selenium.my.company.com:4444/wd/hub
     * @param timeouts       - Timeout configuration, use TimeoutsConfig.builder() to get an instance.
     * @param browserVersion - Version of browser that the Selenium Hub must find a matching Node for.
     * @param browserLocale  - e.g. Locale.US.toString()
     * @param startWindowWidth - Size of Browser window in X direction, in pixels
     * @param startWindowHeight - Size of Browser window in Y direction, in pixels
     * @param browserLogLevel    - Log level for Browser logs and Remote Web Driver logs.
     * @param browserLogFile     - Path (either local or on remote Selenium node) to log file that will be created.
     *                             Currently only works for local Chrome and local/remote IE.
     * @return
     * @throws JiveWebDriverException, IllegalArgumentException
     */
    public static RemoteBrowser createRemoteBrowser(BrowserType browserType,
                                                    String baseTestUrl,
                                                    String seleniumHubURL,
                                                    TimeoutsConfig timeouts,
                                                    Optional<String> browserVersion,
                                                    Optional<String> browserLocale,
                                                    Optional<Integer> startWindowWidth,
                                                    Optional<Integer> startWindowHeight,
                                                    Optional<Level> browserLogLevel,
                                                    Optional<String> browserLogFile)
            throws JiveWebDriverException {
        Preconditions.checkNotNull(browserType, "You must provide a non-null BrowserType!");
        Preconditions.checkNotNull(baseTestUrl, "You must provide a non-null base test URL!");
        Preconditions.checkNotNull(seleniumHubURL, "You must provide a non-null Selenium Hub URL to create a Remote Web Browser!");
        Preconditions.checkNotNull(timeouts, "You must provide a non-null TimeoutsConfig!");

        return (RemoteBrowser) createBrowserInstance(browserType,
                baseTestUrl,
                timeouts,
                Optional.of(seleniumHubURL),
                Optional.<String>absent(),
                Optional.<String>absent(),
                browserVersion,
                browserLocale,
                startWindowWidth,
                startWindowHeight,
                browserLogLevel,
                browserLogFile);
    }

    /**
     * Create a local browser running on the same host.
     *
     * @param browserType - CHROME, FIREFOX, or IE.
     * @param baseTestUrl - Base URL of the Web App you are testing against
     * @param timeouts    - Timeout configuration, use TimeoutsConfig.builder() to get an instance.
     * @param webDriverPath  - path on the local filesystem to the web driver binary - chromedriver or IEDriverServer
     *                      driverPath isn't required if the driver is on the system's PATH variable.
     * @param browserBinaryPath - path on the local filesystem to the binary for starting the browser, e.g. chrome or firefox
     *                            browserBinaryPath isn't required if the browser is installed in the expected location,
     *                            See e.g. for chrome: http://code.google.com/p/selenium/wiki/ChromeDriver
     * @param browserLocale       - e.g. Locale.US.toString()
     * @param startWindowWidth  - Starting width of a browser window, in pixels.
     *                            if startWindowWidth and startWindowHeight are defined, then the Browser window will be
     *                            resized to the given value when started.
     * @param startWindowHeight  - Starting height of a browser window, in pixels.
     * @param browserLogLevel    - Log level for Browser logs and Remote Web Driver logs.
     * @param browserLogFile        - Path in the local file system to a log file.
     *                              Currently only works for local chrome or local/remote IE
     * @return
     * @throws JiveWebDriverException, IllegalArgumentException
     */
    public static Browser createLocalBrowser(BrowserType browserType,
                                             String baseTestUrl,
                                             TimeoutsConfig timeouts,
                                             Optional<String> webDriverPath,
                                             Optional<String> browserBinaryPath,
                                             Optional<String> browserLocale,
                                             Optional<Integer> startWindowWidth,
                                             Optional<Integer> startWindowHeight,
                                             Optional<Level> browserLogLevel,
                                             Optional<String> browserLogFile)
            throws JiveWebDriverException {
        Preconditions.checkNotNull(browserType, "You must provide a non-null Browser Type!");
        Preconditions.checkNotNull(baseTestUrl, "You must provide a non-null base test URL!");
        Preconditions.checkNotNull(timeouts, "You must provide a non-null TimeoutsConfig!");
        return createBrowserInstance(browserType, baseTestUrl, timeouts,
                                     Optional.<String>absent(),  // Hub URL must not be present for local browser
                                     webDriverPath,
                                     browserBinaryPath,
                                     Optional.<String>absent(), // Browser version not required for local browser
                                     browserLocale,
                                     startWindowWidth,
                                     startWindowHeight,
                                     browserLogLevel,
                                     browserLogFile);
    }


    private static Browser createBrowserInstance(BrowserType browserType,
                                                String baseTestUrl,
                                                TimeoutsConfig timeouts,
                                                Optional<String> seleniumHubURL,
                                                Optional<String> webDriverPath,
                                                Optional<String> browserBinaryPath,
                                                Optional<String> browserVersion,
                                                Optional<String> browserLocale,
                                                Optional<Integer> startWindowWidth,
                                                Optional<Integer> startWindowHeight,
                                                Optional<Level> browserLogLevel,
                                                Optional<String> browserLogFile)
            throws IllegalArgumentException, JiveWebDriverException {

        Preconditions.checkNotNull(browserType, "BrowserType cannot be null in BrowserFactory#createBrowserInstance()");

        Browser browser;
        switch (browserType) {
            case FIREFOX:
                browser = new FirefoxBrowser(baseTestUrl, timeouts, webDriverPath, browserBinaryPath, browserVersion, browserLocale, startWindowWidth, startWindowHeight);
                break;
            case CHROME:
                browser = new ChromeBrowser(baseTestUrl, timeouts, webDriverPath, browserBinaryPath, browserVersion, browserLocale, startWindowWidth, startWindowHeight,
                        browserLogLevel, browserLogFile);
                break;
            case IE:
                browser = new InternetExplorerBrowser(baseTestUrl, timeouts, webDriverPath, browserBinaryPath, browserVersion, browserLocale, startWindowWidth, startWindowHeight,
                        browserLogLevel, browserLogFile);
                break;
            default:
                throw new IllegalArgumentException("Only Firefox, Chrome, and IE are currently supported!");
        }

        // If a Remote URL is supplied, then create a RemoteBrowser with configuration taken from the "delegate" browser where it makes sense.
        if (seleniumHubURL.isPresent()) {
            browser = new RemoteBrowser(browser, seleniumHubURL.get());
        }

        browser.initializeBrowser();

        return browser;
    }

}
