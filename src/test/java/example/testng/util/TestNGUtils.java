package example.testng.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.springframework.util.ObjectUtils;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import java.util.List;

/**
 * https://github.com/ttddyy/test-contexts/blob/master/test-contexts-core/src/test/java/net/ttddyy/testcontexts/TestNGUtils.java
 *
 */
public class TestNGUtils {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@SuppressWarnings("deprecation")
	public static void runAndVerify(Class<?>... testClasses) {

		if (ObjectUtils.isEmpty(testClasses)) {
			throw new AssertionError("no test class specified.");
		}

		// run testng tests
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testNG = new TestNG();
		testNG.setTestClasses(testClasses);
		testNG.addListener(tla);
		testNG.run();

		List<ITestResult> failedTests = Lists.newArrayList();
		failedTests.addAll(tla.getFailedTests());
		failedTests.addAll(tla.getConfigurationFailures());
		if (!failedTests.isEmpty()) {
			String header = String.format("Combined Messages (Total:%d)", failedTests.size());

			List<String> errorMessages = Lists.newArrayList();
			errorMessages.add(header);
			errorMessages.addAll(Lists.transform(failedTests, new Function<ITestResult, String>() {
				int i = 1;

				public String apply(ITestResult testResult) {
					String stackTraceString = Throwables.getStackTraceAsString(testResult.getThrowable());
					String template = "Message-%d: %n %s";
					return String.format(template, i++, stackTraceString);
				}
			}));

			// transform messages to a single combined string
			String message = Joiner.on(LINE_SEPARATOR).join(errorMessages);
			throw new AssertionError(message);
		}
	}
}