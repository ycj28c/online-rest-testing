package testng.factory;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Factory;
import org.testng.annotations.Test;

/**
 * TestNG's @Factory
 * http://beust.com/weblog/2004/09/27/testngs-factory/
 */
class DummyTestClass2 {
    private int param;
    public DummyTestClass2(int param) {
    	this.param = param;
    	 System.out.println("Construct Param is " + param);
    }
    @Test
    public void dummyTest2() {
        System.out.println("Param is " + param);
    }
}

public class TestNGFactoryTest2 {
	
	@Factory
	public Object[] factory() {
		@SuppressWarnings("unused")
		List<Object> vResult = new ArrayList<Object>();
//		for (int i=0;i<5;i++) {
//			vResult.add(new DummyTestClass2(i));
//		}
//		return vResult.toArray(new Object[vResult.size()]);
		return new Object[] { new DummyTestClass2(1), new DummyTestClass2(2)};
	}
	
	@Test
    public void t() {
        System.out.println("MultipleFactoryTest");
    }
	
//	public static void main(String[] args) {
//		TestNG testng = new TestNG();
//		testng.setTestClasses(new Class[] { DummyTestClass2.class, DummyTestClass.class});
//		testng.run();
//	}
}
