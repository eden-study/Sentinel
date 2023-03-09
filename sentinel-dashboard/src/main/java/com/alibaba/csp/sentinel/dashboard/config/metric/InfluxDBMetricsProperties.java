package com.alibaba.csp.sentinel.dashboard.config.metric;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * InfluxDB 监控配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@Setter
@Getter
@ConfigurationProperties(prefix = InfluxDBMetricsProperties.PREFIX)
public class InfluxDBMetricsProperties {

	public static final String PREFIX = "sentinel.metrics.influxdb";

	private String measurement = "sentinel_metric";
}
