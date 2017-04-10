package example.testng.dataprovider;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.xml.XmlTest;

/**
 * Names for dynamically generated TestNG tests in Eclipse plugin
 * http://stackoverflow.com/questions/12257387/names-for-dynamically-generated-testng-tests-in-eclipse-plugin/12291703
 */
public class DataProviderExtendsXmlTest extends XmlTest{
	
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() { //Test class name is change to 22222
		return "22222";
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
    }

}
