# Sentinel 流量治理平台

Sentinel 是阿里巴巴开源的流量治理平台，提供了 `流量控制`、`熔断降级`、`系统负载保护`、`黑白名单访问控制` 等功能。在实际的生产需求中，笔者进行了部分扩展：
1. 流控规则持久化：适配 `Apollo`、`Nacos`、`Zookeeper`
2. 监控数据持久化：适配 `InfluxDB`、`Kafka`、`Elasticsearch`
3. 监控面板优化：新增时间控件，允许在任意时刻内查询监控数据。

## 服务端概览

### 改造前

![](https://cdn.jsdelivr.net/gh/shiyindaxiaojie/eden-images/sentinel/sentinel-dashboard-overview-old.png)

### 改造后

![](https://cdn.jsdelivr.net/gh/shiyindaxiaojie/eden-images/sentinel/sentinel-dashboard-overview.png)

## 客户端集成

为了减少客户端集成的工作，您可以使用 [eden-architect](https://github.com/shiyindaxiaojie/eden-architect) 框架，只需要两步就可以完成 Sentinel 的集成。

1. 引入 Sentinel 依赖
````xml
<dependency>
    <groupId>org.ylzl</groupId>
    <artifactId>eden-sentinel-spring-cloud-starter</artifactId>
</dependency>
````
2. 开启 Sentinel 配置
````yaml
spring:
  cloud:
    sentinel: # 流量治理组件
      enabled: false # 默认关闭，请按需开启
      http-method-specify: true # 兼容 RESTful
      eager: true # 立刻刷新到 Dashboard
      transport:
        dashboard: localhost:8090
      datasource:
        flow:
          nacos:
            server-addr: ${spring.cloud.nacos.config.server-addr}
            namespace: ${spring.cloud.nacos.config.namespace}
            groupId: sentinel
            dataId: ${spring.application.name}-flow-rule
            rule-type: flow
            data-type: json
````

笔者提供了两种不同应用架构的示例，里面有集成 Sentinel 的示例。
* 面向领域模型的 **COLA 架构**，代码实例可以查看 [eden-demo-cola](https://github.com/shiyindaxiaojie/eden-demo-cola)
* 面向数据模型的 **分层架构**，代码实例请查看 [eden-demo-layer](https://github.com/shiyindaxiaojie/eden-demo-layer)

## 变更日志

请查阅 [CHANGELOG.md](https://github.com/shiyindaxiaojie/Sentinel/blob/1.8.x/CHANGELOG.md)
