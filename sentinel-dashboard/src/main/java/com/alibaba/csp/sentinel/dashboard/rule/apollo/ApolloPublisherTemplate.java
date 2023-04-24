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
package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import com.alibaba.csp.sentinel.dashboard.config.rule.ApolloRuleProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MachineEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public abstract class ApolloPublisherTemplate<T> implements DynamicRulePublisher<List<T>> {

	private static final Logger log = LoggerFactory.getLogger(ApolloPublisherTemplate.class);

	private final ApolloRuleProperties apolloRuleProperties;

	private final ApolloOpenApiClient apolloOpenApiClient;

	private final Converter<List<T>, String> converter;

	public ApolloPublisherTemplate(
		ApolloRuleProperties apolloRuleProperties,
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<List<T>, String> converter) {
		this.apolloRuleProperties = apolloRuleProperties;
		this.apolloOpenApiClient = apolloOpenApiClient;
		this.converter = converter;
	}

	@Override
	public void publish(MachineEntity machineEntity, List<T> rules) throws Exception {
		AssertUtil.notEmpty(machineEntity.getApp(), "app name cannot be empty");
		if (rules == null) {
			return;
		}

		log.info("Publish rules from apollo, app: {}, rule data: {}", machineEntity.getApp(), rules);

		// Increase the configuration
		String dataId = getDataId(machineEntity.getApp());
		OpenItemDTO openItemDTO = new OpenItemDTO();
		openItemDTO.setKey(dataId);
		openItemDTO.setValue(converter.convert(rules));
		openItemDTO.setComment("Program auto-join");
		openItemDTO.setDataChangeCreatedBy("sentinel");
		apolloOpenApiClient.createOrUpdateItem(
			apolloRuleProperties.getAppId(),
			apolloRuleProperties.getEnv(),
			apolloRuleProperties.getClusterName(),
			apolloRuleProperties.getNamespaceName(),
			openItemDTO);

		// Release configuration
		NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
		namespaceReleaseDTO.setEmergencyPublish(true);
		namespaceReleaseDTO.setReleaseComment("Modify or add configurations");
		namespaceReleaseDTO.setReleasedBy("some-operator");
		namespaceReleaseDTO.setReleaseTitle("Modify or add configurations");
		apolloOpenApiClient.publishNamespace(
			apolloRuleProperties.getAppId(),
			apolloRuleProperties.getEnv(),
			apolloRuleProperties.getClusterName(),
			apolloRuleProperties.getNamespaceName(),
			namespaceReleaseDTO);
	}

	public abstract String getDataId(String app);
}
