package com.jivesoftware.selenium.pagefactory.framework.actions;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.jivesoftware.selenium.pagefactory.framework.browser.Browser;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutType;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import com.jivesoftware.selenium.pagefactory.framework.exception.SeleniumActionsException;
import com.jivesoftware.selenium.pagefactory.framework.pages.BaseTopLevelPage;
import com.jivesoftware.selenium.pagefactory.framework.pages.SubPage;
import com.jivesoftware.selenium.pagefactory.framework.pages.TopLevelPage;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 * Default implementations of Selenium actions that aren't browser-specific.
 */
public abstract class BaseSeleniumActions implements SeleniumActions {
    private static final long DEFAULT_POLL_MILLIS = 100;

    protected Browser browser;
    protected final TimeoutsConfig timeoutsConfig;
    protected static Logger logger = LoggerFactory.getLogger(BaseSeleniumActions.class);

    public BaseSeleniumActions(Browser browser) {
        this.browser = Preconditions.checkNotNull(browser, "Error: you must supply a non-null Browser to BaseSeleniumActions!");
        this.timeoutsConfig = Preconditions.checkNotNull(browser.getTimeouts(), "Error: you must supply a non-null Timeouts to BaseSeleniumActions!");
    }

    //Convenience method to reduce typing
    private WebDriver webDriver() {
        return browser.getWebDriver();
    }

    @Override
    public Browser getBrowser() {
        return browser;
    }

    @Override
    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    @Override
    public Actions getActionsBuilder() {
        return new Actions(webDriver());
    }

    @Override
    public void acceptAlert(TimeoutType timeout) {
        waitOnExpectedCondition(ExpectedConditions.alertIsPresent(),
                "Waiting for javascript alert to be present before accepting alert.", timeout);
        webDriver().switchTo().alert().accept();
    }

    @Override
    public void dismissAlert(TimeoutType timeout) {
        waitOnExpectedCondition(ExpectedConditions.alertIsPresent(),
                "Waiting for javascript alert to be present before dismissing alert.", timeout);
        webDriver().switchTo().alert().dismiss();
    }

    @Override
    public WebElement clearText(String css) {
        WebElement el = verifyElementPresented(css, TimeoutType.DEFAULT);
        try {
            el.clear();
        } catch (Exception e) {
            throw new RuntimeException(format("Error clearing text from element with CSS '%s': %s", css, e.getMessage()), e);
        }
        logger.info("Cleared text from element with CSS '{}'", css);
        return el;
    }

    @Override
    public WebElement clearText(@Nonnull WebElement el) {
        String tag = el.getTagName();
        try {
            el.clear();
        } catch (Exception e) {
            throw new RuntimeException(format("Error clearing text from element <%s>: %s", tag, e.getMessage()), e);
        }
        logger.info("Cleared text from element <{}>", tag);
        return el;
    }

    @Override
    public WebElement clickNoWait(String css) throws JiveWebDriverException {
        WebElement el = getElement(css);
        if (!isClickable(el)) {
            throw new JiveWebDriverException("Element is not clickable: " + css);
        }
        el.click();
        logger.info("Clicked element with CSS '{}', no waiting.", css);
        return el;
    }

    @Override
    public WebElement click(String css, TimeoutType timeout) {
        WebElement el = waitUntilClickable(css, timeout);
        el.click();
        logger.info("Clicked element with CSS '{}'", css);
        return el;
    }

    @Override
    public WebElement click(WebElement el, TimeoutType timeout) {
        waitUntilClickable(el, timeout);
        String tag = el.getTagName();
        el.click();
        logger.info("Clicked element <{}>", tag);
        return el;
    }

