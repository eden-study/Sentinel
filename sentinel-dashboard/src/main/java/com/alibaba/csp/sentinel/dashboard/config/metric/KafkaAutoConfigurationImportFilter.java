package com.alibaba.csp.sentinel.dashboard.config.metric;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * Kafka 自动装配过滤
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.5
 */
public class KafkaAutoConfigurationImportFilter implements AutoConfigurationImportFilter, EnvironmentAware {

	private static final String MATCH_KEY = "sentinel.metrics.sender.type";

	private static final String MATCH_VALUE = "kafka";

	private static final String[] IGNORE_CLASSES = {
		"org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
		"org.springframework.boot.actuate.autoconfigure.metrics.KafkaMetricsAutoConfiguration"
	};

	private Environment environment;

	@Override
	public void setEnvironment(@NotNull Environment environment) {
		this.environment = environment;
	}

	@Override
	public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
		boolean disabled = !MATCH_VALUE.equals(environment.getProperty(MATCH_KEY));
		boolean[] match = new boolean[autoConfigurationClasses.length];
		for (int i = 0; i < autoConfigurationClasses.length; i++) {
			int index = i;
			match[i] = !disabled || Arrays.stream(IGNORE_CLASSES).noneMatch(e -> e.equals(autoConfigurationClasses[index]));
		}
		return match;
	}
}
