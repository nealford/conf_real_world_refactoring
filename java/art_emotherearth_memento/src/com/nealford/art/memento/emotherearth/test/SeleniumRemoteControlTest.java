package com.nealford.art.memento.emotherearth.test;

/**
 * User: Neal Ford
 * Date: Sep 27, 2006
 * Time: 1:50:51 PM
 * <cite>Incidentally, created by IntelliJ IDEA.</cite>
 */

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;

public class SeleniumRemoteControlTest extends TestCase {
	private Selenium selenium;

    public void setUp() {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/");
        selenium.start();
    }

    public void testEMotherEarthEnd2End() {
        selenium.open("/art_emotherearth_memento/welcome");
        selenium.type("user", "Homer");
        selenium.click("//input[@id='submitButton']");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getLocation().matches(".*art_emotherearth_memento/catalog"));
        assertEquals("CatalogView", selenium.getTitle());
        assertTrue(selenium.isTextPresent("Catalog of Items"));
        assertTrue(selenium.isElementPresent("//html/body/table/"));
        assertEquals("Ocean", selenium.getTable("//html/body/table/.1.1"));
        selenium.type("document.forms[1].quantity", "3");
        selenium.click("//input[@id='submit2']");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getLocation().matches(".*art_emotherearth_memento/showcart"));
        assertEquals("ShowCart", selenium.getTitle());
        assertTrue(selenium.isElementPresent("link=Click here for more shopping"));
        assertTrue(selenium.isTextPresent("*, here is your cart:"));
        selenium.click("link=Click here for more shopping");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getLocation().matches(".*art_emotherearth_memento/catalog"));
        selenium.type("document.forms[3].quantity", "2");
        selenium.click("//input[@id='submit4']");
        selenium.waitForPageToLoad("30000");
        selenium.type("ccNum", "444444444444");
        selenium.select("ccType", "label=Amex");
        selenium.type("ccExp", "12/10");
        selenium.click("//input[@value='Check out']");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.isTextPresent("*, Thank you for shopping at eMotherEarth.com"));
        assertTrue(selenium.isTextPresent("regexp:Your confirmation number is \\d?"));
    }

    public void tearDown() {
        selenium.stop();
    }
}
