package com.alibaba.csp.sentinel.dashboard.repository.metric.elasticsearch;

import com.alibaba.csp.sentinel.dashboard.config.metric.ElasticsearchMetricsProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.AliasQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Elasticsearch 监控数据访问仓库
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchMetricsRepository implements MetricsRepository<MetricEntity> {

	private final byte[] lock = new byte[0];

	private final ElasticsearchMetricsProperties elasticsearchMetricsProperties;

	private final ElasticsearchMetricsEntityRepository elasticsearchMetricsEntityRepository;

	private final ElasticsearchRestTemplate elasticsearchRestTemplate;

	private final ElasticsearchMetricsConvertor elasticsearchMetricsConvertor;

	/**
	 * Save the metric to the storage repository.
	 *
	 * @param metric data to save
	 */
	@Override
	public void save(MetricEntity metric) {
		AssertUtil.notNull(metric, "Save a metric but it is null");

		createIndexIfNotExists();
		ElasticsearchMetricsEntity entity =
			elasticsearchMetricsConvertor.toElasticsearchMetricEntity(metric);
		elasticsearchMetricsEntityRepository.save(entity);
	}

	/**
	 * Save all metrics to the storage repository.
	 *
	 * @param metrics to save
	 */
	@Override
	public void saveAll(Iterable<MetricEntity> metrics) {
		AssertUtil.notNull(metrics, "Save all metrics but it is null");

		createIndexIfNotExists();
		List<ElasticsearchMetricsEntity> entities =
			elasticsearchMetricsConvertor.toElasticsearchMetricEntities(metrics);
		elasticsearchMetricsEntityRepository.saveAll(entities);
	}

	/**
	 * Get all metrics by {@code appName} and {@code resourceName} between a period of time.
	 *
	 * @param app       application name for Sentinel
	 * @param resource  resource name
	 * @param startTime start timestamp
	 * @param endTime   end timestamp
	 * @return all metrics in query conditions
	 */
	@Override
	public List<MetricEntity> queryByAppAndResourceBetween(
		String app, String resource, long startTime, long endTime) {

		if (StringUtil.isBlank(app) || !isIndexExists()) {
			return Collections.EMPTY_LIST;
		}

		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder
			.must(new MatchQueryBuilder("app", app))
			.must(new MatchQueryBuilder("resource", resource))
			.must(new RangeQueryBuilder("timestamp").gte(startTime).lte(endTime));

		NativeSearchQuery nativeSearchQuery =
			nativeSearchQueryBuilder
				.withQuery(boolQueryBuilder)
				.withPageable(PageRequest.of(0,
					elasticsearchMetricsProperties.getQueryLimitSize()))
				.build();

		SearchHits<ElasticsearchMetricsEntity> searchHits =
			elasticsearchRestTemplate.search(nativeSearchQuery, ElasticsearchMetricsEntity.class);

		List<ElasticsearchMetricsEntity> resultList =
			searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(resultList)) {
			return Collections.EMPTY_LIST;
		}

		return resultList.stream()
			.map(elasticsearchMetricsConvertor::toMetricEntity)
			.collect(Collectors.toList());
	}

	/**
	 * List resource name of provided application name.
	 *
	 * @param app application name
	 * @return list of resources
	 */
	@Override
	public List<String> listResourcesOfApp(String app) {
		if (StringUtil.isBlank(app) || !isIndexExists()) {
			return Collections.EMPTY_LIST;
		}

		NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("app", app));
		queryBuilder.withQuery(boolQueryBuilder);
		queryBuilder.withCollapseField("resource.keyword");
		queryBuilder.withFields("resource");
		SearchHits<ElasticsearchMetricsEntity> searchHits =
			elasticsearchRestTemplate.search(queryBuilder.build(), ElasticsearchMetricsEntity.class);

		return searchHits.toList().stream().map(e -> e.getContent().getResource()).collect(Collectors.toList());
	}

	/**
	 * 如果索引不存在，直接创建
	 */
	private void createIndexIfNotExists() {
		if (!isIndexExists()) {
			synchronized (lock) {
				if (!isIndexExists()) {
					elasticsearchRestTemplate.indexOps(ElasticsearchMetricsEntity.class).create();
					elasticsearchRestTemplate
						.indexOps(ElasticsearchMetricsEntity.class)
						.addAlias(new AliasQuery(elasticsearchMetricsProperties.getIndexName()));
				}
			}
		}
	}

	/**
	 * 判断索引是否存在
	 *
	 * @return
	 */
	private boolean isIndexExists() {
		return elasticsearchRestTemplate.indexOps(ElasticsearchMetricsEntity.class).exists();
	}
}
