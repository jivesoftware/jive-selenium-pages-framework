package com.jivesoftware.selenium.pagefactory.framework.actions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.jivesoftware.selenium.pagefactory.framework.browser.Browser;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutType;
import com.jivesoftware.selenium.pagefactory.framework.config.TimeoutsConfig;
import com.jivesoftware.selenium.pagefactory.framework.exception.JiveWebDriverException;
import com.jivesoftware.selenium.pagefactory.framework.exception.SeleniumActionsException;
import com.jivesoftware.selenium.pagefactory.framework.pages.SubPage;
import com.jivesoftware.selenium.pagefactory.framework.pages.TopLevelPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;

/**
 * <p>
 * This interface represents actions that one can perform with a Browser interacting with a web page.
 * Upon creation, a Browser instance will instantiate the correct type of SeleniumActions.
 * Pages are endowed with a SeleniumActions when created via standard methods such as
 * {@link Browser#loadTopLevelPage}.
 * </p>
 * <p>
 * Throughout, the {@link com.jivesoftware.selenium.pagefactory.framework.config.TimeoutType} enum is used.
 * This works as follows: if you provide DEFAULT as
 * the argument to a method, then the default Timeout for the given context is used. For example, if you are
 * calling "findElementContainingTextWithRefresh" then using DEFAULT will result in TimeoutType.POLLING_WITH_REFRESH_TIMEOUT
 * being used, because that method refreshes the page and polls until it finds an element containing the given text.
 * </p>
 * <p>
 * If you use anything other than TimeoutType.DEFAULT, then you are overriding the timeout for the given method.
 * This is still restricted to the timeouts defined by the enum, so that timeouts are configurable and we have
 * fewer magic numbers in our code.
 * </p>
 */
public interface SeleniumActions {

    /**
     * Return the {@link com.jivesoftware.selenium.pagefactory.framework.browser.Browser} object this actions class is tied to.
     */
    public Browser getBrowser();

    public void setBrowser(Browser browser);

    /**
     * Get a {@link org.openqa.selenium.interactions.Actions} object--used to build sequences of actions like clicking + dragging
     */
    public Actions getActionsBuilder();

    /**
     * Wait for a javascript confirmation dialog to be present, then accept it.
     */
    public void acceptAlert(TimeoutType timeout);

    /**
     * Wait for a javascript confirmation dialog to be present, then dismiss it.
     */
    public void dismissAlert(TimeoutType timeout);

    /**
     * Click without polling for the element to be clickable or waiting until it's ready.
     * Uses the implicit wait timeout built-in to Selenium.
     *
     * @throws JiveWebDriverException - if the element isn't clickable when this method is called.
     */
    public WebElement clickNoWait(String css) throws JiveWebDriverException;

    /**
     * Click the web element defined by the given CSS, with proper waiting until the element is clickable.
     */
    public WebElement click(String css, TimeoutType timeout);

    /**
     * Click the given web element, with proper waiting until the element is clickable.
     */
    public WebElement click(WebElement el, TimeoutType timeout);

    /**
     * Click a button or link that redirects the browser to a new Page, then load the new Page and return it.
     *
     * @param cssToClick - CSS of the element to be clicked
     * @param pageClass  - Class of the {@link TopLevelPage} object to be returned.
     * @param timeout    - timeout object indicating how long to wait, or just TIMEOUT.DEFAULT to use the default
     * @return - the fully initialized {@link TopLevelPage} object
     */
    public <T extends TopLevelPage> T clickAndLoadTopLevelPage(String cssToClick, Class<T> pageClass, TimeoutType timeout);

    public <T extends TopLevelPage> T clickAndLoadTopLevelPage(WebElement el, Class<T> pageClass, TimeoutType timeout);

    /**
     * Click a button or link, then load a SubPage that will be added to the DOM after the click.
     *
     * @param cssToClick - CSS of the element to be clicked
     * @param pageClass  - class of the {@link SubPage} object to be returned.
     * @param timeout    - timeout object indicating how long to wait, or just TIMEOUT.DEFAULT to use the default
     * @return - the fully initialized {@link SubPage} object
     */
    public <T extends SubPage> T clickAndLoadSubPage(String cssToClick, Class<T> pageClass, TimeoutType timeout);

    public <T extends SubPage> T clickAndLoadSubPage(WebElement el, Class<T> pageClass, TimeoutType timeout);

    /**
     * Click a web element, then verify another element is present on the DOM (not necessarily visible).
     *
     * @return - the WebElement we verified was present
     */
    public WebElement clickAndVerifyPresent(String cssToClick, String cssToVerifyPresent, TimeoutType timeout);

    public WebElement clickAndVerifyPresent(WebElement elToClick, String cssToVerifyPresent, TimeoutType timeout);

