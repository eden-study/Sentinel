# 使用基础镜像
FROM openjdk:11-jdk-slim AS builder

# 指定构建模块
ARG MODULE=sentinel-dashboard

# 设置工作目录
WORKDIR /app

# 复制必要文件
COPY $MODULE/target/$MODULE.jar application.jar
COPY docker/entrypoint.sh entrypoint.sh

# 使用 Spring Boot 的分层模式提取 JAR 文件的依赖项
RUN java -Djarmode=layertools -jar application.jar extract

# 创建容器镜像
FROM openjdk:11-jdk-slim

# 定义元数据
LABEL maintainer="梦想歌 <shiyindaxiaojie@gmail.com>"
LABEL version="1.8.6"

# 指定构建参数
ARG USER=tmpuser
ARG GROUP=tmpgroup

# 设置环境变量
ENV JAVA_HOME /opt/jdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
ENV HOME "/app"
ENV TZ "Asia/Shanghai"
ENV LANG "C.UTF-8"
ENV XMS "512M"
ENV XMX "512M"
ENV XSS "256K"
ENV GC_MODE "G1"
ENV USE_GC_LOG "Y"
ENV USE_HEAP_DUMP "Y"
ENV USE_LARGE_PAGES "N"
ENV SPRING_PROFILES_ACTIVE "dev"
ENV SERVER_PORT "8080"
ENV MANAGEMENT_SERVER_PORT "9080"
ENV AUTH_ENABLED "true"
ENV AUTH_USERNAME "sentinel"
ENV AUTH_PASSWORD "sentinel"
ENV SENTINEL_RULE_TYPE "nacos"
ENV SENTINEL_RULE_NACOS_SERVER_ADDR "localhost:8848"
ENV SENTINEL_RULE_NACOS_NAMESPACE "demo"
ENV SENTINEL_RULE_NACOS_GROUP_ID "sentinel"
ENV SENTINEL_RULE_NACOS_USERNAME "nacos"
ENV SENTINEL_RULE_NACOS_PASSWORD "nacos"
ENV SENTINEL_RULE_APOLLO_PORTAL_URL "http://localhost:10034"
ENV SENTINEL_RULE_APOLLO_TOKEN ""
ENV SENTINEL_RULE_APOLLO_ENV ""
ENV SENTINEL_RULE_ZOOKEEPER_CONNECT_STRING "localhost:2181"
ENV SENTINEL_RULE_ZOOKEEPER_ROOT_PATH "/sentinel"
ENV SENTINEL_METRICS_TYPE "memory"
ENV INFLUX_URL "http://localhost:8086/"
ENV INFLUX_TOKEN ""
ENV INFLUX_ORG "sentinel"
ENV INFLUX_BUCKET "sentinel"
ENV INFLUX_LOG_LEVEL "NONE"
ENV INFLUX_READ_TIMEOUT "10s"
ENV INFLUX_WRITE_TIMEOUT "10s"
ENV INFLUX_CONNECT_TIMEOUT "10s"
ENV SENTINEL_METRICS_ELASTICSEARCH_INDEX_NAME "sentinel_metric"
ENV SPRING_ELASTICSEARCH_REST_URIS "http://localhost:9200"
ENV SPRING_ELASTICSEARCH_REST_CONNECTION_TIMEOUT "3000"
ENV SPRING_ELASTICSEARCH_REST_READ_TIMEOUT "5000"
ENV SPRING_ELASTICSEARCH_REST_USERNAME ""
ENV SPRING_ELASTICSEARCH_REST_PASSWORD ""
ENV SENTINEL_METRICS_SENDER_TYPE "none"
ENV SENTINEL_METRICS_SENDER_KAFKA_TOPIC "sentinel_metric"
ENV SPRING_KAFKA_PRODUCER_BOOTSTRAP_SERVERS "localhost:9092"
ENV SPRING_KAFKA_PRODUCER_BATCH_SIZE "4096"
ENV SPRING_KAFKA_PRODUCER_BUFFER_MEMORY "40960"

# 设置日志目录
RUN mkdir -p $HOME/logs \
	&& touch $HOME/logs/entrypoint.out \
	&& ln -sf /dev/stdout $HOME/logs/entrypoint.out \
	&& ln -sf /dev/stderr $HOME/logs/entrypoint.out

# 切换工作目录
WORKDIR $HOME

# 从基础镜像复制应用程序依赖项和模块
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader ./
COPY --from=builder /app/organization-dependencies ./
COPY --from=builder /app/modules-dependencies ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
COPY --from=builder /app/entrypoint.sh ./

# 创建普通用户
RUN groupadd -g 1000 $GROUP \
	&& useradd -u 1000 -g $GROUP -d $HOME -s /bin/bash $USER \
	&& chown -R $USER:$GROUP $HOME \
	&& chmod -R a+rwX $HOME \
	&& chmod 750 entrypoint.sh

# 切换到容器用户
USER $USER

# 暴露容器端口
EXPOSE $SERVER_PORT $MANAGEMENT_SERVER_PORT

# 设置启动脚本
CMD ["./entrypoint.sh"]
