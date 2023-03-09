package com.alibaba.csp.sentinel.dashboard.config.metric;

import com.alibaba.csp.sentinel.dashboard.repository.metric.elasticsearch.ElasticsearchMetricsConvertor;
import com.alibaba.csp.sentinel.dashboard.repository.metric.elasticsearch.ElasticsearchMetricsEntityRepository;
import com.alibaba.csp.sentinel.dashboard.repository.metric.elasticsearch.ElasticsearchMetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * Elasticsearch 存储监控数据自动配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@ConditionalOnClass(RestHighLevelClient.class)
@ConditionalOnProperty(name = "sentinel.metrics.type", havingValue = "elasticsearch")
@EnableConfigurationProperties({ElasticsearchMetricsProperties.class})
@RequiredArgsConstructor
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ElasticsearchMetricsAutoConfiguration {

	public static final String AUTOWIRED_ELASTICSEARCH_METRICS_REPOSITORY = "Autowired ElasticsearchMetricsRepository";
	private final ElasticsearchMetricsProperties elasticsearchMetricsProperties;

	private final ElasticsearchMetricsEntityRepository elasticsearchMetricsEntityRepository;

	private final ElasticsearchRestTemplate elasticsearchRestTemplate;

	private final ElasticsearchMetricsConvertor elasticsearchMetricsConvertor;

	@Bean
	public ElasticsearchMetricsRepository elasticsearchMetricsRepository() {
		log.debug(AUTOWIRED_ELASTICSEARCH_METRICS_REPOSITORY);
		return new ElasticsearchMetricsRepository(
			elasticsearchMetricsProperties,
			elasticsearchMetricsEntityRepository,
			elasticsearchRestTemplate,
			elasticsearchMetricsConvertor);
	}
}
