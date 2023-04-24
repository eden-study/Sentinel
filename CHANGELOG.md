# Changelog

Changelog of Sentinel.

## 1.8.6 (2022-10-20)

### Features

- 新增健康节点自动移除配置，避免频繁升级应用导致面板长期驻留无效的服务。
- 提高监控数据写入性能，增加 kafka 写入缓冲配置。

### Bug Fixes

- 简化配置，根据设定的组件类型动态排除 AutoConfiguration 装配类。
- 升级 Kafka 组件，解决阿里巴巴升级 Spring 框架导致的兼容问题。 [#3](https://github.com/shiyindaxiaojie/Sentinel/issues/3)

## 1.8.5 (2022-09-09)

### Features

- 新增规则数据持久化，可选项：memory（默认）、nacos（推荐）、apollo、zookeeper。
- 新增监控数据持久化，可选项：memory（默认）、influxdb（推荐）、elasticsearch。
- 监控面板优化，支持时间控件任意时间查询监控数据。为降低查询压力，每页控制在 5 条记录内。
