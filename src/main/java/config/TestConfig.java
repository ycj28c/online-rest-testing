package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Basic test configuration.
 */
@Configuration
public class TestConfig {

	private static final Logger log = LoggerFactory.getLogger(TestConfig.class);

	@Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		log.info("Building PropertyPlaceHolder configurer");
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		ppc.setIgnoreResourceNotFound(false);
		return ppc;
	}
}

