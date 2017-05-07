package com.ayoza.feline;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = {"com.ayoza.feline.core"} , excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.ayoza.feline.core.test.*"))
public class FelineCoreConfig {
	/* */
}
