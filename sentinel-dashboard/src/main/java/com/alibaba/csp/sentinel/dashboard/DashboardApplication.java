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
package com.alibaba.csp.sentinel.dashboard;

import com.alibaba.csp.sentinel.init.InitExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * Sentinel dashboard application.
 *
 * @author Carpenter Lee
 */
@Slf4j
@SpringBootApplication
public class DashboardApplication {

    public static void main(String[] args) {
        triggerSentinelInit();

		SpringApplication app = new SpringApplicationBuilder(DashboardApplication.class)
			.web(WebApplicationType.SERVLET).build();
		app.setBannerMode(Banner.Mode.OFF);
		ConfigurableApplicationContext context = app.run(args);
		Environment env = context.getEnvironment();
		logApplicationServerAfterRunning(env);
    }

    private static void triggerSentinelInit() {
        new Thread(() -> InitExecutor.doInit()).start();
    }

	protected static void logApplicationServerAfterRunning(Environment env) {
		String applicationName = StringUtils.trimToEmpty(env.getProperty("spring.application.name"));
		String contextPath = StringUtils.trimToEmpty(env.getProperty("server.servlet.context-path"));
		int serverPort = NumberUtils.toInt(env.getProperty("server.port"));
		String protocol = env.containsProperty("server.ssl.key-store") ? "https" : "http";
		String localhostAddress = "localhost";
		String hostAddress;
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			hostAddress = localhostAddress;
			log.warn("The host name could not be determined, using `localhost` as fallback");
		}

		log.info(
			"\n----------------------------------------------------------\n\t"
				+ "Application '{}' is running! \n\t"
				+ "Profile(s): \t{}\n\t"
				+ "Local Access URL: \t{}://{}:{}{}\n\t"
				+ "External Access URL: \t{}://{}:{}{}"
				+ "\n----------------------------------------------------------",
			applicationName,
			env.getActiveProfiles(),
			protocol,
			localhostAddress,
			serverPort,
			contextPath,
			protocol,
			hostAddress,
			serverPort,
			contextPath);
	}
}
