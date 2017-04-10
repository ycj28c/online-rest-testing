package testng.dataprovider.report;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

/*
 * Pretty Printing with TestNG
 * https://rationaleemotions.wordpress.com/2013/07/31/pretty-printing-with-testng/
 */
public class SimpleDataDriven {
    @Test(dataProvider = "dp")
    public void myTestMethod(int age, String s) {
        assertNotNull(s);
        assertTrue(age != 0);
    }
 
    @DataProvider
    public Object[][] dp() {
        return new Object[][] { new Object[] { 25, "John" }, new Object[] { 27, "Rambo" }, };
    }
}