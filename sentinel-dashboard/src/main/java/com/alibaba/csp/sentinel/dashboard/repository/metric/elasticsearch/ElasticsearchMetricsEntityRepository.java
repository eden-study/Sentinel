package com.alibaba.csp.sentinel.dashboard.repository.metric.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Date;
import java.util.List;

/**
 * Elasticsearch 监控实体数据访问仓库
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public interface ElasticsearchMetricsEntityRepository extends ElasticsearchRepository<ElasticsearchMetricsEntity, String> {

	List<ElasticsearchMetricsEntity> findByAppAndResource(String app, String resource);

	List<ElasticsearchMetricsEntity> findByAppAndResourceAndTimestampBetween(String app, String resource, Date startTime, Date endTime);
}
