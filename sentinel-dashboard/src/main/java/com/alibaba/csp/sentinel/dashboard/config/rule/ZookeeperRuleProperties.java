package com.alibaba.csp.sentinel.dashboard.config.rule;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Zookeeper 配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@Setter
@Getter
@ConfigurationProperties(prefix = ZookeeperRuleProperties.PREFIX)
public class ZookeeperRuleProperties {

	public static final String PREFIX = "sentinel.rule.zookeeper";

	/**
	 * 开关
	 */
	private boolean enabled;

	/**
	 * 连接地址
	 */
	private String connectString = "localhost:2181";

	/**
	 * 重试次数
	 */
	private Integer maxRetries = 3;

	/**
	 * 睡眠时间
	 */
	private Integer baseSleepTimeMs = 1000;

	/**
	 * 根目录
	 */
	private String rootPath = "/sentinel";

	/**
	 * 子目录
	 */
	private final RulePath rulePath = new RulePath();

	@Setter
	@Getter
	public static class RulePath {

		/**
		 * 授权规则
		 */
		private String authRule = "/auth-rule";

		/**
		 * 降级规则
		 */
		private String degradeRule = "/degrade-rule";

		/**
		 * 流控规则
		 */
		private String flowRule = "/flow-rule";

		/**
		 * 热点参数
		 */
		private String paramFlowRule = "/param-flow-rule";

		/**
		 * 系统规则
		 */
		private String systemRule = "/system-rule";

		/**
		 * 集群流控规则
		 */
		private String clusterRule = "/cluster-rule";
	}
}
