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
import com.alibaba.csp.sentinel.dashboard.rule.nacos.*;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

/**
 * Nacos 规则自动配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@ConditionalOnClass(ConfigService.class)
@ConditionalOnProperty(name = "sentinel.rule.type", havingValue = "nacos")
@EnableConfigurationProperties(NacosRuleProperties.class)
@Configuration(proxyBeanMethods = false)
public class NacosRuleAutoConfiguration {

	private final NacosRuleProperties nacosRuleProperties;

	public NacosRuleAutoConfiguration(NacosRuleProperties nacosRuleProperties) {
		this.nacosRuleProperties = nacosRuleProperties;
	}

	@Bean
	public ConfigService nacosConfigService() throws Exception {
		Properties properties = new Properties();
		properties.put("serverAddr", nacosRuleProperties.getServerAddr());
		properties.put("namespace", nacosRuleProperties.getNamespace());
		return ConfigFactory.createConfigService(properties);
	}

	@Bean
	public AuthorityRuleNacosProvider authorityRuleNacosProvider(
		ConfigService configService, Converter<String, List<AuthorityRuleEntity>> converter) {
		return new AuthorityRuleNacosProvider(nacosRuleProperties, configService, converter);
	}

	@Bean
	public AuthorityRuleNacosPublisher authorityRuleNacosPublisher(
		ConfigService configService, Converter<List<AuthorityRuleEntity>, String> converter) {
		return new AuthorityRuleNacosPublisher(nacosRuleProperties, configService, converter);
	}

	@Bean
	public DegradeRuleNacosProvider degradeRuleNacosProvider(
		ConfigService configService, Converter<String, List<DegradeRuleEntity>> converter) {
		return new DegradeRuleNacosProvider(nacosRuleProperties, configService, converter);
	}

	@Bean
	public DegradeRuleNacosPublisher degradeRuleNacosPublisher(
		ConfigService configService, Converter<List<DegradeRuleEntity>, String> converter) {
		return new DegradeRuleNacosPublisher(nacosRuleProperties, configService, converter);
	}

	@Bean
	public FlowRuleNacosProvider flowRuleNacosProvider(
		ConfigService configService, Converter<String, List<FlowRuleEntity>> converter) {
		return new FlowRuleNacosProvider(nacosRuleProperties, configService, converter);
	}

	@Bean
	public FlowRuleNacosPublisher flowRuleNacosPublisher(
		ConfigService configService, Converter<List<FlowRuleEntity>, String> converter) {
		return new FlowRuleNacosPublisher(nacosRuleProperties, configService, converter);
	}

	@Bean
	public ParamFlowRuleNacosProvider paramFlowRuleNacosProvider(
		ConfigService configService, Converter<String, List<ParamFlowRuleEntity>> converter) {
		return new ParamFlowRuleNacosProvider(nacosRuleProperties, configService, converter);
	}

	@Bean
	public ParamFlowRuleNacosPublisher paramFlowRuleNacosPublisher(
		ConfigService configService, Converter<List<ParamFlowRuleEntity>, String> converter) {
		return new ParamFlowRuleNacosPublisher(nacosRuleProperties, configService, converter);
	}

	@Bean
	public SystemRuleNacosProvider systemRuleNacosProvider(
		ConfigService configService, Converter<String, List<SystemRuleEntity>> converter) {
		return new SystemRuleNacosProvider(nacosRuleProperties, configService, converter);
	}

	@Bean
	public SystemRuleNacosPublisher systemRuleNacosPublisher(
		ConfigService configService, Converter<List<SystemRuleEntity>, String> converter) {
		return new SystemRuleNacosPublisher(nacosRuleProperties, configService, converter);
	}
}
