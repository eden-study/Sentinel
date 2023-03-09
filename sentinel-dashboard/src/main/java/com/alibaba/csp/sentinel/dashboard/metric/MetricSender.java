package com.alibaba.csp.sentinel.dashboard.metric;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;

/**
 * 监控数据发送器
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public interface MetricSender {

	void send(Iterable<MetricEntity> iterable);
}
