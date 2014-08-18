package com.jivesoftware.selenium.pagefactory.framework.browser;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.jivesoftware.selenium.pagefactory.framework.actions.SeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import com.jivesoftware.selenium.pagefactory.framework.pages.BaseTopLevelPage;
import com.jivesoftware.selenium.pagefactory.framework.pages.PageUtils;
import com.jivesoftware.selenium.pagefactory.framework.pages.SubPage;
import com.jivesoftware.selenium.pagefactory.framework.pages.TopLevelPage;
import com.jivesoftware.selenium.pagefactory.framework.webservice.EndpointBuilder;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Base Browser class.
 * Contains a lot of the configuration that is common across browsers.
 * Subclasses must implement getBrowserType, getDesiredCapabilities, isRemote, and getActions
 */
public abstract class Browser {
    private static Logger logger = LoggerFactory.getLogger(Browser.class);
    private static final PageUtils PAGE_UTILS = new PageUtils();

    protected WebDriver webDriver;
    private final String baseTestUrl;
    private final TimeoutsConfig timeouts;
    private final Optional<String> webDriverPath;
    private final Optional<String> browserBinaryPath;
    private final Optional<String> browserVersion;
    private final Optional<String> browserLocale;
    private final Optional<Integer> startWindowWidth;
    private final Optional<Integer> startWindowHeight;
    private final Optional<Level> browserLogLevel;
    private final Optional<String> browserLogFile;

    private Optional<CachedPage> optionalCachedPage = Optional.absent();

    public Browser(String baseTestUrl,
                   TimeoutsConfig timeouts,
                   Optional<String> webDriverPath,
                   Optional<String> browserBinaryPath,
                   Optional<String> browserVersion,
                   Optional<String> browserLocale,
                   Optional<Integer> startWindowWidth,
                   Optional<Integer> startWindowHeight) {

        this(baseTestUrl, timeouts, webDriverPath, browserBinaryPath, browserVersion, browserLocale,
                startWindowWidth, startWindowHeight,
                Optional.<Level>absent(), Optional.<String>absent());

    }

    public Browser(String baseTestUrl,
                   TimeoutsConfig timeouts,
                   Optional<String> webDriverPath,
                   Optional<String> browserBinaryPath,
                   Optional<String> browserVersion,
                   Optional<String> browserLocale,
                   Optional<Integer> startWindowWidth,
                   Optional<Integer> startWindowHeight,
                   Optional<Level> browserLogLevel,
                   Optional<String> browserLogFile) {

        this.baseTestUrl = Preconditions.checkNotNull(baseTestUrl);
        this.timeouts = timeouts;
        this.webDriverPath = webDriverPath;
        this.browserBinaryPath = browserBinaryPath;
        this.browserVersion = browserVersion;
        this.browserLocale = browserLocale;
        this.startWindowWidth = startWindowWidth;
        this.startWindowHeight = startWindowHeight;
        this.browserLogLevel = browserLogLevel;
        this.browserLogFile = browserLogFile;
    }

    /**
     * Initialize the browser. This creates a web driver instance, which opens the Browser to a blank page.
     * Resize the window to the configured values.
     *
     * @throws JiveWebDriverException
     */
    public void initializeBrowser() throws JiveWebDriverException {
        this.webDriver = createWebDriver();
        if (startWindowWidth.isPresent() && startWindowHeight.isPresent()) {
            this.webDriver.manage().window().setSize(new Dimension(startWindowWidth.get(), startWindowHeight.get()));
        }
        this.webDriver.manage().timeouts().pageLoadTimeout(getPageTimeoutSeconds(), TimeUnit.SECONDS);
        this.webDriver.manage().timeouts().implicitlyWait(getImplicitWaitTimeoutMillis(), TimeUnit.MILLISECONDS);
    }

    public abstract BrowserType getBrowserType();

    public abstract DesiredCapabilities getDesiredCapabilities();

    public abstract boolean isRemote();

    public abstract SeleniumActions getActions();

    protected abstract WebDriver createWebDriver() throws JiveWebDriverException;

