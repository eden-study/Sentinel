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
package com.alibaba.csp.sentinel.dashboard.rule.memory;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MachineEntity;
import com.alibaba.csp.sentinel.dashboard.discovery.AppManagement;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于内存拉取规则模板
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
public abstract class MemoryProviderTemplate<T> implements DynamicRuleProvider<List<T>> {

	private final AppManagement appManagement;

	public MemoryProviderTemplate(AppManagement appManagement) {
		this.appManagement = appManagement;
	}

	@Override
	public List<T> getRules(MachineEntity machineEntity) throws Exception {
		if (StringUtil.isBlank(machineEntity.getApp())) {
			return new ArrayList<>();
		}
		List<MachineInfo> list = appManagement.getDetailApp(machineEntity.getApp()).getMachines()
			.stream()
			.filter(MachineInfo::isHealthy)
			.sorted((e1, e2) -> Long.compare(e2.getLastHeartbeat(), e1.getLastHeartbeat())).collect(Collectors.toList());
		if (list.isEmpty()) {
			return new ArrayList<>();
		} else {
			MachineInfo machine = list.get(0);
			return getRules(machine.getApp(), machine.getIp(), machine.getPort());
		}
	}

	public abstract List<T> getRules(String app, String ip, int port) throws Exception;
}
