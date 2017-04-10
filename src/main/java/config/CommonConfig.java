package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
@PropertySource(value = "classpath:common.properties")
public class CommonConfig {

	@Autowired
	ConfigurableEnvironment env;
	
}