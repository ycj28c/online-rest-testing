package testng.factory;

import org.testng.TestNG;
import org.testng.annotations.Test;

/**
 * TestNG factory test
 * http://www.javarticles.com/2015/03/example-of-testng-factory.html
 * 
 * example to run by maven:
 * mvn exec:java -Dexec.mainClass="testng.factory.TestNGFactoryTest"
 */
class DummyTestClass {
    private String param;
    public DummyTestClass(String param) {
        this.param = param;
    }
    @Test
    public void dummyTest() {
        System.out.println("Param is " + param);
    }
}

public class TestNGFactoryTest {
	
	public static void main(String[] args) {
		TestNG testng = new TestNG();
		testng.setTestClasses(new Class[] { DummyTestClass.class });
		testng.run();
	}
}
