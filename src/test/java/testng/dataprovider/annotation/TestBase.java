package testng.dataprovider.annotation;

import java.lang.reflect.Method;

import org.testng.ITest;
import org.testng.annotations.BeforeMethod;

public class TestBase implements ITest {

	/**
	 * Name of the current test. Used to implement {@link ITest#getTestName()}
	 */

	private String testInstanceName = "";

	/**
	 * Allows us to set the current test name internally to this class so that
	 * the TestNG framework can use the {@link ITest} implementation for naming
	 * tests.
	 *
	 * @param testName
	 */

	private void setTestName(String anInstanceName) {
		testInstanceName = anInstanceName;
	}

	/**
	 * See {@link ITest#getTestName()}
	 */

	public String getTestName() {
		return testInstanceName;
	}

	/**
	 * Method to transform the name of tests when they are called with the
	 * testname as one of the parameters. Only takes effect if method has
	 * {@link UseAsTestName} annotation on it..
	 *
	 * @param method
	 *            The method being called.
	 *
	 * @param parameterBlob
	 *            The set of test data being passed to that method.
	 */

	@BeforeMethod(alwaysRun = true)
	public void extractTestNameFromParameters(Method method, Object[] parameters) {

		/*
		 * Verify Parameters
		 */

		// checkNotNull(method);
		// checkNotNull(parameters);

		/*
		 * Empty out the name from the previous test
		 */

		setTestName(method.getName());

		/*
		 * If there is a UseAsTestCaseID annotation on the method, use it to get
		 * a new test name
		 */

		UseAsTestName useAsTestName = method.getAnnotation(UseAsTestName.class);

		if (useAsTestName != null) {

			/*
			 * Check that the index it uses is viable.
			 */

			if (useAsTestName.idx() > parameters.length - 1) {
				throw new IllegalArgumentException(format(
						"We have been asked to use an incorrect parameter as a Test Case ID. The {0} annotation on method {1} is asking us to use the parameter at index {2} in the array and there are only {3} parameters in the array.",
						UseAsTestName.class.getSimpleName(), method.getName(), useAsTestName.idx(), parameters.length));
			}

			/*
			 * Is the parameter it points to assignable as a string.
			 */

			Object parmAsObj = parameters[useAsTestName.idx()];

			if (!String.class.isAssignableFrom(parmAsObj.getClass())) {
				throw new IllegalArgumentException(
						format("We have been asked to use a parameter of an incorrect type as a Test Case Name. The {0} annotation on method {1} is asking us to use the parameter at index {2} in the array that parameter is not usable as a string. It is of type {3}",
								UseAsTestName.class.getSimpleName(), method.getName(), useAsTestName.idx(),
								parmAsObj.getClass().getSimpleName()));
			}

			/*
			 * Get the parameter at the specified index and use it.
			 */

			String testCaseId = (String) parameters[useAsTestName.idx()];

			setTestName(testCaseId);
		}
	}

	private <T> String format(String s, String simpleName, String name, T idx, T simpleName2) {
		return s.replace("{0}", simpleName).replace("{1}", name).replace("{2}", String.valueOf(idx)).replace("{3}",
				String.valueOf(simpleName2));

	}
}
