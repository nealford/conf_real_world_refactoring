package com.nealford.art.memento.emotherearth.test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;

/**
 * User: Neal Ford
 * Date: Feb 7, 2007
 * Time: 9:55:54 PM
 * <cite>Incidentally, created by IntelliJ IDEA.</cite>
 */
public class DecisionDemoTest extends TestCase {
    private Selenium s;

    public void setUp() {
        s = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/");
        s.start();
    }

    private void login() {
        s.open("/art_emotherearth_memento/welcome");
        s.type("user", "Homer");
        s.click("//input[@id='submitButton']");
        s.waitForPageToLoad("30000");
        assertTrue(s.getLocation().matches(".*art_emotherearth_memento/catalog"));
    }

    private void verifyLastPageContent() {
        assertTrue(s.isTextPresent("*, Thank you for shopping at eMotherEarth.com"));
        assertTrue(s.isTextPresent("regexp:Your confirmation number is \\d?"));
    }

    public void test_Simple_transaction_runs_from_first_to_last_page() {
        login();
        assertEquals("CatalogView", s.getTitle());
        assertTrue(s.isTextPresent("Catalog of Items"));
        assertTrue(s.isElementPresent("//html/body/table/"));
        assertEquals("Ocean", s.getTable("//html/body/table/.1.1"));
        s.type("document.forms[1].quantity", "3");
        s.click("//input[@id='submit2']");
        s.waitForPageToLoad("30000");
        assertTrue(s.getLocation().matches(".*art_emotherearth_memento/showcart"));
        assertEquals("ShowCart", s.getTitle());
        assertTrue(s.isElementPresent("link=Click here for more shopping"));
        assertTrue(s.isTextPresent("*, here is your cart:"));
        s.click("link=Click here for more shopping");
        s.waitForPageToLoad("30000");
        assertTrue(s.getLocation().matches(".*art_emotherearth_memento/catalog"));
        s.type("document.forms[3].quantity", "2");
        s.click("//input[@id='submit4']");
        s.waitForPageToLoad("30000");
        s.type("ccNum", "444444444444");
        s.select("ccType", "label=Amex");
        s.type("ccExp", "12/10");
        s.click("//input[@value='Check out']");
        s.waitForPageToLoad("30000");
        verifyLastPageContent();
    }

    public void test_Undo_operation_restores_button_state_and_shopping_cart_correctly() {
        login();
        assertFalse(s.isElementPresent("restoreButton"));
        s.type("id=qty1", "1");
        s.click("//input[@id='submit1']");
        s.waitForPageToLoad("30000");
        assertEquals("Ocean", s.getTable("//html/body/p[1]/table.1.1"));
        s.click("link=Click here for more shopping");
        s.waitForPageToLoad("30000");
        s.type("id=qty2", "2");
        s.click("//input[@id='submit2']");
        s.waitForPageToLoad("30000");
        assertEquals("Ocean", s.getTable("//html/body/p[1]/table.1.1"));
        assertEquals("Leaves (green)", s.getTable("//html/body/p[1]/table.2.1"));
        s.click("bookmark");
        s.waitForPageToLoad("30000");
        assertTrue(s.isElementPresent("restoreButton"));
        s.click("link=Click here for more shopping");
        s.waitForPageToLoad("30000");
        s.type("id=qty3", "1");
        s.click("//input[@id='submit3']");
        s.waitForPageToLoad("30000");
        assertEquals("Ocean", s.getTable("//html/body/p[1]/table.1.1"));
        assertEquals("Leaves (green)", s.getTable("//html/body/p[1]/table.2.1"));
        assertEquals("Leaves (brown)", s.getTable("//html/body/p[1]/table.3.1"));
        s.click("link=Click here for more shopping");
        s.waitForPageToLoad("30000");
        s.type("id=qty4", "2");
        s.click("//input[@id='submit4']");
        s.waitForPageToLoad("30000");
        assertEquals("Ocean", s.getTable("//html/body/p[1]/table.1.1"));
        assertEquals("Leaves (green)", s.getTable("//html/body/p[1]/table.2.1"));
        assertEquals("Leaves (brown)", s.getTable("//html/body/p[1]/table.3.1"));
        assertEquals("Mountain", s.getTable("//html/body/p[1]/table.4.1"));
        s.click("bookmark");
        s.waitForPageToLoad("30000");
        s.click("link=Click here for more shopping");
        s.waitForPageToLoad("30000");
        s.type("id=qty5", "1");
        s.click("//input[@id='submit5']");
        s.waitForPageToLoad("30000");
        assertEquals("Ocean", s.getTable("//html/body/p[1]/table.1.1"));
        assertEquals("Leaves (green)", s.getTable("//html/body/p[1]/table.2.1"));
        assertEquals("Leaves (brown)", s.getTable("//html/body/p[1]/table.3.1"));
        assertEquals("Mountain", s.getTable("//html/body/p[1]/table.4.1"));
        assertEquals("Lake", s.getTable("//html/body/p[1]/table.5.1"));
        s.click("restore");
        s.waitForPageToLoad("30000");
        assertTrue(s.isElementPresent("restoreButton"));
        s.click("restore");
        s.waitForPageToLoad("30000");
        assertFalse(s.isElementPresent("restoreButton"));
        s.type("ccNum", "444444444444");
        s.select("ccType", "label=Amex");
        s.type("ccExp", "12/10");
        s.click("//input[@value='Check out']");
        s.waitForPageToLoad("30000");
        verifyLastPageContent();
    }


    protected void tearDown() throws Exception {
        s.stop();
    }
}
