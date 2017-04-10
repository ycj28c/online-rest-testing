package example.testng.factory;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;

/**
 * TestNG Factory Example
 * http://www.javarticles.com/2015/03/example-of-testng-factory.html
 *
 */
public class MultipleFactoryTest {
    private String param;
 
    public MultipleFactoryTest(String param) {
        this.param = param;
    }
 
    @Test
    public void t() {
        System.out.println("MultipleFactoryTest:t:" + param);
    }
     
    @Factory
    public Object[] create() {
        return new Object[] { new MultipleFactoryTest("Multiple factory test"), new TestClass1()};
    }
}
