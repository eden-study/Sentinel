package com.alibaba.csp.sentinel.dashboard.config.rule;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 规则转换器配置
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 1.8.2
 */
@Configuration(proxyBeanMethods = false)
public class RuleConverterAutoConfiguration {

	/**
	 * 授权规则实体编码器
	 *
	 * @return Converter
	 */
	@Bean
	public Converter<List<AuthorityRuleEntity>, String> authorityRuleEntityEncoder() {
		return JSON::toJSONString;
	}

	/**
	 * 授权规则实体解码器
	 *
	 * @return Converter
	 */
	@Bean
	public Converter<String, List<AuthorityRuleEntity>> authorityRuleEntityDecoder() {
		return s -> JSON.parseArray(s, AuthorityRuleEntity.class);
	}

	/**
	 * 流控规则实体编码器
	 *
	 * @return Converter
	 */
	@Bean
	public Converter<List<FlowRuleEntity>, String> flowRuleEntityEncoder() {
		return JSON::toJSONString;
	}

	/**
	 * 流控规则实体解码器
	 *
	 * @return Converter
	 */
	@Bean
	public Converter<String, List<FlowRuleEntity>> flowRuleEntityDecoder() {
		return s -> JSON.parseArray(s, FlowRuleEntity.class);
	}

	/**
	 * 降级规则实体编码器
	 *
	 * @return Converter
	 */
	@Bean
	public Converter<List<DegradeRuleEntity>, String> degradeEntityEncoder() {
		return JSON::toJSONString;
	}

	/**
	 * 降级规则实体解码器
	 *
	 * @return Converter
	 */
	@Bean
	public Converter<String, List<DegradeRuleEntity>> degradeEntityDecoder() {
		return s -> JSON.parseArray(s, DegradeRuleEntity.class);
	}

	/**
	 * 热点参数规则实体编码器
	 *
	 * @return Converter
	 */
	@Bean
	public Converter<List<ParamFlowRuleEntity>, String> paramFlowRuleEntityEncoder() {
		return JSON::toJSONString;
	}

	/**
	 * 热点参数规则实体解码器
	 *
	 * @return Converter
	 */
	@Bean
	public Converter<String, List<ParamFlowRuleEntity>> paramFlowRuleEntityDecoder() {
		return s -> JSON.parseArray(s, ParamFlowRuleEntity.class);
	}

	/**
	 * 系统参数规则实体编码器
	 *
	 * @return Converter
	 */
	@Bean
	public Converter<List<SystemRuleEntity>, String> systemRuleEntityEncoder() {
		return JSON::toJSONString;
	}

	/**
	 * 系统参数规则实体解码器
	 *
	 * @return Converter
	 */
	@Bean
	public Converter<String, List<SystemRuleEntity>> systemRuleEntityDecoder() {
		return s -> JSON.parseArray(s, SystemRuleEntity.class);
	}
}
