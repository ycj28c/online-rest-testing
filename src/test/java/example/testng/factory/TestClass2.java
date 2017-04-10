package example.testng.factory;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class TestClass2 {
    @Test
    public void t() {
        System.out.println("TestClass2.t");
    }
     
    @Factory
    public Object[] create() {
        return new Object[] { new TestClass3() };
    }
}