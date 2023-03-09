package com.alibaba.csp.sentinel.dashboard.repository.metric.elasticsearch;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Elasticsearch 监控实体转换
 */
@Mapper(
    componentModel = "spring",
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ElasticsearchMetricsConvertor {

  ElasticsearchMetricsEntity toElasticsearchMetricEntity(MetricEntity metricEntity);

  List<ElasticsearchMetricsEntity> toElasticsearchMetricEntities(
      Iterable<MetricEntity> metricEntities);

  MetricEntity toMetricEntity(ElasticsearchMetricsEntity elasticsearchMetricEntity);

  List<MetricEntity> toMetricEntities(
      Iterable<ElasticsearchMetricsEntity> elasticsearchMetricEntities);
}
