package com.jivesoftware.selenium.pagefactory.framework.browser;

import com.jivesoftware.selenium.pagefactory.framework.pages.TopLevelPage;

import javax.annotation.Nonnull;

/**
 * Created by charles.capps on 7/29/14.
 */
public final class CachedPage {
    private final String url;
    private final TopLevelPage cachedPage;

    public CachedPage(@Nonnull String url, @Nonnull TopLevelPage cachedPage) {
        this.url = url;
        this.cachedPage = cachedPage;
    }

    public String getUrl() {
        return url;
    }

    public TopLevelPage getCachedPage() {
        return cachedPage;
    }
}
