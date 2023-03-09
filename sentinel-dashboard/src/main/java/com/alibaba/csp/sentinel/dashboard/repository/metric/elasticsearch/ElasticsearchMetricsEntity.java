package com.alibaba.csp.sentinel.dashboard.repository.metric.elasticsearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;

/**
 * 监控数据 ES 实体模型
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@Data
@Document(indexName = "#{@elasticsearchMetricsProperties.getIndexName()}.#{T(java.time.LocalDate).now().toString()}", createIndex = false)
@Setting(shards = 1, replicas = 0)
public class ElasticsearchMetricsEntity {

	@Id
	private String id;

	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="uuuu-MM-dd'T'HH:mm:ss.SSSX", timezone ="GMT+8")
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
	private Date gmtCreate;

	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="uuuu-MM-dd'T'HH:mm:ss.SSSX", timezone ="GMT+8")
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
	private Date gmtModified;

	@Field(type = FieldType.Keyword)
	private String app;

	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern ="uuuu-MM-dd'T'HH:mm:ss.SSSX", timezone ="GMT+8")
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
	private Date timestamp;

	@Field(type = FieldType.Text)
	private String resource;

	@Field(type = FieldType.Long)
	private Long passQps;

	@Field(type = FieldType.Long)
	private Long successQps;

	@Field(type = FieldType.Long)
	private Long blockQps;

	@Field(type = FieldType.Long)
	private Long exceptionQps;

	@Field(type = FieldType.Double)
	private double rt;

	@Field(type = FieldType.Integer)
	private int count;

	@Field(type = FieldType.Integer)
	private int resourceCode;
}
