package com.alibaba.csp.sentinel.dashboard.config.rule;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Sentinel 规则配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@Setter
@Getter
@ConfigurationProperties(prefix = NacosRuleProperties.PREFIX)
public class NacosRuleProperties {

	public static final String PREFIX = "sentinel.rule.nacos";

	/**
	 * Nacos 地址
	 */
	private String serverAddr = "localhost:8848";

	/**
	 * Nacos 命名空间
	 */
	private String namespace = "default";

	/**
	 * Nacos GroupId
	 */
	private String groupId = "SENTINEL_GROUP";

	/**
	 * Nacos 用户名
	 */
	private String username = "nacos";

	/**
	 * Nacos 密码
	 */
	private String password = "nacos";

	/**
	 * Nacos DataId 后缀
	 */
	private final DataIdSuffix dataIdSuffix = new DataIdSuffix();

	@Setter
	@Getter
	public static class DataIdSuffix {

		/**
		 * 授权规则后缀
		 */
		private String authRule = "-auth-rule";

		/**
		 * 降级规则后缀
		 */
		private String degradeRule = "-degrade-rule";

		/**
		 * 流控规则后缀
		 */
		private String flowRule = "-flow-rule";

		/**
		 * 热点参数后缀
		 */
		private String paramFlowRule = "-param-flow-rule";

		/**
		 * 系统规则后缀
		 */
		private String systemRule = "-system-rule";

		/**
		 * 集群流控规则后缀
		 */
		private String clusterRule = "-cluster-rule";

		/**
		 * 网关流控规则
		 */
		private String gatewayFlowRule = "-gateway-flow-rule";

		/**
		 * 网关API
		 */
		private String gatewayApi = "-gateway-api";
	}
}
