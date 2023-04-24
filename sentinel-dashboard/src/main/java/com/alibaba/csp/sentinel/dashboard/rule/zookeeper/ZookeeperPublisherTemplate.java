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
package com.alibaba.csp.sentinel.dashboard.rule.zookeeper;

import com.alibaba.csp.sentinel.dashboard.config.rule.ZookeeperRuleProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MachineEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Zookeeper 发布规则模板
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public abstract class ZookeeperPublisherTemplate<T> implements DynamicRulePublisher<List<T>> {

	private static final Logger log = LoggerFactory.getLogger(ZookeeperPublisherTemplate.class);

	private final ZookeeperRuleProperties zookeeperRuleProperties;

	private final CuratorFramework zkClient;

	private final Converter<List<T>, String> converter;

	public ZookeeperPublisherTemplate(
		ZookeeperRuleProperties zookeeperRuleProperties,
		CuratorFramework zkClient,
		Converter<List<T>, String> converter) {
		this.zookeeperRuleProperties = zookeeperRuleProperties;
		this.zkClient = zkClient;
		this.converter = converter;
	}

	@Override
	public void publish(MachineEntity machineEntity, List<T> rules) throws Exception {
		AssertUtil.notNull(machineEntity, "machineEntity cannot be null");
		AssertUtil.notEmpty(machineEntity.getApp(), "app name cannot be empty");

		String path = getPath(machineEntity.getApp());
		Stat stat = zkClient.checkExists().forPath(path);
		if (stat == null) {
			zkClient
				.create()
				.creatingParentContainersIfNeeded()
				.withMode(CreateMode.PERSISTENT)
				.forPath(path, null);
		}
		log.info("Publish rules from zookeeper, app: {}, rule data: {}", machineEntity.getApp(), rules);
		byte[] data =
			CollectionUtils.isEmpty(rules) ? "[]".getBytes() : converter.convert(rules).getBytes();
		zkClient.setData().forPath(path, data);
	}

	public abstract String getPath(String app);
}
