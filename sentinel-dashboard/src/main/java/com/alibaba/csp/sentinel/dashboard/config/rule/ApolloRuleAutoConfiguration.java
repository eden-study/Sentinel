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
import com.alibaba.csp.sentinel.dashboard.repository.extensions.apollo.provider.*;
import com.alibaba.csp.sentinel.dashboard.repository.extensions.apollo.publisher.*;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Apollo 规则自动配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@ConditionalOnClass(ApolloOpenApiClient.class)
@ConditionalOnProperty(name = "sentinel.rule.type", havingValue = "apollo")
@EnableConfigurationProperties(ApolloRuleProperties.class)
@Configuration(proxyBeanMethods = false)
public class ApolloRuleAutoConfiguration {

	private final ApolloRuleProperties apolloRuleProperties;

	public ApolloRuleAutoConfiguration(ApolloRuleProperties apolloRuleProperties) {
		this.apolloRuleProperties = apolloRuleProperties;
	}

	@Bean
	public ApolloOpenApiClient apolloOpenApiClient() {
		return ApolloOpenApiClient.newBuilder()
			.withPortalUrl(apolloRuleProperties.getPortalUrl())
			.withToken(apolloRuleProperties.getToken())
			.build();
	}

	@Bean
	public AuthorityRuleApolloProvider authorityRuleApolloProvider(
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<String, List<AuthorityRuleEntity>> converter) {
		return new AuthorityRuleApolloProvider(apolloRuleProperties, apolloOpenApiClient, converter);
	}

	@Bean
	public AuthorityRuleApolloPublisher authorityRuleApolloPublisher(
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<List<AuthorityRuleEntity>, String> converter) {
		return new AuthorityRuleApolloPublisher(apolloRuleProperties, apolloOpenApiClient, converter);
	}

	@Bean
	public DegradeRuleApolloProvider degradeRuleApolloProvider(
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<String, List<DegradeRuleEntity>> converter) {
		return new DegradeRuleApolloProvider(apolloRuleProperties, apolloOpenApiClient, converter);
	}

	@Bean
	public DegradeRuleApolloPublisher degradeRuleApolloPublisher(
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<List<DegradeRuleEntity>, String> converter) {
		return new DegradeRuleApolloPublisher(apolloRuleProperties, apolloOpenApiClient, converter);
	}

	@Bean
	public FlowRuleApolloProvider flowRuleApolloProvider(
		ApolloOpenApiClient apolloOpenApiClient, Converter<String, List<FlowRuleEntity>> converter) {
		return new FlowRuleApolloProvider(apolloRuleProperties, apolloOpenApiClient, converter);
	}

	@Bean
	public FlowRuleApolloPublisher flowRuleApolloPublisher(
		ApolloOpenApiClient apolloOpenApiClient, Converter<List<FlowRuleEntity>, String> converter) {
		return new FlowRuleApolloPublisher(apolloRuleProperties, apolloOpenApiClient, converter);
	}

	@Bean
	public ParamFlowRuleApolloProvider paramFlowRuleApolloProvider(
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<String, List<ParamFlowRuleEntity>> converter) {
		return new ParamFlowRuleApolloProvider(apolloRuleProperties, apolloOpenApiClient, converter);
	}

	@Bean
	public ParamFlowRuleApolloPublisher paramFlowRuleApolloPublisher(
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<List<ParamFlowRuleEntity>, String> converter) {
		return new ParamFlowRuleApolloPublisher(apolloRuleProperties, apolloOpenApiClient, converter);
	}

	@Bean
	public SystemRuleApolloProvider systemRuleApolloProvider(
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<String, List<SystemRuleEntity>> converter) {
		return new SystemRuleApolloProvider(apolloRuleProperties, apolloOpenApiClient, converter);
	}

	@Bean
	public SystemRuleApolloPublisher systemRuleApolloPublisher(
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<List<SystemRuleEntity>, String> converter) {
		return new SystemRuleApolloPublisher(apolloRuleProperties, apolloOpenApiClient, converter);
	}
}
