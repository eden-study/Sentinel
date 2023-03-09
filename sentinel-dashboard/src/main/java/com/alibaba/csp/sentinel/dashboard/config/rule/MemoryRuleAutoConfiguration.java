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

import com.alibaba.csp.sentinel.dashboard.client.SentinelApiClient;
import com.alibaba.csp.sentinel.dashboard.discovery.AppManagement;
import com.alibaba.csp.sentinel.dashboard.rule.memory.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基于内存规则配置（默认开启）
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@ConditionalOnProperty(name = "sentinel.rule.type", havingValue = "memory")
@Configuration(proxyBeanMethods = false)
public class MemoryRuleAutoConfiguration {

	private final AppManagement appManagement;

	private final SentinelApiClient sentinelApiClient;

	public MemoryRuleAutoConfiguration(AppManagement appManagement, SentinelApiClient sentinelApiClient) {
		this.appManagement = appManagement;
		this.sentinelApiClient = sentinelApiClient;
	}

	@Bean
	public AuthorityRuleMemoryProvider authorityRuleApiProvider() {
		return new AuthorityRuleMemoryProvider(appManagement, sentinelApiClient);
	}

	@Bean
	public AuthorityRuleMemoryPublisher authorityRuleApiPublisher() {
		return new AuthorityRuleMemoryPublisher(appManagement, sentinelApiClient);
	}

	@Bean
	public DegradeRuleMemoryProvider degradeRuleApiProvider() {
		return new DegradeRuleMemoryProvider(appManagement, sentinelApiClient);
	}

	@Bean
	public DegradeRuleMemoryPublisher degradeRuleApiPublisher() {
		return new DegradeRuleMemoryPublisher(appManagement, sentinelApiClient);
	}

	@Bean
	public FlowRuleMemoryProvider flowRuleApiProvider() {
		return new FlowRuleMemoryProvider(appManagement, sentinelApiClient);
	}

	@Bean
	public FlowRuleMemoryPublisher flowRuleApiPublisher() {
		return new FlowRuleMemoryPublisher(appManagement, sentinelApiClient);
	}

	@Bean
	public ParamFlowRuleMemoryProvider paramFlowRuleApiProvider() {
		return new ParamFlowRuleMemoryProvider(appManagement, sentinelApiClient);
	}

	@Bean
	public ParamFlowRuleMemoryPublisher paramFlowRuleApiPublisher() {
		return new ParamFlowRuleMemoryPublisher(appManagement, sentinelApiClient);
	}

	@Bean
	public SystemRuleMemoryProvider systemRuleApiProvider() {
		return new SystemRuleMemoryProvider(appManagement, sentinelApiClient);
	}

	@Bean
	public SystemRuleMemoryPublisher systemRuleApiPublisher() {
		return new SystemRuleMemoryPublisher(appManagement, sentinelApiClient);
	}
}
