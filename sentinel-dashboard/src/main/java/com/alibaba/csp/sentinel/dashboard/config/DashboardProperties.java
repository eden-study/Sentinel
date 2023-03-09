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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = DashboardProperties.PREFIX)
public class DashboardProperties {

	public static final String PREFIX = "sentinel.dashboard";

	private int removeAppNoMachineMillis = 0;

	private int unhealthyMachineMillis = 30000;

	private int autoRemoveMachineMillis = 0;

	private final App app = new App();

	@Data
	public static class App {

		private int hideAppNoMachineMillis = 0;
	}
}
