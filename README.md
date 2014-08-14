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

