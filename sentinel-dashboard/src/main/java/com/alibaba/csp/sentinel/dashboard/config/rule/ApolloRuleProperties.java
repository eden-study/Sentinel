package com.alibaba.csp.sentinel.dashboard.config.rule;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Apollo 配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@Setter
@Getter
@ConfigurationProperties(prefix = ApolloRuleProperties.PREFIX)
public class ApolloRuleProperties {

	public static final String PREFIX = "sentinel.rule.apollo";

	/**
	 * 开关
	 */
	private boolean enabled;

	/**
	 * Apollo 地址
	 */
	private String portalUrl = "http://localhost:10034";

	/**
	 * Apollo 密钥
	 */
	private String token;

	private String appId = "sentinel";

	private String env = "dev";

	private String clusterName = "sentinel";

	private String namespaceName = "sentinel";

	/**
	 * Apollo DataId
	 */
	private final DataId dataId = new DataId();

	@Setter
	@Getter
	public static class DataId {

		/**
		 * 授权规则
		 */
		private String authRule = "-auth-rule";

		/**
		 * 降级规则
		 */
		private String degradeRule = "-degrade-rule";

		/**
		 * 流控规则
		 */
		private String flowRule = "-flow-rule";

		/**
		 * 热点参数
		 */
		private String paramFlowRule = "-param-flow-rule";

		/**
		 * 系统规则
		 */
		private String systemRule = "-system-rule";

		/**
		 * 集群流控规则
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