    @Override
    public WebElement clickAndVerifyPresent(String cssToClick, String cssToVerifyPresent, TimeoutType timeout) {
        click(cssToClick, timeout);
        logger.info("After click, waiting for '{}' to be present.", cssToVerifyPresent);
        int waitSeconds = getTimeout(timeoutsConfig.getWebElementPresenceTimeoutSeconds(), timeout);
        final String errorMessage = format("Failure in clickAndVerifyPresent: element '%s' never became present after %d seconds!",
                cssToVerifyPresent, waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage)
            .ignoring(StaleElementReferenceException.class);
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssToVerifyPresent)));
    }

    @Override
    public WebElement clickAndVerifyPresent(WebElement el, String cssToVerifyPresent, TimeoutType timeout) {
        click(el, timeout);
        logger.info("After click, waiting for '{}' to be present.", cssToVerifyPresent);
        int waitSeconds = getTimeout(timeoutsConfig.getWebElementPresenceTimeoutSeconds(), timeout);
        final String errorMessage = format("Failure in clickAndVerifyPresent: element '%s' never became present after %d seconds!",
                cssToVerifyPresent, waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage)
            .ignoring(StaleElementReferenceException.class);
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssToVerifyPresent)));
    }

    @Override
    public WebElement clickAndVerifyVisible(String cssToClick, String cssToVerifyVisible, TimeoutType timeout) {
        click(cssToClick, timeout);
        logger.info("After click, waiting for '{}' to be visible.", cssToVerifyVisible);
        return verifyElementVisible(cssToVerifyVisible, timeout);
    }

    @Override
    public WebElement clickAndVerifyVisible(WebElement el, String cssToVerifyVisible, TimeoutType timeout) {
        click(el, timeout);
        logger.info("After click, waiting for '{}' to be visible.", cssToVerifyVisible);
        return verifyElementVisible(cssToVerifyVisible, timeout);
    }

    @Override
    public <T extends TopLevelPage> T clickAndLoadTopLevelPage(String cssToClick, Class<T> pageClass, TimeoutType timeout) {
        click(cssToClick, TimeoutType.DEFAULT);
        return loadTopLevelPage(pageClass);
    }

    @Override
    public <T extends TopLevelPage> T clickAndLoadTopLevelPage(WebElement el, Class<T> pageClass, TimeoutType timeout) {
        click(el, TimeoutType.DEFAULT);
        return loadTopLevelPage(pageClass);
    }

    @Override
    public <T extends SubPage> T clickAndLoadSubPage(String cssToClick, Class<T> pageClass, TimeoutType timeout) {
        click(cssToClick, TimeoutType.DEFAULT);
        return loadSubPage(pageClass);
    }

    @Override
    public <T extends SubPage> T clickAndLoadSubPage(WebElement el, Class<T> pageClass, TimeoutType timeout) {
        click(el, TimeoutType.DEFAULT);
        return loadSubPage(pageClass);
    }

    @Override
    public void clickAndVerifyNotPresent(String cssToClick, String cssToVerifyNotPresent, TimeoutType timeout) {
        click(cssToClick, timeout);
        logger.info("After click, waiting for '{}' to NOT be present.", cssToVerifyNotPresent);
        int waitSeconds = getTimeout(timeoutsConfig.getWebElementPresenceTimeoutSeconds(), timeout);
        final String errorMessage = format("Failure in clickAndVerifyNotPresent: element '%s' never became removed from the DOM after %d seconds!",
                cssToVerifyNotPresent, waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage)
            .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.not(
                ExpectedConditions.presenceOfAllElementsLocatedBy((By.cssSelector(cssToVerifyNotPresent)))));
    }

    @Override
    public void clickAndVerifyNotPresent(WebElement el, String cssToVerifyNotPresent, TimeoutType timeout) {
        click(el, timeout);
        logger.info("After click, waiting for '{}' to NOT be present.", cssToVerifyNotPresent);
        int waitSeconds = getTimeout(timeoutsConfig.getWebElementPresenceTimeoutSeconds(), timeout);
        final String errorMessage = format("Failure in clickAndVerifyNotPresent: element '%s' never became removed from the DOM after %d seconds!",
                cssToVerifyNotPresent, waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage)
            .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.not(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(cssToVerifyNotPresent))));
    }

    @Override
    public void clickAndVerifyNotVisible(String cssToClick, String cssToVerifyNotVisible, TimeoutType timeout) {
        click(cssToClick, timeout);
        logger.info("After click, waiting for '{}' to NOT be visible.", cssToVerifyNotVisible);
        verifyElementInvisible(cssToVerifyNotVisible, timeout);
    }

    @Override
    public void clickAndVerifyNotVisible(WebElement el, String cssToVerifyNotVisible, TimeoutType timeout) {
        click(el, timeout);
        logger.info("After click, waiting for '{}' to NOT be visible.", cssToVerifyNotVisible);
        verifyElementInvisible(cssToVerifyNotVisible, timeout);
    }

    @Override
    public void clickAndSelectFromList(String cssToClick, String popoverCSS) {
        invokeMenuItemAndSelect(getElement(cssToClick), popoverCSS);
    }

    @Override
    public void clickAndSelectFromList(WebElement clickable, String popoverCSS) {
        invokeMenuItemAndSelect(clickable, popoverCSS);
    }

    @Override
    public boolean doesElementHaveClass(String css, String cssClass) {
        WebElement el = verifyElementPresented(css, TimeoutType.DEFAULT);
        return WebElementHelpers.webElementHasClass(el, cssClass);
    }

    @Override
    public Object executeJavascript(String script) {
        logger.trace("Executing javascript: '{}'", script);
        try {
            return ((JavascriptExecutor) webDriver()).executeScript(script);
        } catch (Exception e) {
            throw new RuntimeException(format("Exception executing Javascript '%s':", script), e);
        }
    }

    @Override
    public void waitForJavascriptSymbolToBeDefined(final String symbol, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getPageLoadTimeoutSeconds(), timeout);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds, DEFAULT_POLL_MILLIS); //Check every 100ms
        wait.ignoring(StaleElementReferenceException.class);
        try {
            wait.until(new ExpectedCondition<Object>() {
                @Nullable
                @Override
                public Object apply(@Nullable WebDriver input) {
                    Object jsResult = executeJavascript(format("return (typeof %s != 'undefined') && (%s != null)", symbol, symbol));
                    logger.trace("javascript result: " + jsResult);
                    return jsResult;
                }
            });
        } catch (TimeoutException e) {
            throw new RuntimeException(
                    format("Timeout waiting for javascript symbol '%s' to be defined with %d seconds timeout used", symbol, waitSeconds), e);
        }
        logger.info("Success verifying javascript symbol '{}' is defined!", symbol);
    }

    @Override
    public void waitForJavascriptSymbolToHaveValue(final String symbol, final String value, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getPageLoadTimeoutSeconds(), timeout);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds, DEFAULT_POLL_MILLIS); //Check every 100ms
        wait.ignoring(StaleElementReferenceException.class);
        try {
            wait.until(new ExpectedCondition<Object>() {
                @Nullable
                @Override
                public Object apply(@Nullable WebDriver input) {
                    Object jsResult = executeJavascript(format("return (%s) === (%s)", symbol, value));
                    logger.trace("javascript result: " + jsResult);
                    return jsResult;
                }
            });
        } catch (TimeoutException e) {
            throw new RuntimeException(
                    format("Timeout waiting for javascript symbol '%s' to have value '%s' with %d seconds timeout used", symbol, value, waitSeconds), e);
        }
        logger.info("Success verifying javascript symbol '{}' has value '{}'!", symbol, value);
    }


    /**
     * According to Selenium Javadoc, this is the correct way to check for existence of an element.
     */
    @Override
    public boolean exists(String css) {
        List<WebElement> elements = findElements(css, null);
        return elements.size() > 0;
    }

    @Override
    public boolean exists(String css, WebElement parentEl) {
        List<WebElement> elements = findElements(css, parentEl);
        return elements.size() > 0;
    }

    @Override
    public WebElement findElementContainingText(String css, String text) {
        List<WebElement> matches = findElements(css, null);
        for (WebElement el : matches) {
            try {
                if (el.getText().contains(text)) {
                    logger.info("SUCCESS: Found web element containing text '{}' with CSS '{}'", text, css);
                    return el;
                }
            } catch (Exception e) { //Don't fail just because one web element was stale. Continue searching for the text.
                logger.debug("Exception while searching for web elements containing text '{}' with CSS '{}'", text, css);
                logger.debug(Throwables.getStackTraceAsString(e));
            }
        }
        return null;
    }

    @Override
    public WebElement findVisibleElementContainingText(String css, String text) {
        List<WebElement> matches = findElements(css, null);
        for (WebElement el : matches) {
            try {
                if (el.getText().contains(text) && el.isDisplayed()) {
                    logger.info("SUCCESS: Found visible web element containing text '{}' with CSS '{}'", text, css);
                    return el;
                }
            } catch (Exception e) { //Don't fail just because one web element was stale. Continue searching for the text.
                logger.debug("Exception while searching for web elements containing text '{}' with CSS '{}'", text, css);
                logger.debug(Throwables.getStackTraceAsString(e));
            }
        }
        return null;
    }

    @Override
    public WebElement findVisibleElementContainingTextWithWait(final String css, final String text, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getWebElementPresenceTimeoutSeconds(), timeout);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.ignoring(StaleElementReferenceException.class);

        return wait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(@Nullable WebDriver input) {
                WebElement el = findVisibleElementContainingText(css, text);
                if (el == null) {
                    GeneralUtils.waitOneSecond();
                }
                return el;
            }
        });
    }

    @Override
    public WebElement findElementWithRefresh(final String css, TimeoutType timeout) {
        return findElementContainingTextWithRefresh(css, "", timeout);
    }

    @Override
    public WebElement findElementContainingTextWithRefresh(final String css, final String text, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getPollingWithRefreshTimeoutSeconds(), timeout);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.ignoring(StaleElementReferenceException.class);

        logger.info("Waiting for element containing text '{}' defined by css '{}', timeout of {} seconds", new Object[]{text, css, waitSeconds});
        try {
            WebElement found = wait.until(new ExpectedCondition<WebElement>() {
                @Override
                public WebElement apply(@Nullable WebDriver input) {
                    long start = new Date().getTime();
                    while ((new Date().getTime() - start) / 1000 < timeoutsConfig.getPauseBetweenRefreshSeconds()) {
                        WebElement el = findElementContainingText(css, text);
                        if (el != null) {
                            return el;
                        }
                        GeneralUtils.waitMillis(timeoutsConfig.getPauseBetweenTriesMillis());
                    }
                    getBrowser().refreshPage();
                    return null;
                }
            });
            logger.info("Success finding element containing text '{}' defined by css '{}'!", text, css);
            return found;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting to find text '{}' in an element matching css '{}'", text, css);
            throw new TimeoutException(
                    format("Timeout waiting to find text '%s' in an element matching css '%s'", text, css));
        }
    }

    @Override
    public WebElement findVisibleElementWithRefresh(final String css, TimeoutType timeout) {
        return findVisibleElementContainingTextWithRefresh(css, "", timeout);
    }

    @Override
    public WebElement findVisibleElementContainingTextWithRefresh(final String css, final String text, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getPollingWithRefreshTimeoutSeconds(), timeout);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.ignoring(StaleElementReferenceException.class);

        logger.info("Waiting for element containing text '{}' defined by css '{}', timeout of {} seconds", new Object[]{text, css, waitSeconds});
        try {
            WebElement found = wait.until(new ExpectedCondition<WebElement>() {
                @Override
                public WebElement apply(@Nullable WebDriver input) {
                    long start = new Date().getTime();
                    while ((new Date().getTime() - start) / 1000 < timeoutsConfig.getPauseBetweenRefreshSeconds()) {
                        WebElement el = findVisibleElementContainingText(css, text);
                        if (el != null) {
                            return el;
                        }
                        GeneralUtils.waitMillis(timeoutsConfig.getPauseBetweenTriesMillis());
                    }
                    getBrowser().refreshPage();
                    return null;
                }
            });
            logger.info("Success finding element containing text '{}' defined by css '{}'!", text, css);
            return found;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting to find text '{}' in an element matching css '{}'", text, css);
            throw new TimeoutException(
                    format("Timeout waiting to find text '%s' in an element matching css '%s'", text, css));
        }
    }

    @Override
    public WebElement findElementContainingTextWithWait(final String css, final String text, TimeoutType timeout) {
        final int waitSeconds = getTimeout(timeoutsConfig.getWebElementPresenceTimeoutSeconds(), timeout);
        final String msg = format("Failure in findElementContainingTextWithWait: never found text '%s' in element " +
                                  "with css '%s' with timeout of %d seconds", text, css, waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.ignoring(StaleElementReferenceException.class)
            .withMessage(msg);

        return wait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(@Nullable WebDriver input) {
                WebElement el = findElementContainingText(css, text);
                if (el == null) {
                    GeneralUtils.waitOneSecond();
                }
                return el;
            }
        });
    }

    @Override
    @Nullable
    public WebElement getChildElement(String css, WebElement parentEl) {
        List<WebElement> elements = findElements(css, parentEl);
        if (elements.size() > 0) {
            return elements.get(0);
        }
        return null;
    }

    @Override
    @Nullable
    public WebElement getElement(String css) {
        List<WebElement> elements = findElements(css, null);
        if (elements.size() > 0) {
            return elements.get(0);
        }
        return null;
    }

    @Override
    @Nonnull
    public List<WebElement> getChildElements(String css, WebElement parentEl) {
        return findElements(css, parentEl);
    }

    @Override
    @Nonnull
    public List<WebElement> getElements(String css) {
        return findElements(css, null);
    }

    @Override
    @Nonnull
    public WebElement getElementWithWait(String css) {
        return getChildElementWithWait(css, null);
    }


    @Override
    @Nonnull
    public WebElement getChildElementWithWait(String css, WebElement parentEl) {
        try {
            WebElement el = findElement(css, parentEl);
            logger.trace("Successfully found web element by CSS '{}'", css);
            return el;
        } catch (NoSuchElementException e) {
            long implicitWait = browser.getImplicitWaitTimeoutMillis();
            throw new RuntimeException(
                    format("Timeout using implicit wait of %d ms waiting to find web element with CSS '%s' ", implicitWait, css));
        }
    }

    @Override
    @Nonnull
    public WebElement getParentElement(WebElement el) {
        return el.findElement(By.xpath(".."));
    }

    @Override
    @Nonnull
    public WebElement inputText(String css, String text) {
        logger.info("Inputting text '{}' into element with CSS '{}'", text, css);
        WebElement el = getElementWithWait(css);
        try {
            el.sendKeys(text);
        } catch (Exception e) {
            throw new RuntimeException(format("Error inputting text '%s' into element with CSS '%s': %s", text, css, e.getMessage()), e);
        }
        return el;
    }

    @Override
    @Nonnull
    public WebElement inputText(@Nonnull WebElement el, String text) {
        logger.info("Inputting text '{}' into web element <{}>", text, el.getTagName());
        try {
            el.sendKeys(text);
        } catch (Exception e) {
            throw new RuntimeException(format("Error inputting text '%s' into element <%s>: %s", text, el.getTagName(), e.getMessage()), e);
        }
        return el;
    }

    @Override
    public WebElement inputTextAndSelectFromList(WebElement inputField, String value, String popoverCSS) throws SeleniumActionsException {
        return inputTextAndSelectFromList(inputField, value, popoverCSS, 0);         // default is no retries
    }

    @Override
    public WebElement inputTextAndSelectFromList(WebElement inputField, String value, String popoverCSS,
                                                 int withRetryCount) throws SeleniumActionsException {
        return enterTextAndSelectFromList(inputField, value, popoverCSS, withRetryCount, false);
    }

    @Override
    public WebElement inputTextSlowly(String css, String text) {
        WebElement el = getElementWithWait(css);
        logger.info("Inputting text '{}' into web element with CSS '{}'", text, css);
        return inputTextSlowly(el, text);
    }

    @Override
    public WebElement inputTextSlowly(@Nonnull WebElement el, String text) {
        logger.info("Inputting text {} slowly into web element {}", text, el.getTagName());
        for (Character c : text.toCharArray()) {
            el.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(timeoutsConfig.getPauseBetweenKeysMillis());
            } catch (InterruptedException e) {
                // don't care
            }
        }
        return el;
    }

    @Override
    public WebElement inputTextSlowlyAndSelectFromList(WebElement inputField, String value, String popoverCSS) throws SeleniumActionsException {
        return inputTextSlowlyAndSelectFromList(inputField, value, popoverCSS, 0);      // default is no retries
    }

    @Override
    public WebElement inputTextSlowlyAndSelectFromList(WebElement inputField, String value, String popoverCSS,
                                                       int withRetryCount) throws SeleniumActionsException {
        return enterTextAndSelectFromList(inputField, value, popoverCSS, withRetryCount, true);
    }

    @Override
    public void enterTextForAutoCompleteAndSelectFirstMatch(String inputCSS, String text, String popupItemCss,
                                                            String requiredPopupText) {
        enterTextForAutoCompleteAndSelectFirstMatch(inputCSS, 0, text, popupItemCss, requiredPopupText);
    }

    @Override
    public void enterTextForAutoCompleteAndSelectFirstMatch(String inputCSS, int minChars, String text, String popupItemCss,
                                                            String requiredPopupText) {
        if (minChars > text.length()) {
            throw new RuntimeException(format("Minimum characters to enter (%d) is greater than the length of the input text '%s'!", minChars, text));
        }
        scrollIntoView(inputCSS);
        if (minChars > 0) {
            inputText(inputCSS, text.substring(0, minChars));
        }
        for (int i = minChars; i < text.length(); i++) {
            String oneChar = String.valueOf(text.charAt(i));
            inputText(inputCSS, oneChar);

            // If the last char is being entered, wait 5 full seconds for the expected popup. Otherwise, wait 1 second.
            TimeoutType timeout = (i == text.length() - 1) ? TimeoutType.FIVE_SECONDS : TimeoutType.ONE_SECOND;
            try {
                WebElement matchingPopup = findElementContainingTextWithWait(popupItemCss, requiredPopupText, timeout);
                if (matchingPopup != null) {
                    try {
                        getActionsBuilder().moveToElement(matchingPopup)
                                .pause(500) // Sometimes javascript needs a moment to register that it's being hovered.
                                .click()
                                .perform();
                        logger.info("Success - clicked popup for autocomplete text \"{}\"", text);
                        return;
                    } catch (Exception e) {
                        logger.debug("Exception clicking popup from autocomplete.", e);
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
        throw new RuntimeException(format("No popup defined by CSS '%s' found with required text '%s'", popupItemCss, requiredPopupText));
    }

    @Override
    public void inputTinyMceText(String text) {
        waitForTinyMceToBeReady();
        ((JavascriptExecutor) webDriver()).executeScript(format("tinyMCE.activeEditor.setContent(\"%s\")", text));
    }

    @Override
    public void waitForTinyMceToBeReady() {
        waitForJavascriptSymbolToBeDefined("tinyMCE", TimeoutType.DEFAULT);
        waitForJavascriptSymbolToBeDefined("tinyMCE.activeEditor", TimeoutType.DEFAULT);
        waitForJavascriptSymbolToHaveValue("tinyMCE.activeEditor.initialized", "true", TimeoutType.DEFAULT);
    }

    @Override
    public boolean isClickable(String css) {
        WebElement el = getElement(css);
        if (el == null) {
            return false;
        }
        return isClickable(el);
    }

    /**
     * Conditions according to selenium Javadoc for an element to be clickable
     */
    @Override
    public boolean isClickable(WebElement el) {
        if (el == null) {
            return false;
        }
        try {
            if (!el.isDisplayed()) { //If not visible, element isn't clickable
                return false;
            }
            if (el.getSize().getHeight() <= 0 || el.getSize().getWidth() <= 0) { // If width or height is 0, element is not clickable
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isVisible(String css) {
        WebElement el = getElement(css);
        return isVisible(el);
    }

    @Override
    public boolean isVisible(WebElement el) {
        if (el == null) {
            return false;
        }
        return el.isDisplayed() && el.getSize().getHeight() > 0 && el.getSize().getWidth() > 0;
    }

    @Override
    public void scrollIntoView(String css) {
        WebElement el = verifyElementPresented(css, TimeoutType.DEFAULT);
        scrollIntoView(el);
    }

    @Override
    public void scrollIntoView(WebElement el) {
        int scrollHeight = webDriver().manage().window().getSize().getHeight();
        int y = Math.max(0, el.getLocation().getY() - scrollHeight / 2); //Subtract half the window height so its in the middle of the viewable area.
        executeJavascript(format("window.scrollTo(%d, %d)", 0, y));
    }

    @Override
    public void scrollIntoView(String scrollContainerCSS, String css) {
        WebElement parent = verifyElementPresented(scrollContainerCSS, TimeoutType.DEFAULT);
        WebElement el = verifyElementPresented(css, TimeoutType.DEFAULT);
        int currentScrollTop = ((Long) executeJavascript(format("return $('%s').scrollTop()", scrollContainerCSS))).intValue();
        int y = el.getLocation().getY();
        int parentY = parent.getLocation().getY();
        int scrollTo = Math.max(0, y - parentY + currentScrollTop);
        executeJavascript(format("$('%s').scrollTop(%d)", scrollContainerCSS, scrollTo));
    }

    @Override
    public void scrollIntoView(String scrollContainerCSS, WebElement el) {
        WebElement parent = verifyElementPresented(scrollContainerCSS, TimeoutType.DEFAULT);
        int currentScrollTop = ((Long) executeJavascript(format("return $('%s').scrollTop()", scrollContainerCSS))).intValue();
        int y = el.getLocation().getY();
        int parentY = parent.getLocation().getY();
        int scrollTo = Math.max(0, y - parentY + currentScrollTop);
        executeJavascript(format("$('%s').scrollTop(%d)", scrollContainerCSS, scrollTo));
    }

    @Override
    public void openWebPage(URI uri) {
        try {
            browser.openPageByURL(uri.toString(), BaseTopLevelPage.class);
        } catch (URISyntaxException e) {
            // This failure mode should be impossible, since we passed in a URI. 
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T extends TopLevelPage> T loadTopLevelPage(Class<T> pageClass) {
        return browser.loadTopLevelPage(pageClass);
    }

    @Override
    public <T extends SubPage> T loadSubPage(Class<T> pageClass) {
        return browser.loadSubPage(pageClass);
    }

    @Override
    public void verifyElementContainsText(final String css, final String text, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getWebElementPresenceTimeoutSeconds(), timeout);
        final String errorMessage = format("Failure in verifyElementContainsText: an element with CSS '%s' was never found containing text '%s'!",
                css, text);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage)
            .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(css), text));
        logger.info("SUCCESS: Verified element with CSS '{}' contains text '{}'", css, text);
    }

    @Override
    public WebElement verifyElementHasClass(final String css, final String cssClass, TimeoutType timeout) {
        return waitOnFunction(new Function<SeleniumActions, WebElement>() {
                                  @Nullable
                                  @Override
                                  public WebElement apply(SeleniumActions input) {
                                      WebElement el = input.verifyElementPresented(css, TimeoutType.DEFAULT);
                                      if (WebElementHelpers.webElementHasClass(el, cssClass)) {
                                          return el;
                                      }
                                      return null;
                                  }
                              }, this,
                format("Waiting for element with css '%s' to have css class '%s'", css, cssClass),
                timeout);
    }

    @Override
    public WebElement verifyElementDoesNotHaveClass(final String css, final String cssClass, TimeoutType timeout) {
        return waitOnFunction(new Function<SeleniumActions, WebElement>() {
                                  @Nullable
                                  @Override
                                  public WebElement apply(SeleniumActions input) {
                                      WebElement el = input.verifyElementPresented(css, TimeoutType.DEFAULT);
                                      if (!WebElementHelpers.webElementHasClass(el, cssClass)) {
                                          return el;
                                      }
                                      return null;
                                  }
                              }, this,
                format("Waiting for element with css '%s' to NOT have css class '%s'", css, cssClass),
                timeout);
    }

    @Override
    public WebElement verifyElementPresented(String css, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getWebElementPresenceTimeoutSeconds(), timeout);
        final String errorMessage = format("Failure in verifyElementPresented: element '%s' never became presented after %d seconds!",
                css, waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage)
            .ignoring(StaleElementReferenceException.class);
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(css)));
        logger.trace("SUCCESS: Verified element with CSS '{}' is present", css);
        return el;
    }

    @Override
    public void verifyElementNotPresented(String css, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getWebElementPresenceTimeoutSeconds(), timeout);
        final String errorMessage = format("Failure in verifyElementNotPresented: element '%s' never became not presented after %d seconds!",
                css, waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage)
            .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(css)));
        logger.trace("SUCCESS: Verified element with CSS '{}' is NOT present", css);
    }

    @Override
    public void verifyElementWithTextNotPresented(String css, String text, TimeoutType timeout) {
        try {
            findElementContainingTextWithWait(css, text, timeout);
            throw new RuntimeException(
                    format("Error in verifyElementWithTextNotPresented: found element with css '%s' containing text '%s'!", css, text));
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void verifyElementSelected(String css, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getClickTimeoutSeconds(), timeout);
        final String errorMessage = format("Failure in verifyElementSelected: Element '%s' never became selected after %d seconds!",
                css, waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage)
            .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.elementToBeSelected(By.cssSelector(css)));
    }

    @Override
    public void verifyElementSelected(WebElement el, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getClickTimeoutSeconds(), timeout);
        final String errorMessage = format("Failure in verifyElementSelected: Element '%s' never became selected after %d seconds!",
                el.getTagName(), waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage);
        wait.until(ExpectedConditions.elementToBeSelected(el));
        logger.info("SUCCESS: Verified element <{}> is selected", el.getTagName());
    }

    @Override
    public void verifyElementNotSelected(String css, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getClickTimeoutSeconds(), timeout);
        final String errorMessage = format("Failure in verifyElementNotSelected: Element '%s' never became deselected after %d seconds!",
                css, waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage)
            .ignoring(StaleElementReferenceException.class);
        wait.until(ExpectedConditions.elementSelectionStateToBe(By.cssSelector(css), false));
        logger.info("SUCCESS: Verified element with CSS '{}' is NOT selected", css);
    }

    @Override
    public void verifyElementNotSelected(WebElement el, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getClickTimeoutSeconds(), timeout);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.until(ExpectedConditions.elementSelectionStateToBe(el, false));
        logger.info("SUCCESS: Verified element <{}> is NOT selected", el.getTagName());
    }

    @Override
    public WebElement verifyElementVisible(final String css, TimeoutType timeout) {
        final String errorMessage = format("Error in verifyElementVisible: element with css '%s' never became visible", css);
        return waitOnExpectedCondition(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(css)), errorMessage, timeout);
    }

    @Override
    public void verifyElementInvisible(String css, TimeoutType timeout) {
        waitOnExpectedCondition(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(css)),
                format("Failure in verifyElementInvisible waiting for element with css '%s' to be invisible", css), timeout);
    }

    @Override
    public void verifyElementWithTextIsInvisible(String css, String text, TimeoutType timeout) {
        try {
            WebElement visibleEl = findVisibleElementContainingTextWithWait(css, text, timeout);
            throw new RuntimeException(
                    format("Error in verifyElementWithTextIsInvisible: found element by css '%s' containing text '%s'", css, text));
        } catch (Exception e) {
            return; // OK - we didn't find a visible element containing the given text
        }
    }

    @Override
    public WebElement verifyPageRefreshed(WebElement elementFromBeforeRefresh, String cssAfterRefresh, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getPageRefreshTimeoutSeconds(), timeout);
        logger.info("Waiting for CSS '{}' to be present after page refreshes, using timeout of {} seconds", cssAfterRefresh, waitSeconds);
        waitOnExpectedConditionForSeconds(ExpectedConditions.stalenessOf(elementFromBeforeRefresh),
                "Timeout waiting for web element to become stale (waiting for page to reload).",
                waitSeconds);
        logger.info("Verified web element became stale (page is reloading).");
        WebElement el = verifyElementPresented(cssAfterRefresh, TimeoutType.DEFAULT);
        logger.info("Successfully verified page refreshed by finding web element with CSS '{}'.", cssAfterRefresh);

        return el;
    }

    @Override
    public <T, V> V waitOnFunction(Function<T, V> function, T input, String message, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getMediumTimeoutSeconds(), timeout);
        FluentWait<T> fluentWait = new FluentWait<T>(input)
                .withTimeout(waitSeconds, TimeUnit.SECONDS)
                .pollingEvery(DEFAULT_POLL_MILLIS, TimeUnit.MILLISECONDS)
                .withMessage(message)
                .ignoring(NotFoundException.class)
                .ignoring(StaleElementReferenceException.class);
        return fluentWait.until(function);
    }

    @Override
    public <T> void waitOnPredicate(Predicate<T> predicate, T input, String message, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getMediumTimeoutSeconds(), timeout);
        FluentWait<T> fluentWait = new FluentWait<T>(input)
                .withTimeout(waitSeconds, TimeUnit.SECONDS)
                .pollingEvery(DEFAULT_POLL_MILLIS, TimeUnit.MILLISECONDS)
                .withMessage(message)
                .ignoring(NotFoundException.class)
                .ignoring(StaleElementReferenceException.class);
        fluentWait.until(predicate);
    }

    @Override
    public void waitOnPredicate(Predicate predicate, String message, TimeoutType timeout) {
        waitOnPredicate(predicate, new Object(), message, timeout);
    }

    @Override
    public <T> void waitOnPredicateWithRefresh(final Predicate<T> predicate, final T input, String message, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getMediumTimeoutSeconds(), timeout);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds, DEFAULT_POLL_MILLIS);
        wait.withMessage(message)
            .ignoring(StaleElementReferenceException.class);

        logger.info("Waiting on expected condition, using timeout of {} seconds", waitSeconds);
        wait.until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(@Nullable WebDriver webDriver) {
                if (predicate.apply(input)) {
                    return true;
                }
                browser.refreshPage(BaseTopLevelPage.class);
                return false;
            }
        });
    }

    @Override
    public void waitOnPredicateWithRefresh(final Predicate predicate, String message, TimeoutType timeout) {
        waitOnPredicateWithRefresh(predicate, new Object(), message, timeout);
    }

    @Override
    public <T> T waitOnExpectedCondition(ExpectedCondition<T> expectedCondition, String message, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getWebElementPresenceTimeoutSeconds(), timeout); //Default of web element presence timeout
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds, DEFAULT_POLL_MILLIS);
        wait.withMessage(message)
            .ignoring(StaleElementReferenceException.class);
        logger.info("Waiting on expected condition, using timeout of {} seconds", waitSeconds);
        return wait.until(expectedCondition);
    }

    private <T> T waitOnExpectedConditionForSeconds(ExpectedCondition<T> expectedCondition, String message, int timeout) {
        WebDriverWait wait = new WebDriverWait(webDriver(), timeout, DEFAULT_POLL_MILLIS);
        wait.withMessage(message)
            .ignoring(StaleElementReferenceException.class);
        logger.info("Waiting on expected condition, using timeout of {} seconds", timeout);
        return wait.until(expectedCondition);
    }

    @Override
    public WebElement waitUntilClickable(String css, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getClickTimeoutSeconds(), timeout);
        final String errorMessage = format("Element '%s' never became clickable after '%d' seconds", css, waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(errorMessage)
            .ignoring(StaleElementReferenceException.class);
        logger.info("Waiting for CSS element '{}' to be clickable, using timeout of {} seconds", css, waitSeconds);
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(css)));
        return el;
    }

    @Override
    public WebElement waitUntilClickable(final WebElement el, TimeoutType timeout) {
        int waitSeconds = getTimeout(timeoutsConfig.getClickTimeoutSeconds(), timeout);
        final String message = format("Element never became clickable after '%d' seconds", waitSeconds);
        WebDriverWait wait = new WebDriverWait(webDriver(), waitSeconds);
        wait.withMessage(message)
            .ignoring(StaleElementReferenceException.class);
        wait.until(new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver webDriver) {
                if (isClickable(el)) {
                    return el;
                }
                return null;
            }
        });
        return el;
    }

    //////////////////////////////////////Timeouts//////////////////////////////////////////////
    @Override
    public TimeoutsConfig getTimeoutsConfig() {
        return timeoutsConfig;
    }

    protected int getTimeout(int defaultTimeout, TimeoutType timeout) {
        return timeout == TimeoutType.DEFAULT ? defaultTimeout : timeoutsConfig.getTimeoutInSeconds(timeout);
    }

    //////////////////////////////////// helpers ////////////////////////////////////////////////

    /**
     * Convenient helper to Find Elements from either top page element or from parent element.
     *
     * @param css      - CSS defining the input element
     * @param parentEl - Parent web element. If this is provided the search will be from the parent element. If null, search will be from top element
     * @return - List of elements
     */
    protected List<WebElement> findElements(String css, WebElement parentEl) {
        if (parentEl == null) {
            return webDriver().findElements(By.cssSelector(css));
        } else {
            return parentEl.findElements(By.cssSelector(css));
        }
    }

    protected WebElement findElement(String css, WebElement parentEl) {
        if (parentEl == null) {
            return webDriver().findElement(By.cssSelector(css));
        } else {
            return parentEl.findElement(By.cssSelector(css));
        }
    }

    protected WebElement enterTextAndSelectFromList(WebElement inputField, String value, String popoverCSS,
                                                    int withRetryCount, boolean slowly) throws SeleniumActionsException {
        boolean done = false;
        int initialCount = withRetryCount;

        do {
            try {
                enterTextAndSelectFromList(inputField, value, popoverCSS, slowly);
                done = true;
            } catch (Exception ex) {
                logger.error("Caught an exception " + ex.getMessage());
            }
            withRetryCount--;
        } while (!done && withRetryCount > 0);

        // Need to subtract 1, so that we have 0 retries if we succeeded on the first try.
        int numberOfUsedRetries = initialCount - withRetryCount - 1;
        if (numberOfUsedRetries > 0) {
            logger.warn(done ?
                    format("Entered text successfully and selected CSS '%s' from list after %d retries", popoverCSS, numberOfUsedRetries) :
                    format("Failed to enter text and select CSS '%s' from list.", popoverCSS));
        }
        if (!done) {
            throw new SeleniumActionsException(format("Failed to inputTextAndSelectFromList after %d retries", numberOfUsedRetries));
        }
        return inputField;
    }

    protected void enterTextAndSelectFromList(WebElement inputField, String value, String popoverCSS, boolean slowly) {
        clearText(inputField);
        if (slowly) {
            inputTextSlowly(inputField, value);
        } else {
            inputText(inputField, value);
        }
        verifyElementPresented(popoverCSS, TimeoutType.DEFAULT);
        click(popoverCSS, TimeoutType.DEFAULT);
    }

    protected void invokeMenuItemAndSelect(WebElement clickable, String popoverCSS) {
        Preconditions.checkNotNull(clickable, "Input WebElement cannot be null");
        waitUntilClickable(clickable, TimeoutType.DEFAULT);
        click(clickable, TimeoutType.DEFAULT);
        verifyElementPresented(popoverCSS, TimeoutType.DEFAULT);
        waitUntilClickable(popoverCSS, TimeoutType.DEFAULT);
        click(popoverCSS, TimeoutType.DEFAULT);
    }

}
