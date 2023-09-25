package com.alibaba.csp.sentinel.dashboard.repository.metric.influxdb;

import com.alibaba.csp.sentinel.dashboard.config.metric.InfluxDBMetricsProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.spring.influx.InfluxDB2Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Elasticsearch 监控实体数据访问仓库
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@RequiredArgsConstructor
public class InfluxDBMetricsEntityRepository implements MetricsRepository<MetricEntity> {

	private final InfluxDB2Properties influxDB2Properties;

	private final InfluxDBMetricsProperties influxDBMetricsProperties;

	private final InfluxDBClient influxDBClient;

	/**
	 * Save the metric to the storage repository.
	 *
	 * @param metric metric data to save
	 */
	@Override
	public void save(MetricEntity metric) {
		AssertUtil.notNull(metric, "Save a metric but it is null");

		InfluxDBMetricsEntity entity = InfluxDBMetricsEntity.builder()
			.app(metric.getApp())
			.resource(metric.getResource())
			.passQps(metric.getPassQps())
			.successQps(metric.getSuccessQps())
			.blockQps(metric.getBlockQps())
			.exceptionQps(metric.getExceptionQps())
			.rt(metric.getRt())
			.count(metric.getCount())
			.resourceCode(metric.getResourceCode())
			.gmtCreate(metric.getGmtCreate().getTime())
			.gmtModified(metric.getGmtModified().getTime())
			.timestamp(metric.getTimestamp().getTime())
			.build();
		influxDBClient.getWriteApiBlocking().writeMeasurement(WritePrecision.MS, entity);
	}

	/**
	 * Save all metrics to the storage repository.
	 *
	 * @param metrics metrics to save
	 */
	@Override
	public void saveAll(Iterable<MetricEntity> metrics) {
		if (metrics == null) {
			return;
		}

		metrics.forEach(this::save);
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
	public List<MetricEntity> queryByAppAndResourceBetween(String app, String resource, long startTime, long endTime) {
		List<MetricEntity> results = new ArrayList<>();
		if (StringUtil.isBlank(app)) {
			return results;
		}

		Instant now = Instant.now();
		String flux = String.format("from(bucket:\"%s\") |> range(start: %s, stop: %s)"
				+ " |> filter(fn: (r) => (r[\"_measurement\"] == \"%s\" and r[\"app\"] == \"%s\") and r[\"resource\"] == \"%s\")",
			influxDB2Properties.getBucket(),
			toDurationStr(startTime, now),
			toDurationStr(endTime, now),
			influxDBMetricsProperties.getMeasurement(), app, resource);

		List<FluxTable> fluxTables = influxDBClient.getQueryApi().query(flux);
		for (FluxTable fluxTable : fluxTables) {
			List<FluxRecord> records = fluxTable.getRecords();
			for (FluxRecord fluxRecord : records) {
				results.add(toMetricEntity(fluxRecord));
			}
		}
		return results;
	}

	private String toDurationStr(long selected, Instant now) {
		Instant passed = Instant.ofEpochMilli(selected);
		long sec = Duration.between(now, passed).getSeconds();
		if (sec > 0) {
			return "now()";
		}
		return sec + "s";
	}

	/**
	 * List resource name of provided application name.
	 *
	 * @param app application name
	 * @return list of resources
	 */
	@Override
	public List<String> listResourcesOfApp(String app) {
		List<String> results = new ArrayList<>();
		if (StringUtil.isBlank(app)) {
			return results;
		}

		String flux = String.format("from(bucket:\"%s\") |> range(start: -5m)"
				+ " |> filter(fn: (r) => (r[\"_measurement\"] == \"%s\" and r[\"app\"] == \"%s\") )",
			influxDB2Properties.getBucket(), influxDBMetricsProperties.getMeasurement(), app);

		List<MetricEntity> influxResults = new ArrayList<>();
		List<FluxTable> tables = influxDBClient.getQueryApi().query(flux);
		for (FluxTable fluxTable : tables) {
			List<FluxRecord> records = fluxTable.getRecords();
			for (FluxRecord fluxRecord : records) {
				influxResults.add(toMetricEntity(fluxRecord));
			}
		}

		if (CollectionUtils.isEmpty(influxResults)) {
			return results;
		}
		Map<String, MetricEntity> resourceCount = new HashMap<>(32);
		for (MetricEntity metricEntity : influxResults) {
			String resource = metricEntity.getResource();
			if (resourceCount.containsKey(resource)) {
				MetricEntity oldEntity = resourceCount.get(resource);
				oldEntity.addPassQps(metricEntity.getPassQps());
				oldEntity.addRtAndSuccessQps(metricEntity.getRt(), metricEntity.getSuccessQps());
				oldEntity.addBlockQps(metricEntity.getBlockQps());
				oldEntity.addExceptionQps(metricEntity.getExceptionQps());
				oldEntity.addCount(1);
			} else {
				resourceCount.put(resource, metricEntity);
			}
		}
		results = resourceCount.entrySet()
			.stream()
			.sorted((o1, o2) -> {
				MetricEntity e1 = o1.getValue();
				MetricEntity e2 = o2.getValue();
				int t = e2.getBlockQps().compareTo(e1.getBlockQps());
				if (t != 0) {
					return t;
				}
				return e2.getPassQps().compareTo(e1.getPassQps());
			})
			.map(Map.Entry::getKey)
			.collect(Collectors.toList());

		return results;
	}

	private static MetricEntity toMetricEntity(FluxRecord fluxRecord) {
		MetricEntity entity = new MetricEntity();
		entity.setApp(toStr(fluxRecord.getValueByKey("app")));
		entity.setResource(toStr(fluxRecord.getValueByKey("resource")));
		entity.setBlockQps(toLongZero(fluxRecord.getValueByKey("blockQps")));
		entity.setCount(toInt(fluxRecord.getValueByKey("count")));
		entity.setExceptionQps(toLongZero(fluxRecord.getValueByKey("exceptionQps")));
		entity.setPassQps(toLongZero(fluxRecord.getValueByKey("passQps")));
		entity.setSuccessQps(toLongZero(fluxRecord.getValueByKey("successQps")));
		entity.setRt(toDouble(fluxRecord.getValueByKey("rt")));
		entity.setTimestamp(toDate(toLong(fluxRecord.getValueByKey("timestamp"))));
		entity.setGmtCreate(toDate(toLong(fluxRecord.getValueByKey("gmtCreate"))));
		entity.setGmtModified(toDate(toLong(fluxRecord.getValueByKey("gmtModified"))));
		return entity;
	}

	private static Date toDate(Long time) {
		if (null != time) {
			return new Date(time);
		}
		return new Date();
	}

	private static String toStr(Object obj) {
		return String.valueOf(obj);
	}

	private static double toDouble(Object obj) {
		if (null != obj) {
			return Double.parseDouble(toStr(obj));
		}
		return 0;
	}

	private static Long toLongZero(Object obj) {
		if (null != obj) {
			return Long.valueOf(toStr(obj));
		}
		return 0L;
	}

	private static Long toLong(Object obj) {
		if (null != obj) {
			return Long.valueOf(toStr(obj));
		}
		return null;
	}

	private static int toInt(Object obj) {
		if (null != obj) {
			return Integer.parseInt(toStr(obj));
		}
		return 0;
	}
}
