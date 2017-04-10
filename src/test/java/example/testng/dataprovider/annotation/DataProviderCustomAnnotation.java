package example.testng.dataprovider.annotation;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * TestNG: Dynamically Naming Tests from data provider parameters
 * http://biggerwrench.blogspot.com/2014/02/testng-dynamically-naming-tests-from.html 
 *
 */
public class DataProviderCustomAnnotation extends TestBase {

	@DataProvider(name = "rawDP")
	public Object[][] sampleDataProvider() {
		Object[][] rawData = { 
			{ "SCENARIO_1", "First Test Scenario" }, 
			{ "SCENARIO_2", "Second Test Scenario" },
			{ "SCENARIO_3", "Third Test Scenario" } 
		};

		return rawData;
	}

	@Test(dataProvider = "rawDP")
	public void shouldHaveTestNamesBasedOnMethodName(String arg1, String arg2) {
	}

	@UseAsTestName()
	@Test(dataProvider = "rawDP")
	public void shouldHaveTestNamesStartingWithANA(String arg1, String arg2) {
		getTestName().equals(arg1);
	}

	@UseAsTestName(idx = 1)
	@Test(dataProvider = "rawDP")
	public void shouldHaveTestNamesStartingWithThe(String arg1, String arg2) {
		getTestName().equals(arg2);
	}
}
