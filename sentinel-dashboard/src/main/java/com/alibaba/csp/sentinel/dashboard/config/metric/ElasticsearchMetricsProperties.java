package com.alibaba.csp.sentinel.dashboard.config.metric;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Elasticsearch 监控配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@Setter
@Getter
@ConfigurationProperties(prefix = ElasticsearchMetricsProperties.PREFIX)
public class ElasticsearchMetricsProperties {

	public static final String PREFIX = "sentinel.metrics.elasticsearch";

	private String indexName = "sentinel_metric";

	private int queryLimitSize = 1800;
}
