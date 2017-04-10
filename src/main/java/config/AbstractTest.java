package config;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@ContextConfiguration(classes = { TestConfig.class, })
@TestPropertySource(locations = { "classpath:spreadsheet-${spreadsheet:default}.properties" })
abstract public class AbstractTest extends AbstractTestNGSpringContextTests {

}