    public LoggingPreferences getLoggingPreferences() {
        Level level = getLogLevel();
        LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.BROWSER, level);
        loggingPreferences.enable(LogType.CLIENT, level);
        loggingPreferences.enable(LogType.DRIVER, level);
        loggingPreferences.enable(LogType.SERVER, level);
        return loggingPreferences;
    }

    public void cleanSession() {
        webDriver.manage().deleteAllCookies();
    }

    public void quit() {
        webDriver.quit();
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public Optional<Integer> getStartWindowWidth() {
        return startWindowWidth;
    }

    public Optional<Integer> getStartWindowHeight() {
        return startWindowHeight;
    }

    public long getPageTimeoutSeconds() {
        return timeouts.getPageLoadTimeoutSeconds();
    }

    public long getImplicitWaitTimeoutMillis() {
        return timeouts.getImplicitWaitTimeoutMillis();
    }

    public String getBaseTestUrl() {
        return baseTestUrl;
    }

    public Optional<String> getWebDriverPath() {
        return webDriverPath;
    }

    public Optional<String> getBrowserBinaryPath() {
        return browserBinaryPath;
    }

    public Optional<String> getBrowserVersion() {
        return browserVersion;
    }

    public Optional<String> getBrowserLocale() {
        return browserLocale;
    }

    public Optional<Level> getBrowserLogLevel() {
        return browserLogLevel;
    }

    public Level getLogLevel() {
        return browserLogLevel.isPresent() ? browserLogLevel.get() : Level.WARNING;
    }

    public Optional<String> getBrowserLogFile() {
        return browserLogFile;
    }

    public TimeoutsConfig getTimeouts() {
        return timeouts;
    }

    /**
     * Opens a new page in the Browser by URL. An absolute URL or the path can be provided.
     * If a path is provided, then the baseTestUrl provided when creating the browser will be used as the
     * base of the URL.
     *
     * Invalidates the cached page and loads a fresh new page.
     *
     * @param href - the href from a link, which may be a relative path from baseTestUrl or may be absolute
     * @return - a generic {@link com.jivesoftware.selenium.pagefactory.framework.pages.BaseTopLevelPage}
     * page object. To open a page with more specific functionality, you must extend
     * {@link com.jivesoftware.selenium.pagefactory.framework.pages.BaseTopLevelPage} and then
     * call {@link #openPageByURL(String, Class)}.
     */
    public TopLevelPage openPageByURL(String href) throws URISyntaxException {
       return openPageByURL(href, BaseTopLevelPage.class);
    }

    /**
     * Opens a new page in the Browser by URL. An absolute URL or the path can be provided.
     *
     * Invalidates the cached page and loads a fresh new page.
     *
     * @param href - the href from a link, which may be a relative path from baseTestUrl or may be absolute
     * @param pageClass - the {@link com.jivesoftware.selenium.pagefactory.framework.pages.TopLevelPage} class to load.
     */
    public <T extends TopLevelPage> T openPageByURL(String href, Class<T> pageClass) throws URISyntaxException {
        URI uri = new URI(href);
        URI absoluteURI;
        if (uri.isAbsolute()) {
            absoluteURI = uri;
        } else {
            String fullURIStr = EndpointBuilder.uri(baseTestUrl, "/", href);
            absoluteURI = new URI(fullURIStr);
        }
        logger.info("Opening web page by URL {}", absoluteURI);
        runLeavePageHook();
        invalidateCachedPage();
        T page = PAGE_UTILS.loadPageFromURL(absoluteURI, pageClass, getWebDriver(), getActions());
        setCachedPage(page);
        return page;
    }

    /**
     * Invalidate cached page, and return a fresh TopLevelPage with newly initialized WebElements.
     *
     * This method does not do a Browser Refresh of the page.
     *
     * It does:
     * Invalidate the cache.
     * Initialize the current page again by loading webelements and running page load hooks
     *
     * @param pageClass - the class of the current Page
     */
    public <T extends TopLevelPage> T reloadTopLevelPage(Class<T> pageClass) {
        invalidateCachedPage();
        return loadTopLevelPage(pageClass);
    }

    /**
     * Load a sub page. No caching is used for {@link SubPage}'s.
     *
     * @param pageClass - the class of the SubPage that is currently present on the DOM in the browser to load.
     */
    public <T extends SubPage> T loadSubPage(Class<T> pageClass) {
        return PAGE_UTILS.loadCurrentPage(pageClass, webDriver, getActions());
    }

    /**
     * If the current page is still valid, and the URL hasn't changed, and the
     * class given as input is assignable from the cached page,
     * THEN return the cached page and avoid re-initializing web elements and running page hooks.
     * <p/>
     * Otherwise, invalidate the cache and load as normal.
     *
     * @param pageClass - the class of the current Page
     */
    public <T extends TopLevelPage> T loadTopLevelPage(Class<T> pageClass) {
        if (shouldUseCachedPage(pageClass)) {
            logger.info("CACHE HIT: Fetching page of type " + pageClass.getSimpleName() + " from the Page Cache");
            // This cast is safe, because we check in shouldUseCachedPage
            return (T) optionalCachedPage.get().getCachedPage();
        }
        logger.info("Loading page of type " + pageClass.getSimpleName());
        // If the page wasn't valid, then invalidate the cache.
        runLeavePageHook();
        invalidateCachedPage();
        T page = PAGE_UTILS.loadCurrentPage(pageClass, webDriver, getActions());
        setCachedPage(page);
        return page;
    }

    /**
     * @param pageClass - the class of the expected Page after refreshing.
     */
    public <T extends TopLevelPage> T refreshPage(Class<T> pageClass) {
        runLeavePageHook();
        invalidateCachedPage();
        webDriver.navigate().refresh();
        T page = loadTopLevelPage(pageClass);
        setCachedPage(page);
        return page;
    }

    /**
     * Refresh the current page, without giving back a newly initialized Page object.
     */
    public void refreshPage() {
        runLeavePageHook();
        webDriver.navigate().refresh();
        if (optionalCachedPage.isPresent()) {
            TopLevelPage cachedPage = optionalCachedPage.get().getCachedPage();
            cachedPage.refreshElements();
        }
    }

    /**
     * Save a screenshot in PNG format to given file name.
     *
     * @param filename
     * @return - a File representing the saved screenshot.
     */
    public File saveScreenshotToFile(String filename) {
        TakesScreenshot screenshotDriver;
        if (isRemote()) {
            screenshotDriver = (TakesScreenshot) new Augmenter().augment(getWebDriver());
        } else {
            screenshotDriver = ((TakesScreenshot) getWebDriver());
        }

        File scrFile = screenshotDriver.getScreenshotAs(OutputType.FILE);

        // Now you can do whatever you need to do with it, for example copy somewhere
        File outFile = new File(filename);
        try {
            FileUtils.copyFile(scrFile, outFile);
        } catch (IOException e) {
            logger.error("Error saving screenshot!", e);
        }
        return outFile;
    }

    public void invalidateCachedPage() {
        optionalCachedPage = Optional.absent();
    }

    //--------------Private helpers------------
    private void setCachedPage(TopLevelPage p) {
        final String url = webDriver.getCurrentUrl();
        CachedPage cachedPage = new CachedPage(url, p);
        optionalCachedPage = Optional.of(cachedPage);
        logger.debug("Set cached page of type {} with URL {}", p.getClass().getSimpleName(), url);
    }

    private <T extends TopLevelPage> boolean shouldUseCachedPage(Class<T> pageClass) {
        if (!optionalCachedPage.isPresent()) {
            return false;
        }
        CachedPage cachedPage = optionalCachedPage.get();

        // The cached page must be an instance of the required page class.
        if (!pageClass.isInstance(cachedPage.getCachedPage())) {
            return false;
        }

        try {
            URI currentURI = URI.create(webDriver.getCurrentUrl());
            URI cachedURI = URI.create(cachedPage.getUrl());

            // Hosts must be equal
            if (!Objects.equals(currentURI.getHost(), cachedURI.getHost())) {
                return false;
            }

            // Paths must be equal
            if (!Objects.equals(currentURI.getPath(), cachedURI.getPath())) {
                return false;
            }

        } catch (Exception e) {
            logger.debug("Error constructing URIs from the current webdriver URL", e);
            return false;
        }

        return true;
    }

    public void runLeavePageHook() {
        if (optionalCachedPage.isPresent()) {
            optionalCachedPage.get().getCachedPage().leavePageHook();
        }
    }

    @Nullable
    public abstract LogEntries getBrowserLogEntries();
}
