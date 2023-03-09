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
package com.alibaba.csp.sentinel.dashboard.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@EnableConfigurationProperties(DashboardProperties.class)
@Configuration(proxyBeanMethods = false)
public class DashboardAutoConfiguration implements InitializingBean {

    private final DashboardProperties dashboardProperties;

	@Override
	public void afterPropertiesSet() {
		if (dashboardProperties.getApp().getHideAppNoMachineMillis() > 0L) {
			DashboardConfig.putCache(DashboardConfig.CONFIG_HIDE_APP_NO_MACHINE_MILLIS,
				dashboardProperties.getApp().getHideAppNoMachineMillis());
		}
		if (dashboardProperties.getRemoveAppNoMachineMillis() > 0L) {
			DashboardConfig.putCache(DashboardConfig.CONFIG_REMOVE_APP_NO_MACHINE_MILLIS,
				dashboardProperties.getRemoveAppNoMachineMillis());
		}
		if (dashboardProperties.getAutoRemoveMachineMillis() > 0L) {
			DashboardConfig.putCache(DashboardConfig.CONFIG_AUTO_REMOVE_MACHINE_MILLIS,
				dashboardProperties.getAutoRemoveMachineMillis());
		}
		if (dashboardProperties.getUnhealthyMachineMillis() > 0L) {
			DashboardConfig.putCache(DashboardConfig.CONFIG_UNHEALTHY_MACHINE_MILLIS,
				dashboardProperties.getUnhealthyMachineMillis());
		}
	}
}
