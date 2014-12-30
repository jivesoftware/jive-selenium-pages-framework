package com.jivesoftware.selenium.pagefactory.framework.pages;

import com.google.common.base.Optional;
import com.jivesoftware.selenium.pagefactory.framework.actions.SeleniumActions;
import com.jivesoftware.selenium.pagefactory.framework.browser.web.WebBrowser;
import com.jivesoftware.selenium.pagefactory.framework.exception.InvalidPageUrlException;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base abstract class for a TopLevelPage. Implements the default pageLoadHook that waits for the page identifier to be present.
 * <p/>
 * Subclasses should call super.pageLoadHook() if they want to wait on the page identifier.
 */
public class BaseTopLevelPage<S extends SeleniumActions> implements TopLevelPage {
    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(BaseTopLevelPage.class);

    private static final PageUtils PAGE_UTILS = new PageUtils();
    protected S a;

    public final S getActions() {
        return a;
    }

    public final void setActions(SeleniumActions actions) {
        this.a = (S) actions;
    }

    @Nonnull
    @Override
    public String getWebPagePath() {
        Optional<String> optionalPathFromAnnotation = PAGE_UTILS.getWebPagePathForClass(getClass());
        if (optionalPathFromAnnotation.isPresent()) {
            return optionalPathFromAnnotation.get();
        }
        return "/";
    }

    @Override
    public void pageLoadHook() {
        // First verify that the current URL matches the value annotated with @WebPagePath
        verifyCurrentURL();

        PAGE_UTILS.defaultPageLoadHook(this, a);
    }

    /**
     * Verify that the current URL the browser is pointing to matches the path given by the @WebPagePath annotation.
     *
     * For example, if the current URL is "http://example.com/foo/bar" and we specify @WebPagePath(path = "/bar") then this would be a match, because we only
     * require the URL to end with the given path. (Since we can't know the root context of the server).
     *
     * If the current URL is "http:/example.com/foo/1234" and we specify @WebPagePath(isRegex = true, path = "/foo/\\d+"), then this would match as a regex.
     */
    public void verifyCurrentURL() {
        WebPagePath webPagePath = getClass().getAnnotation(WebPagePath.class);

        // If the @WebPagePath annotation isn't present, or browser isn't a WebBrowser, then return.
        if (webPagePath == null || !(a.getBrowser() instanceof WebBrowser)) {
            return;
        }

        String expectedPath = webPagePath.path();
        boolean regex = webPagePath.isRegex();

        String currentURL = a.getCurrentURL();

        // Not sure when a WebDriver returns null for current URL, but just don't validate in this case
        if (currentURL == null) {
            return;
        }

        URI currentURI = URI.create(currentURL);
        String currentPath = currentURI.getPath();

        // Remove trailing slashes
        if (currentPath.endsWith("/")) {
            currentPath = currentPath.substring(0, currentPath.length() - 1);
        }
        if (expectedPath.endsWith("/")) {
            expectedPath = expectedPath.substring(0, expectedPath.length() - 1);
        }

        if (regex) {
            Pattern pattern = Pattern.compile(expectedPath);
            Matcher m = pattern.matcher(currentPath);
            if (!m.find() || !m.hitEnd()) {
                throw new InvalidPageUrlException(String.format("The current path of the web browser is %s, but expected the path to end with an expression " +
                                                                    "matching the regex '%s'",
                                                                currentPath, expectedPath));
            }

            logger.info("SUCCESS - the current path {} matches the regex '{}'", currentPath, expectedPath);

        } else {
            // The current path should end with the expected path --- we don't know what the Root context of the server is.
            if (!currentPath.endsWith(expectedPath)) {
                throw new InvalidPageUrlException(String.format("The current path of the web browser is %s, but expected the path to end with '%s'",
                                                                currentPath, expectedPath));
            }

            logger.info("SUCCESS - the current path {} matches the required path '{}'", currentPath, expectedPath);
        }
    }


    @Override
    public By getPageIdentifier() {
        return null;
    }

    public final void initSubPages() {
        PAGE_UTILS.initSubPages(this, a);
    }

    @Override
    public final void refreshElements() {
        PageFactory.initElements(getActions().getBrowser().getWebDriver(), this);
        initSubPages();
        pageLoadHook();
    }


    @Override
    public void refreshPage() {
        getActions().getBrowser().refreshPage();
        refreshElements();
    }

    @Override
    public void leavePageHook() {

    }
}
