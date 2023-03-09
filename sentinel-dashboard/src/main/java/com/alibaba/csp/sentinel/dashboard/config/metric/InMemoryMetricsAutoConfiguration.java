package com.alibaba.csp.sentinel.dashboard.config.metric;

import com.alibaba.csp.sentinel.dashboard.repository.metric.memory.InMemoryMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 内存化监控自动配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@ConditionalOnProperty(name = "sentinel.metrics.type", havingValue = "memory")
@RequiredArgsConstructor
@Slf4j
@Configuration(proxyBeanMethods = false)
public class InMemoryMetricsAutoConfiguration {

	public static final String AUTOWIRED_IN_MEMORY_METRICS_REPOSITORY = "Autowired InMemoryMetricsRepository";

	@Bean
	public InMemoryMetricsRepository inMemoryMetricsRepository() {
		log.debug(AUTOWIRED_IN_MEMORY_METRICS_REPOSITORY);
		return new InMemoryMetricsRepository();
	}
}
