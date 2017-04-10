package testng.dataprovider;

import java.lang.reflect.Method;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DataProviderMultMethods {
	/* want to programmatically generate tests from data provider, researching on it */
	
	@DataProvider
    public Object[][] dp(Method method)
    {
        System.out.println("Test method : "+method.getName());
        if(method.getName().equals("test1"))
            return new Object[][]{{method.getName()}};
        else if(method.getName().equals("test2"))
            return new Object[][]{{method.getName()}};
        else
            return new Object[][]{};
    }

    @Test(dataProvider="dp")
    public void test1(String name)
    {
        System.out.println("DP -->"+name);
    }

    @Test(dataProvider="dp")
    public void test2(String name)
    {
        System.out.println("DP -->"+name);
    }
}