    /**
     * Click a web element, then verify another element is present on the DOM (not necessarily visible).
     *
     * @return - the WebElement we verified was present
     */
    public WebElement clickAndVerifyVisible(String cssToClick, String cssToVerifyPresent, TimeoutType timeout);

    public WebElement clickAndVerifyVisible(WebElement elToClick, String cssToVerifyPresent, TimeoutType timeout);

    /**
     * Click a web element, then verify another element is NOT present on the DOM (so also not visible).
     */
    public void clickAndVerifyNotPresent(String cssToClick, String cssToVerifyPresent, TimeoutType timeout);

    public void clickAndVerifyNotPresent(WebElement elToClick, String cssToVerifyPresent, TimeoutType timeout);

    /**
     * Click a web element, then verify another element is NOT present on the DOM (so also not visible).
     */
    public void clickAndVerifyNotVisible(String cssToClick, String cssToVerifyPresent, TimeoutType timeout);

    public void clickAndVerifyNotVisible(WebElement elToClick, String cssToVerifyPresent, TimeoutType timeout);

    /**
     * Click a web element defined by CSS cssToClick, then click a popup that is required to be displayed after clicking.
     *
     * @param cssToClick - CSS for the element to be clicked
     * @param popoverCSS - CSS for the popover element that must be present after clicking
     */
    public void clickAndSelectFromList(String cssToClick, String popoverCSS);

    public void clickAndSelectFromList(WebElement clickable, String popoverCSS);

    /**
     * Clear text from an input element.
     *
     * @param css - CSS defining the input element
     * @return - the input element
     */
    public WebElement clearText(String css);

    public WebElement clearText(WebElement el);

    /**
     * @return true if-and-only-if the web element found by the given css has the CSS class "cssClass"
     */
    public boolean doesElementHaveClass(String css, String cssClass);

    /**
     * Execute the given javascript synchronously and return the result.
     *
     * @return - see Selenium docs for what can be returned here, probably just Integer, Boolean, Double, or null
     */
    public Object executeJavascript(String script);

    /**
     * Wait for the given symbol to be defined AND non-null in javascript
     */
    public void waitForJavascriptSymbolToBeDefined(String symbol, TimeoutType timeout);

    public void waitForJavascriptSymbolToHaveValue(String symbol, String value, TimeoutType timeout);

    /**
     * Immediately return true or false as to whether a web element exists on the page.
     */
    public boolean exists(String css);

    public boolean exists(String css, WebElement parentEl);

    public WebElement findElementWithRefresh(String css, TimeoutType timeout);

    public WebElement findVisibleElementWithRefresh(String css, TimeoutType timeout);

    public WebElement findElementContainingText(String css, String text);

    public WebElement findVisibleElementContainingText(String css, String text);

    public WebElement findVisibleElementContainingTextWithWait(final String css, final String text, TimeoutType timeout);

    public WebElement findElementContainingTextWithRefresh(final String css, final String text, TimeoutType timeout);

    public WebElement findVisibleElementContainingTextWithRefresh(final String css, final String text, TimeoutType timeout);

    public WebElement findElementContainingTextWithWait(String css, String text, TimeoutType timeout);

    /**
     * Immediately try to return a WebElement without any implicit or explicit waiting.
     *
     * @return - the WebElement, or null if not present.
     */
    public
    @Nullable
    WebElement getElement(String css);

    public
    @Nullable
    WebElement getChildElement(String css, WebElement parentEl);

    /**
     * Get a WebElement using the implicit wait configured for the Selenium WebDriver.
     *
     * @return the WebElement when found. Null is never returned.
     * @throw RuntimeException - if the web element isn't present after waiting.
     */
    public
    @Nonnull
    WebElement getElementWithWait(String css);

    public
    @Nonnull
    WebElement getChildElementWithWait(String css, WebElement parentEl);

    public List<WebElement> getElements(String css);

    public List<WebElement> getChildElements(String css, WebElement parentEl);

    public WebElement getParentElement(WebElement el);

    public WebElement inputText(String css, String text);

    public WebElement inputText(@Nonnull WebElement el, String text);

    public WebElement inputTextAndSelectFromList(WebElement inputField, String value, String popoverCSS) throws SeleniumActionsException;

    public WebElement inputTextAndSelectFromList(WebElement inputField, String value, String popoverCSS, int withRetryCount) throws SeleniumActionsException;

    public WebElement inputTextSlowly(String css, String text);

    public WebElement inputTextSlowly(WebElement el, String text);

    public WebElement inputTextSlowlyAndSelectFromList(WebElement inputField, String value, String popoverCSS) throws SeleniumActionsException;

    public WebElement inputTextSlowlyAndSelectFromList(WebElement inputField, String value, String popoverCSS, int withRetryCount) throws SeleniumActionsException;

