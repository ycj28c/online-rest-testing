package example.testng.dataprovider;

import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * How to add tests programmatically, dataprovider + ITest implements for test name
 * https://groups.google.com/forum/#!topic/testng-users/18EHRIsHGGM
 *
 */
public class DataProviderImplementITest implements org.testng.ITest{
	
	public String getTestName() { //test method name is change to 11111
		return "11111";
	}
	
	@DataProvider
    public Object[][] dp()
    {
        return new Object[][]{{"aaaaa"},{"bbbbb"}};
    }

    @Test(dataProvider="dp")
    public void test1(String name)
    {
        System.out.println("DP -->"+name);
        /* http://stackoverflow.com/questions/20887821/how-can-i-override-the-test-method-name-that-appears-on-the-testng-report */
        Reporter.log( getTestName() );  // this magic shows test name on report
    }

//    @Test(dataProvider="dp")
//    public void test2(String name)
//    {
//        System.out.println("DP -->"+name);
//    }

}
