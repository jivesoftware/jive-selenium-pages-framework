package com.jivesoftware.selenium.pagefactory.framework.pages;

/**
 * Created by charles.capps on 7/29/14.
 */
public interface SubPage extends Page {
    /**
     * Set parent page for the subpages.
     */
    void setParent(Page parent);

    /**
     * get parent page from the subpages.
     */
    Page getParent();

    boolean hasParent();

    /**
     * A CSS selector that returns page container locator; useful for nav bars, side bars,
     * inbox message items.
     */
    String getPageContainerCSS();
}
