jive-selenium-pages-framework
=============================

<h2>Jive Selenium Pages Framework</h2>

<h4>Author: Charles Capps</h4>
<h4>Contact Email: qa.automators@jivesoftware.com</h4>
<h4>Javadoc can be found 
    <a href="http://jivesoftware.github.io/jive-selenium-pages-framework/javadoc/" title="Jive Selenium Pages Framework Javadoc">here</a>
</h4>

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
    Use LocalBrowserBuilder.getBuilder() or RemoteBrowserBuilder.getBuilder() to instantiate a 
    Chrome, Firefox, or Internet Explorer Browser that is either on the local machine or running in a 
    Selenium Grid.
    </li>
    <li>Methods such as saveScreenshotToFile() are helpful utilities for interacting with the Browser.</li>
    <li>
    After you have a browser, call browser.getActions() to get an instance of SeleniumActions.
    </li>
</ul>

<h3>Adding jive-selenium-pages-framework as a dependency</h3>
This project is in the Maven Central Repository, 
so if your project uses maven you can just add this as a dependency with:

<pre>
    &lt;dependency&gt;
        &lt;groupId&gt;com.jivesoftware&lt;/groupId&gt;
        &lt;artifactId&gt;jive-selenium-pages-framework&lt;/artifactId&gt;
        &lt;version&gt;1.0.10&lt;/version&gt;
    &lt;/dependency&gt;
</pre>

<h3>Building</h3>
This project uses maven. For maven, you must have JAVA_HOME set to a valid Java installation of Java7 or above. 
As long as you have maven 3.0.5 or above installed and Java7 or above, then you should be
able to execute the following:

<pre>mvn clean install -DskipTests</pre>

to install a new version to your local repo. You can then use it by adding the version you installed to the POM
of any local project. 

<h4>Sample code creating a Browser instance (Chrome)</h4>

<pre>
    // Create a TimeoutsConfig instance. You can also just use TimeoutsConfig.defaultTimeoutsConfig().
    TimeoutsConfig timeouts = TimeoutsConfig.builder()
        .clickTimeoutSeconds(2)                  // Timeout waiting for a WebElement to be clickable (used by the framework)
        .webElementPresenceTimeoutSeconds(5)     // Timeout when polling for a web element to be present (or visible, depending on the method)
        .pageLoadTimoutSeconds(10)               // Timeout waiting for a new page to load (used by the framework, and to configure underlying WebDriver).
        .implicitWaitTimeoutMillis(2000)         // Implicit wait timeout used by the underlying WebDriver.
        .build();

    // Create a ChromeBrowser
    Browser browser = LocalBrowserBuilder.getChromeBuilder("http://my.webapp.com/webapp")  // Base URL for testing. 
                           .withTimeoutsConfig(timeouts)             // TimeoutsConfig created above.
                           .withBrowserLocale(Locale.US.toString())  // Browser locale
                           .withStartWindowWidth(1280)               // Starting width for the browser window in pixels
                           .withStartWindowHeight(1024)              // Starting height for the browser window in pixels
                           .withBrowserLogLevel(Level.INFO)          // Logging Level for the WebDriver's logs
                           .withBrowserLogFile("chromedriver.log")   // Path to logfile, only supported for Chrome and IE. 
                           .build();
                           
     // Load a web page
     TopLevelPage googleHomePage = browser.openPageByUrl("http://google.com");
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

