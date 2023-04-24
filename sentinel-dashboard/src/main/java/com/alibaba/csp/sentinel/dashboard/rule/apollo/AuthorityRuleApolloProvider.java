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
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;

import java.util.List;

/**
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public class AuthorityRuleApolloProvider extends ApolloProviderTemplate<AuthorityRuleEntity> {

	private final ApolloRuleProperties apolloRuleProperties;

	public AuthorityRuleApolloProvider(
		ApolloRuleProperties apolloRuleProperties,
		ApolloOpenApiClient apolloOpenApiClient,
		Converter<String, List<AuthorityRuleEntity>> converter) {
		super(apolloRuleProperties, apolloOpenApiClient, converter);
		this.apolloRuleProperties = apolloRuleProperties;
	}

	@Override
	public String getDataId(String app) {
		return ApolloRuleUtils.getDataId(app, apolloRuleProperties.getDataId().getAuthRule());
	}
}
