package com.alibaba.csp.sentinel.dashboard.config.metric;

import com.alibaba.csp.sentinel.dashboard.repository.metric.influxdb.InfluxDBMetricsEntityRepository;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.spring.influx.InfluxDB2AutoConfiguration;
import com.influxdb.spring.influx.InfluxDB2Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * InfluxDB 存储监控数据自动配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@ConditionalOnProperty(name = "sentinel.metrics.type", havingValue = "influxdb")
@AutoConfigureAfter(InfluxDB2AutoConfiguration.class)
@EnableConfigurationProperties(InfluxDBMetricsProperties.class)
@RequiredArgsConstructor
@Slf4j
@Configuration(proxyBeanMethods = false)
public class InfluxDBMetricsAutoConfiguration {

	public static final String AUTOWIRED_INFLUX_DB_METRICS_ENTITY_REPOSITORY = "Autowired InfluxDBMetricsEntityRepository";
	private final InfluxDB2Properties influxDB2Properties;

	private final InfluxDBMetricsProperties influxDBMetricsProperties;

	private final InfluxDBClient influxDBClient;

	@Bean
	public InfluxDBMetricsEntityRepository influxDBMetricsEntityRepository() {
		log.debug(AUTOWIRED_INFLUX_DB_METRICS_ENTITY_REPOSITORY);
		return new InfluxDBMetricsEntityRepository(influxDB2Properties,
			influxDBMetricsProperties, influxDBClient);
	}
}
