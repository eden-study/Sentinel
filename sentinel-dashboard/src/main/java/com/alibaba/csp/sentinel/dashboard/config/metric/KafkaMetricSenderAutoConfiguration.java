package com.alibaba.csp.sentinel.dashboard.config.metric;

import com.alibaba.csp.sentinel.dashboard.metric.kafka.KafkaMetricSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;

/**
 * Kafka 监控自动配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@ConditionalOnProperty(name = "sentinel.metrics.sender.type", havingValue = "kafka")
@ConditionalOnClass({KafkaTemplate.class})
@EnableConfigurationProperties({KafkaProperties.class, KafkaMetricProperties.class})
@RequiredArgsConstructor
@Slf4j
@Configuration(proxyBeanMethods = false)
public class KafkaMetricSenderAutoConfiguration {

	private final KafkaProperties properties;

	@Bean
	public KafkaMetricSender kafkaMetricsProducer(
		KafkaMetricProperties kafkaMetricProperties, KafkaTemplate<String, String> kafkaTemplate) {
		return new KafkaMetricSender(kafkaMetricProperties, kafkaTemplate);
	}

	@Bean
	@ConditionalOnMissingBean(KafkaTemplate.class)
	public KafkaTemplate<?, ?> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory,
											 ProducerListener<Object, Object> kafkaProducerListener,
											 ObjectProvider<RecordMessageConverter> messageConverter) {
		KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
		messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
		kafkaTemplate.setProducerListener(kafkaProducerListener);
		kafkaTemplate.setDefaultTopic(this.properties.getTemplate().getDefaultTopic());
		return kafkaTemplate;
	}

	@Bean
	@ConditionalOnMissingBean(ProducerListener.class)
	public ProducerListener<Object, Object> kafkaProducerListener() {
		return new LoggingProducerListener<>();
	}
	@Bean
	@ConditionalOnMissingBean(ProducerFactory.class)
	public ProducerFactory<?, ?> kafkaProducerFactory(
		ObjectProvider<DefaultKafkaProducerFactoryCustomizer> customizers) {
		DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(
			this.properties.buildProducerProperties());
		String transactionIdPrefix = this.properties.getProducer().getTransactionIdPrefix();
		if (transactionIdPrefix != null) {
			factory.setTransactionIdPrefix(transactionIdPrefix);
		}
		customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));
		return factory;
	}
}
