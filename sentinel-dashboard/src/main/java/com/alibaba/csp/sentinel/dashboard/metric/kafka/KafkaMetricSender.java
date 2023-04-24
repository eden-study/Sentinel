package com.alibaba.csp.sentinel.dashboard.metric.kafka;

import com.alibaba.csp.sentinel.dashboard.config.metric.KafkaMetricProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.metric.MetricSender;
import com.alibaba.nacos.common.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * 基于 Kafka 发送监控数据
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@Slf4j
public class KafkaMetricSender<T> implements MetricSender {

	private final KafkaMetricProperties kafkaMetricProperties;

	private final KafkaTemplate<String, String> kafkaTemplate;

	public KafkaMetricSender(KafkaMetricProperties kafkaMetricProperties,
							 KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaMetricProperties = kafkaMetricProperties;
		this.kafkaTemplate = kafkaTemplate;
	}

	public void send(Iterable<MetricEntity> iterable) {
		iterable.forEach(entity -> {
			String data = JacksonUtils.toJson(entity);

			ListenableFuture<SendResult<String, String>> sendResult =
				kafkaTemplate.send(kafkaMetricProperties.getTopic(),
					entity.getResource(), data);

			sendResult.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

				@Override
				public void onFailure(Throwable e) {
					// 可以考虑失败就重试
					log.error("发送监控数据到 Kafka 异常", e);
				}

				@Override
				public void onSuccess(SendResult<String, String> sendResult) {
					// 可以考虑配合 acks=all 和 min.insync.replicas=2 确保消息成功提交到 Broker
				}
			});

		});
	}
}
