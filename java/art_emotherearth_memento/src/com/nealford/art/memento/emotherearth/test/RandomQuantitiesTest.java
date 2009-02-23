package com.nealford.art.memento.emotherearth.test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;

import java.util.Random;

/**
 * User: Neal Ford
 * Date: Feb 23, 2007
 * Time: 9:33:12 AM
 * <cite>Incidentally, created by IntelliJ IDEA.</cite>
 */
public class RandomQuantitiesTest extends SeleneseTestCase {
    private Random random;
    private Selenium s;


    protected void setUp() throws Exception {
        super.setUp();
        random = new Random();
        s = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/");
        s.start();
    }

    public void test_Random_number_of_quantities() throws Exception {
        s.open("/art_emotherearth_memento/RawData.jsp");
        String userName = s.getValue("user_name");
        String ccNum = s.getValue("cc_num");
        String ccType = s.getValue("cc_type");
        String ccExpDate = s.getValue("cc_exp_date");
        s.open("/art_emotherearth_memento/welcome");
        assertEquals("WelcomeView", s.getTitle());
        s.type("userName", userName);
        s.click("submitButton");
        s.waitForPageToLoad("30000");
        assertEquals("CatalogView", s.getTitle());
        int randomNumberOfInvocations = random.nextInt(5) + 1;
        for (int invocations = 0; invocations < randomNumberOfInvocations; invocations++) {
            int randomQuantity = random.nextInt(3) + 1;
            int randomProductId = random.nextInt(5) + 1;
            s.type("qty"+ randomProductId , String.valueOf(randomQuantity));
            s.click("submit" + randomProductId);
            s.waitForPageToLoad("30000");   
            assertEquals("ShowCart", s.getTitle());
            if (invocations < randomNumberOfInvocations - 1) {
                s.click("link=Click here for more shopping");
                s.waitForPageToLoad("30000");
            }
        }
        s.click("//input[@value='Show Checkout']");
        assertTrue(s.getConfirmation().matches("^Do you really want to check out[\\s\\S]$"));
        s.type("ccNum", ccNum);
        s.select("ccType", "label=" + ccType);
        s.type("ccExp", ccExpDate);
        s.click("//input[@value='Check out']");
        s.waitForPageToLoad("30000");
        assertEquals("CheckOutView", s.getTitle());
        checkForVerificationErrors();
    }

    public void tearDown() {
        s.stop();
    }    
}


