/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.config.rule;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.dashboard.repository.extensions.zookeeper.provider.*;
import com.alibaba.csp.sentinel.dashboard.repository.extensions.zookeeper.publisher.*;
import com.alibaba.csp.sentinel.datasource.Converter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Zookeeper 规则自动配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@ConditionalOnClass(CuratorFramework.class)
@ConditionalOnProperty(name = "sentinel.rule.type", havingValue = "zookeeper")
@EnableConfigurationProperties(ZookeeperRuleProperties.class)
@Configuration(proxyBeanMethods = false)
public class ZookeeperRuleAutoConfiguration {

	private final ZookeeperRuleProperties zookeeperRuleProperties;

	public ZookeeperRuleAutoConfiguration(ZookeeperRuleProperties zookeeperRuleProperties) {
		this.zookeeperRuleProperties = zookeeperRuleProperties;
	}

	@Bean
	public CuratorFramework zkClient() {
		CuratorFramework zkClient =
			CuratorFrameworkFactory.newClient(
				zookeeperRuleProperties.getConnectString(),
				new ExponentialBackoffRetry(
					zookeeperRuleProperties.getBaseSleepTimeMs(), zookeeperRuleProperties.getMaxRetries()));
		zkClient.start();
		return zkClient;
	}

	@Bean
	public AuthorityRuleZookeeperProvider authorityRuleZookeeperProvider(
		CuratorFramework zkClient, Converter<String, List<AuthorityRuleEntity>> converter) {
		return new AuthorityRuleZookeeperProvider(zookeeperRuleProperties, zkClient, converter);
	}

	@Bean
	public AuthorityRuleZookeeperPublisher authorityRuleZookeeperPublisher(
		CuratorFramework zkClient, Converter<List<AuthorityRuleEntity>, String> converter) {
		return new AuthorityRuleZookeeperPublisher(zookeeperRuleProperties, zkClient, converter);
	}

	@Bean
	public DegradeRuleZookeeperProvider degradeRuleZookeeperProvider(
		CuratorFramework zkClient, Converter<String, List<DegradeRuleEntity>> converter) {
		return new DegradeRuleZookeeperProvider(zookeeperRuleProperties, zkClient, converter);
	}

	@Bean
	public DegradeRuleZookeeperPublisher degradeRuleZookeeperPublisher(
		CuratorFramework zkClient, Converter<List<DegradeRuleEntity>, String> converter) {
		return new DegradeRuleZookeeperPublisher(zookeeperRuleProperties, zkClient, converter);
	}

	@Bean
	public FlowRuleZookeeperProvider flowRuleZookeeperProvider(
		CuratorFramework zkClient, Converter<String, List<FlowRuleEntity>> converter) {
		return new FlowRuleZookeeperProvider(zookeeperRuleProperties, zkClient, converter);
	}

	@Bean
	public FlowRuleZookeeperPublisher flowRuleZookeeperPublisher(
		CuratorFramework zkClient, Converter<List<FlowRuleEntity>, String> converter) {
		return new FlowRuleZookeeperPublisher(zookeeperRuleProperties, zkClient, converter);
	}

	@Bean
	public ParamFlowRuleZookeeperProvider paramFlowRuleZookeeperProvider(
		CuratorFramework zkClient, Converter<String, List<ParamFlowRuleEntity>> converter) {
		return new ParamFlowRuleZookeeperProvider(zookeeperRuleProperties, zkClient, converter);
	}

	@Bean
	public ParamFlowRuleZookeeperPublisher paramFlowRuleZookeeperPublisher(
		CuratorFramework zkClient, Converter<List<ParamFlowRuleEntity>, String> converter) {
		return new ParamFlowRuleZookeeperPublisher(zookeeperRuleProperties, zkClient, converter);
	}

	@Bean
	public SystemRuleZookeeperProvider systemRuleNacosProvider(
		CuratorFramework zkClient, Converter<String, List<SystemRuleEntity>> converter) {
		return new SystemRuleZookeeperProvider(zookeeperRuleProperties, zkClient, converter);
	}

	@Bean
	public SystemRuleZookeeperPublisher systemRuleNacosPublisher(
		CuratorFramework zkClient, Converter<List<SystemRuleEntity>, String> converter) {
		return new SystemRuleZookeeperPublisher(zookeeperRuleProperties, zkClient, converter);
	}
}
