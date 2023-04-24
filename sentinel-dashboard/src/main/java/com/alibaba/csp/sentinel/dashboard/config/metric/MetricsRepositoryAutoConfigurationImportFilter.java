package com.alibaba.csp.sentinel.dashboard.config.metric;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * 监控数据存储组件自动装配动态过滤
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 2.4.x
 */
public class MetricsRepositoryAutoConfigurationImportFilter implements AutoConfigurationImportFilter, EnvironmentAware {

	private static final String MATCH = "sentinel.metrics.type";

	private static final String IN_MEMORY = "memory";

	private static final String INFLUXDB = "influxdb";

	private static final String ELASTICSEARCH = "elasticsearch";

	private static final String[] INFLUX_ENABLED_AUTO_CONFIGURATIONS = {
		"com.influxdb.spring.influx.InfluxDB2AutoConfiguration",
		"com.influxdb.spring.influx.InfluxDB2AutoConfigurationReactive",
		"com.influxdb.spring.health.InfluxDB2HealthIndicatorAutoConfiguration",
		"org.springframework.boot.actuate.autoconfigure.influx.InfluxDbHealthContributorAutoConfiguration"
	};

	private static final String[] ES_ENABLED_AUTO_CONFIGURATIONS = {
		"org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration",
		"org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration",
		"org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration",
		"org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration",
		"org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRestClientAutoConfiguration",
		"org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticsearchHealthIndicatorAutoConfiguration"
	};

	private Environment environment;

	@Override
	public void setEnvironment(@NotNull Environment environment) {
		this.environment = environment;
	}

	@Override
	public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
		String type = environment.getProperty(MATCH);
		boolean[] match = new boolean[autoConfigurationClasses.length];
		for (int i = 0; i < autoConfigurationClasses.length; i++) {
			int index = i;

			// 非过滤池的配置类全部跳过
			if (Arrays.stream(INFLUX_ENABLED_AUTO_CONFIGURATIONS)
					.noneMatch(e -> e.equals(autoConfigurationClasses[index])) &&
				Arrays.stream(ES_ENABLED_AUTO_CONFIGURATIONS)
					.noneMatch(e -> e.equals(autoConfigurationClasses[index]))) {
				match[i] = true;
				continue;
			}

			// 如果设置的是内存，过滤池全部禁用
			if (type == null || IN_MEMORY.equals(type)) {
				match[i] = false;
				continue;
			}

			match[i] = INFLUXDB.equals(type)?
				Arrays.stream(INFLUX_ENABLED_AUTO_CONFIGURATIONS)
					.anyMatch(e -> e.equals(autoConfigurationClasses[index])):
				Arrays.stream(ES_ENABLED_AUTO_CONFIGURATIONS)
					.anyMatch(e -> e.equals(autoConfigurationClasses[index]));
		}
		return match;
	}
}
