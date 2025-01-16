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
package com.alibaba.csp.sentinel.dashboard.repository.extensions.zookeeper.provider;

import com.alibaba.csp.sentinel.dashboard.config.rule.ZookeeperRuleProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MachineEntity;
import com.alibaba.csp.sentinel.dashboard.repository.extensions.DynamicRuleProvider;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

/**
 * Zookeeper 拉取规则模板
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public abstract class ZookeeperProviderTemplate<T> implements DynamicRuleProvider<List<T>> {

	private final ZookeeperRuleProperties zookeeperRuleProperties;

	private final CuratorFramework zkClient;

	private final Converter<String, List<T>> converter;

	public ZookeeperProviderTemplate(
		ZookeeperRuleProperties zookeeperRuleProperties,
		CuratorFramework zkClient,
		Converter<String, List<T>> converter) {
		this.zookeeperRuleProperties = zookeeperRuleProperties;
		this.zkClient = zkClient;
		this.converter = converter;
	}

	@Override
	public List<T> getRules(MachineEntity machineEntity) throws Exception {
		AssertUtil.notNull(machineEntity, "machineEntity cannot be null");

		String path = getPath(machineEntity.getApp());
		Stat stat = zkClient.checkExists().forPath(path);
		if (stat == null) {
			return new ArrayList<>(0);
		}
		byte[] bytes = zkClient.getData().forPath(path);
		if (null == bytes || bytes.length == 0) {
			return new ArrayList<>();
		}
		String s = new String(bytes);

		return converter.convert(s);
	}

	public abstract String getPath(String app);
}
