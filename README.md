jive-selenium-pages-framework
=============================

<h2>Jive Selenium Pages Framework</h2>

<h4>Author: Charles Capps</h4>
<h4>Email: charles.capps@jivesoftware.com</h4>

<p>
This framework provides many valuable features for simplifying Selenium Browser testing.
The framework has been used internally at Jive Software with much success. 
It simplifies the configuration and creation of Selenium WebDrivers for different browsers. 
The framework also provides a Page abstraction for modeling your webapp's pages. 
</p>

<h3>Key Features:</h3>

<h4>Browser configuration and instantiation</h4>
<ul>
    <li>The Browser classes provide a facade for configuring and using Selenium WebDrivers.</li>
    <li>
    Use BrowserFactory.createLocalBrowser() or BrowserFactory.createRemoteBrowser() to instantiate a 
    Chrome, Firefox, or Internet Explorer Browser that is either on the local machine or running in a 
    Selenium Grid.
    </li>
    <li>Methods such as saveScreenshotToFile() are helpful utilities for interacting with the Browser.</li>
    <li>
    After you have a browser, call browser.getActions() to get an instance of SeleniumActions.
    </li>
</ul>

<h4>Sample code creating a Browser instance (Chrome)</h4>

<pre>
    // Create a TimeoutsConfig instance
    TimeoutsConfig timeouts = TimeoutsConfig.builder()
        .clickTimeoutSeconds(2)                  // Timeout waiting for a WebElement to be clickable (used by the framework)
        .webElementPresenceTimeoutSeconds(5)     // Timeout when polling for a web element to be present (or visible, depending on the method)
        .pageLoadTimoutSeconds(10)               // Timeout waiting for a new page to load (used by the framework, and to configure underlying WebDriver).
        .implicitWaitTimeoutMillis(2000)         // Implicit wait timeout used by the underlying WebDriver.
        .build();

    // Create a ChromeBrowser
    Browser browser = BrowserFactory.createLocalBrowser(BrowserType.CHROME,       // BrowserType -- currently only supports Chrome, Firefox, and IE
                                                        "http://my.webapp.com/webapp",  // Base URL for testing. 
                                                        timeouts,                  // TimeoutsConfig created above.
                                                        Optional.<String>absent(), // Path to web driver -- Not required if Chromedriver is on your PATH 
                                                        Optional.<String>absent(), // Path to browser binary -- Not required if Chrome binary is in standard location 
                                                        Locale.US.toString(),      // Browser locale
                                                        Optional.of(1280),         // Optional starting width for the browser window in pixels
                                                        Optional.of(1024),         // Optional starting height in pixels
                                                        Optional.of(Level.INFO),   // Optional Logging Level for the WebDriver's logs
                                                        Optional.of("chromedriver.log") // Optional path to logfile, only supported for Chrome and IE. 
                                                        );
</pre>

<h4>SeleniumActions</h4>
<ul>
    <li>SeleniumActions are for interacting with the DOM and javascript of a page.</li>
    <li>Obtain a SeleniumActions from a Browser instance with browser.getActions()</li>
    <li>Provides methods for waiting until a WebElement is present or visible.</li>
    <li>Provides methods to refresh the page until a WebElement is present or visible.</li>
    <li>Provides methods for finding a WebElement containing specific text.</li>
    <li>Provides methods for interacting with <a href="http://www.tinymce.com/">Tiny MCE</a> text editors.</li>
    <li>Provides methods to load Pages.</li>
    <li>Provides much more functionality.</li>
</ul>

<h4>Pages</h4>
<ul>
    <li>Pages provide an abstraction for modeling the pages for your webapp.</li>
    <li>Pages should extend BaseTopLevelPage or BaseSubPage.</li>
    <li>Uses the Selenium @FindBy annotation to instantiate member variables that are WebElements.</li>
    <li>Use the annotation @SubPageField to indicate a member variable that is a SubPage and should be instantiated on page load.</li>
    <li>Model the actions that you can perform on your web pages in your Page classes.</li>
    <li>Then, test code is incredibly simple. It just delegates to Page classes and performs high-level actions.</li>
</ul>

