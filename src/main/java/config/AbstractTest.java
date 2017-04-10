package config;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ContextConfiguration(classes = { TestConfig.class, })
abstract public class AbstractTest extends AbstractTestNGSpringContextTests {

}