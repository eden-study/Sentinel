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
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public abstract class ApolloProviderTemplate<T> implements DynamicRuleProvider<List<T>> {

	private final ApolloRuleProperties apolloRuleProperties;

	private final ApolloOpenApiClient apolloOpenApiClient;

	private final Converter<String, List<T>> converter;

	public ApolloProviderTemplate(
		ApolloRuleProperties apolloRuleProperties,
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<String, List<T>> converter) {
		this.apolloRuleProperties = apolloRuleProperties;
		this.apolloOpenApiClient = apolloOpenApiClient;
		this.converter = converter;
	}

	@Override
	public List<T> getRules(MachineEntity machineEntity) throws Exception {
		String dataId = getDataId(machineEntity.getApp());
		OpenNamespaceDTO openNamespaceDTO =
			apolloOpenApiClient.getNamespace(
				apolloRuleProperties.getAppId(),
				apolloRuleProperties.getEnv(),
				apolloRuleProperties.getClusterName(),
				apolloRuleProperties.getNamespaceName());
		String rules =
			openNamespaceDTO.getItems().stream()
				.filter(p -> p.getKey().equals(dataId))
				.map(OpenItemDTO::getValue)
				.findFirst()
				.orElse("");

		if (StringUtil.isEmpty(rules)) {
			return new ArrayList<>();
		}
		return converter.convert(rules);
	}

	public abstract String getDataId(String app);
}