    /**
     * Enter the given text into the input defined by inputCSS, one character at a time.
     * At each step, verify the previous popup was removed from the DOM, and find the new popup.
     * Then see if there is a popup containing the required text on the page. If so, click it and return.
     *
     * @param inputCSS          - CSS for the input element
     * @param text              - text you are entering into the input element
     * @param popupItemCss      - CSS for the popup element containing required text, or list element if there multiple
     * @param requiredPopupText - text required to be present in popup element defined by popupItemCss
     */
    public void enterTextForAutoCompleteAndSelectFirstMatch(String inputCSS, String text, String popupItemCss,
                                                            String requiredPopupText);

    /**
     * Same as above, but enter minChars characters before checking for the popup to exist.
     *
     * @param minChars
     */
    public void enterTextForAutoCompleteAndSelectFirstMatch(String inputCSS, int minChars, String text, String popupItemCss,
                                                            String requiredPopupText);


    /**
     * Enter text into the active tiny MCE editor.
     */
    public void inputTinyMceText(String text);

    /**
     * Wait for tinyMCE.activeEditor.initialized to be true, see Tiny MCE documentation online for why.
     */
    public void waitForTinyMceToBeReady();

    /**
     * Return immediately with an answer as to whether an element is clickable.
     *
     * @return - true if the element is present and clickable, false otherwise.
     */
    public boolean isClickable(String css);

    public boolean isClickable(WebElement el);

    /**
     * Return immediately with an answer as to whether an element is visible.
     *
     * @return - true if the element is present and visible, false otherwise.
     * See Selenium's docs for the definition of visible, it has to be on the page, scrolled into view,
     * have a height and width > 0, etc.
     */
    public boolean isVisible(String css);

    public boolean isVisible(WebElement css);

    /**
     * Scroll so that the element is in the middle of the page.
     */
    public void scrollIntoView(String css);

    public void scrollIntoView(WebElement el);

    /**
     * Scroll the given element with a scroll bar defined by parentCSS so that the web element given by css is in view
     */
    public void scrollIntoView(String parentCSS, String css);

    public void scrollIntoView(String parentCSS, WebElement el);

    public void openWebPage(URI uri);

    /**
     * Returns a Page object with initialized WebElements that is a valid page class for the currently open page in the web driver.
     *
     * @param pageClass - a Class representing the type of page to be initialized.
     */
    public <T extends TopLevelPage> T loadTopLevelPage(Class<T> pageClass);

    public <T extends SubPage> T loadSubPage(Class<T> pageClass);

    public void verifyElementContainsText(String css, String text, TimeoutType timeout);

    public WebElement verifyElementHasClass(String css, String cssClass, TimeoutType timeout);

    public WebElement verifyElementDoesNotHaveClass(final String css, final String cssClass, TimeoutType timeout);

    public void verifyElementSelected(String css, TimeoutType timeout);

    public void verifyElementSelected(WebElement el, TimeoutType timeout);

    public void verifyElementNotSelected(String css, TimeoutType timeout);

    public void verifyElementNotSelected(WebElement el, TimeoutType timeout);

    public WebElement verifyElementPresented(String css, TimeoutType timeout);

    public void verifyElementNotPresented(String css, TimeoutType timeout);

    public void verifyElementWithTextNotPresented(String css, String text, TimeoutType timeout);

    public WebElement verifyElementVisible(String css, TimeoutType timeout);

    public void verifyElementInvisible(String css, TimeoutType timeout);

    public void verifyElementWithTextIsInvisible(String css, String text, TimeoutType timeout);

    /* Method to simplify general waiting code in Pages and Keywords. Takes a function and waits until the return value is non-null.*/
    public <T, V> V waitOnFunction(Function<T, V> function, T input, String message, TimeoutType timeout);

    /* Method to simplify general waiting code in Pages and Keywords. Takes a predicate and waits until it returns true.*/
    public <T> void waitOnPredicate(Predicate<T> predicate, T input, String message, TimeoutType timeout);

    /* Same, but a helper for predicates that don't require an Input, because they use closure to interact with a containing class. */
    public void waitOnPredicate(Predicate<Object> predicate, String message, TimeoutType timeout);

    /* Same, but refresh the page after each time the predicate is checked. */
    public <T> void waitOnPredicateWithRefresh(Predicate<T> predicate, T input, String message, TimeoutType timeout);

    /* Same, but no input is required. */
    public void waitOnPredicateWithRefresh(Predicate<Object> predicate, String message, TimeoutType timeout);

    public <T> T waitOnExpectedCondition(ExpectedCondition<T> expectedCondition, String message, TimeoutType timeout);

    public WebElement verifyPageRefreshed(WebElement elementFromBeforeRefresh, String cssAfterRefresh, TimeoutType timeout);

    public WebElement waitUntilClickable(String css, TimeoutType timeout);

    public WebElement waitUntilClickable(WebElement el, TimeoutType timeout);

    //////////////////////////////////////Timeouts//////////////////////////////////////////////
    public TimeoutsConfig getTimeoutsConfig();
}
