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
package com.alibaba.csp.sentinel.dashboard.repository.extensions.memory.publisher;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MachineEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.AppManagement;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.repository.extensions.DynamicRulePublisher;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public abstract class MemoryPublisherTemplate<T> implements DynamicRulePublisher<List<T>> {

	private static final Logger log = LoggerFactory.getLogger(MemoryPublisherTemplate.class);

	private final AppManagement appManagement;

	public MemoryPublisherTemplate(AppManagement appManagement) {
		this.appManagement = appManagement;
	}

	@Override
	public void publish(MachineEntity machineEntity, List<T> rules) throws Exception {
		if (StringUtil.isBlank(machineEntity.getApp())) {
			return;
		}
		if (rules == null) {
			return;
		}
		Set<MachineInfo> set = appManagement.getDetailApp(machineEntity.getApp()).getMachines();


		log.info("Update rules in memory, app: {}, rule data: {}", machineEntity.getApp(), rules);

		for (MachineInfo machine : set) {
			if (!machine.isHealthy()) {
				continue;
			}
			// TODO: parse the results
			publish(machineEntity.getApp(), machine.getIp(), machine.getPort(), rules);
		}
	}

	public abstract void publish(String app, String ip, int port, List<T> rules) throws Exception;
}
