package com.jivesoftware.selenium.pagefactory.framework.pages;

import com.jivesoftware.selenium.pagefactory.framework.actions.BaseSeleniumActions;

import javax.annotation.Nonnull;

/**
 * Represents a Page that is a top-level web page.
 */
public interface TopLevelPage extends Page {

    @Nonnull
    String getWebPagePath();

    void leavePageHook();

}
