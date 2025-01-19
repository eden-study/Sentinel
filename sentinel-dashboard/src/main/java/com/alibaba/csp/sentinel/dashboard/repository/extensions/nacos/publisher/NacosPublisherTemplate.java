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
package com.alibaba.csp.sentinel.dashboard.repository.extensions.nacos.publisher;

import com.alibaba.csp.sentinel.dashboard.config.rule.NacosRuleProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MachineEntity;
import com.alibaba.csp.sentinel.dashboard.repository.extensions.RulePublisher;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public abstract class NacosPublisherTemplate<T> implements RulePublisher<List<T>> {

	private static final Logger log = LoggerFactory.getLogger(NacosPublisherTemplate.class);

	private final NacosRuleProperties nacosRuleProperties;

	private final ConfigService configService;

	private final Converter<List<T>, String> converter;

	public NacosPublisherTemplate(
		NacosRuleProperties nacosRuleProperties,
		ConfigService configService,
		Converter<List<T>, String> converter) {
		this.nacosRuleProperties = nacosRuleProperties;
		this.configService = configService;
		this.converter = converter;
	}

	@Override
	public void publish(MachineEntity machineEntity, List<T> rules) throws Exception {
		AssertUtil.notEmpty(machineEntity.getApp(), "app name cannot be empty");
		if (rules == null) {
			return;
		}

		String rulesData = CollectionUtils.isEmpty(rules) ? "[]" : converter.convert(rules);
		log.info("Publish rules to nacos, app: {}, rule data: {}", machineEntity.getApp(), rulesData);
		configService.publishConfig(getDataId(machineEntity.getApp()), nacosRuleProperties.getGroupId(),
			rulesData);
	}

	public abstract String getDataId(String app);
}
