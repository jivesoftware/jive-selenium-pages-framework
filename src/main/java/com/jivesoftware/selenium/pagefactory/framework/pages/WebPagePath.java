package com.jivesoftware.selenium.pagefactory.framework.pages;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by charles.capps on 7/29/14.
 *
 * An annotation on a TopLevelPage class indicating the path part of the URI to the web page resource.
 *
 * e.g. "/social-business-software/social-community-software/" would be the path for
 * http://www.jivesoftware.com/social-business-software/social-community-software/
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebPagePath {

    @Nonnull String path();

    boolean isRegex() default false;

}
