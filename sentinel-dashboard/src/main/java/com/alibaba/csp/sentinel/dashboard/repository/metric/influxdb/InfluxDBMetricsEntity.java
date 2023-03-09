package com.alibaba.csp.sentinel.dashboard.repository.metric.influxdb;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Builder;
import lombok.Data;

/**
 * 监控数据实体模型
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@Builder
@Data
@Measurement(name = "sentinel_metric")
public class InfluxDBMetricsEntity {

	@Column(name = "gmtCreate", tag = true)
	private Long gmtCreate;

	@Column(name = "gmtModified")
	private Long gmtModified;

	@Column(name = "app", tag = true)
	private String app;

	@Column(name = "timestamp", tag = true)
	private Long timestamp;

	@Column(name = "resource", tag = true)
	private String resource;

	@Column(name = "passQps", tag = true)
	private Long passQps;

	@Column(name = "successQps", tag = true)
	private Long successQps;

	@Column(name = "blockQps", tag = true)
	private Long blockQps;

	@Column(name = "exceptionQps", tag = true)
	private Long exceptionQps;

	@Column(name = "rt", tag = true)
	private double rt;

	@Column(name = "count", tag = true)
	private Integer count;

	@Column(name = "resourceCode", tag = true)
	private Integer resourceCode;
}
