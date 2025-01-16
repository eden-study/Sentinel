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
package com.alibaba.csp.sentinel.dashboard.repository.extensions.nacos.provider;

import com.alibaba.csp.sentinel.dashboard.config.rule.NacosRuleProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MachineEntity;
import com.alibaba.csp.sentinel.dashboard.repository.extensions.DynamicRuleProvider;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public abstract class NacosProviderTemplate<T> implements DynamicRuleProvider<List<T>> {

	private final NacosRuleProperties nacosRuleProperties;

	private final ConfigService configService;

	private final Converter<String, List<T>> converter;

	public NacosProviderTemplate(
		NacosRuleProperties nacosRuleProperties,
		ConfigService configService,
		Converter<String, List<T>> converter) {
		this.nacosRuleProperties = nacosRuleProperties;
		this.configService = configService;
		this.converter = converter;
	}

	@Override
	public List<T> getRules(MachineEntity machineEntity) throws Exception {
		String rules =
			configService.getConfig(
				getDataId(machineEntity.getApp()), nacosRuleProperties.getGroupId(), 3000);
		if (StringUtil.isEmpty(rules)) {
			return new ArrayList<>();
		}
		return converter.convert(rules);
	}

	public abstract String getDataId(String app);
}
