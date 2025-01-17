#!/bin/bash

JAVA_MAJOR_VERSION=$(java -version 2>&1 | sed -E -n 's/.* version "([0-9]*).*$/\1/p')

JAVA_OPTS="${JAVA_OPTS} -server"
JAVA_OPTS="${JAVA_OPTS} -XX:+UnlockExperimentalVMOptions -XX:+UnlockDiagnosticVMOptions"
JAVA_OPTS="${JAVA_OPTS} -XX:+AlwaysPreTouch -XX:+PrintFlagsFinal -XX:-DisplayVMOutput -XX:-OmitStackTraceInFastThrow"
JAVA_OPTS="${JAVA_OPTS} -Xms${XMS:-1G} -Xmx${XMX:-1G} -Xss${XSS:-256K}"
JAVA_OPTS="${JAVA_OPTS} -XX:MetaspaceSize=${METASPACE_SIZE:-128M} -XX:MaxMetaspaceSize=${MAX_METASPACE_SIZE:-256M}"
JAVA_OPTS="${JAVA_OPTS} -XX:MaxGCPauseMillis=${MAX_GC_PAUSE_MILLIS:-200}"

if [ "${GC_MODE}" = "ShenandoahGC" ]; then
	echo "GC mode is ShenandoahGC"
	JAVA_OPTS="${JAVA_OPTS} -XX:+UseShenandoahGC"
elif [ "${GC_MODE}" = "ZGC" ]; then
	echo "GC mode is ZGC"
	JAVA_OPTS="${JAVA_OPTS} -XX:+UseZGC"
elif [ "${GC_MODE}" = "G1" ]; then
	echo "GC mode is G1"
	JAVA_OPTS="${JAVA_OPTS} -XX:+UseG1GC"
	JAVA_OPTS="${JAVA_OPTS} -XX:InitiatingHeapOccupancyPercent=${INITIATING_HEAP_OCCUPANCY_PERCENT:-45}"
	JAVA_OPTS="${JAVA_OPTS} -XX:G1ReservePercent=${G1_RESERVE_PERCENT:-10} -XX:G1HeapWastePercent=${G1_HEAP_WASTE_PERCENT:-5} "
	JAVA_OPTS="${JAVA_OPTS} -XX:G1NewSizePercent=${G1_NEW_SIZE_PERCENT:-50} -XX:G1MaxNewSizePercent=${G1_MAX_NEW_SIZE_PERCENT:-50}"
	JAVA_OPTS="${JAVA_OPTS} -XX:G1MixedGCCountTarget=${G1_MIXED_GCCOUNT_TARGET:-8}"
	JAVA_OPTS="${JAVA_OPTS} -XX:G1MixedGCLiveThresholdPercent=${G1_MIXED_GCLIVE_THRESHOLD_PERCENT:-65}"
	JAVA_OPTS="${JAVA_OPTS} -XX:+UseStringDeduplication -XX:+ParallelRefProcEnabled"
elif [ "${GC_MODE}" = "CMS" ]; then
	echo "GC mode is CMS"
	JAVA_OPTS="${JAVA_OPTS} -XX:+UseConcMarkSweepGC -Xmn${XMN:-512m}"
	JAVA_OPTS="${JAVA_OPTS} -XX:ParallelGCThreads=${PARALLEL_GC_THREADS:-2} -XX:ConcGCThreads=${CONC_GC_THREADS:-1}"
	JAVA_OPTS="${JAVA_OPTS} -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=${CMS_INITIATING_HEAP_OCCUPANCY_PERCENT:-92}"
	JAVA_OPTS="${JAVA_OPTS} -XX:+CMSClassUnloadingEnabled -XX:+CMSScavengeBeforeRemark"
	if [ "$JAVA_MAJOR_VERSION" -le "8" ] ; then
		JAVA_OPTS="${JAVA_OPTS} -XX:+CMSIncrementalMode -XX:CMSFullGCsBeforeCompaction=${CMS_FULL_GCS_BEFORE_COMPACTION:-5}"
		JAVA_OPTS="${JAVA_OPTS} -XX:+ExplicitGCInvokesConcurrent -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses"
	fi
fi

if [ "${USE_GC_LOG}" = "Y" ]; then
    echo "GC log path is '${HOME}/logs/jvm_gc.log'."
    JAVA_OPTS="${JAVA_OPTS} -XX:+PrintVMOptions"
    if [ "$JAVA_MAJOR_VERSION" -gt "8" ] ; then
        JAVA_OPTS="${JAVA_OPTS} -Xlog:gc:file=${HOME}/logs/jvm_gc-%p-%t.log:tags,uptime,time,level:filecount=${GC_LOG_FILE_COUNT:-10},filesize=${GC_LOG_FILE_SIZE:-100M}"
	else
		JAVA_OPTS="${JAVA_OPTS} -Xloggc:${HOME}/logs/jvm_gc.log -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps"
		JAVA_OPTS="${JAVA_OPTS} -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=${GC_LOG_FILE_COUNT:-10} -XX:GCLogFileSize=${GC_LOG_FILE_SIZE:-100M}"
		JAVA_OPTS="${JAVA_OPTS} -XX:+PrintGCCause -XX:+PrintGCApplicationStoppedTime"
		JAVA_OPTS="${JAVA_OPTS} -XX:+PrintTLAB -XX:+PrintReferenceGC -XX:+PrintHeapAtGC"
		JAVA_OPTS="${JAVA_OPTS} -XX:+FlightRecorder -XX:+PrintSafepointStatistics -XX:PrintSafepointStatisticsCount=1"
        JAVA_OPTS="${JAVA_OPTS} -XX:+DebugNonSafepoints -XX:+SafepointTimeout -XX:SafepointTimeoutDelay=500"
	fi
fi

if [ ! -d "${HOME}/logs" ]; then
  mkdir ${HOME}/logs
fi

if [ "${USE_HEAP_DUMP}" = "Y" ]; then
	echo "Heap dump path is '${HOME}/logs/jvm_heap_dump.hprof'."
	JAVA_OPTS="${JAVA_OPTS} -XX:HeapDumpPath=${HOME}/logs/jvm_heap_dump.hprof -XX:+HeapDumpOnOutOfMemoryError"
fi

if [ "${USE_LARGE_PAGES}" = "Y" ]; then
	echo "Use large pages."
	JAVA_OPTS="${JAVA_OPTS} -XX:+UseLargePages"
fi

if [ "${JDWP_DEBUG:-N}" = "Y" ]; then
	echo "Attach to remote JVM using port ${JDWP_PORT:-5005}."
	JAVA_OPTS="${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=${JDWP_PORT:-5005},server=y,suspend=n"
fi

JAVA_OPTS="${JAVA_OPTS} -Dserver.port=${SERVER_PORT} -Dmanagement.server.port=${MANAGEMENT_SERVER_PORT}"

# 认证
JAVA_OPTS="${JAVA_OPTS} -Dauth.enabled=${AUTH_ENABLED:true}"
JAVA_OPTS="${JAVA_OPTS} -Dauth.username=${AUTH_USERNAME:sentinel}"
JAVA_OPTS="${JAVA_OPTS} -Dauth.password=${AUTH_PASSWORD:sentinel}"

# 规则存储类型，可选项：memory（默认）、nacos（推荐）、apollo、zookeeper
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.type=${SENTINEL_RULE_TYPE:nacos}"

# Nacos 存储规则，如果您设置了 sentinel.metrics.type=nacos，需要调整相关配置
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.nacos.server-addr=${SENTINEL_RULE_NACOS_SERVER_ADDR:localhost:8848}"
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.nacos.namespace=${SENTINEL_RULE_NACOS_NAMESPACE:demo}"
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.nacos.group-id=${SENTINEL_RULE_NACOS_GROUP_ID:sentinel}"
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.nacos.username=${SENTINEL_RULE_NACOS_USERNAME:nacos}"
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.nacos.password=${SENTINEL_RULE_NACOS_PASSWORD:nacos}"

# Apollo 存储规则，如果您设置了 sentinel.metrics.type=apollo，需要调整相关配置
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.apollo.portal-url=${SENTINEL_RULE_APOLLO_PORTAL_URL}"
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.apollo.token=${SENTINEL_RULE_APOLLO_TOKEN}"
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.apollo.env=${SENTINEL_RULE_APOLLO_ENV}"

# Zookeeper 存储规则，如果您设置了 sentinel.metrics.type=zookeeper，需要调整相关配置
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.zookeeper.connect-string=${SENTINEL_RULE_ZOOKEEPER_CONNECT_STRING}"
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.rule.zookeeper.root-path=${SENTINEL_RULE_ZOOKEEPER_ROOT_PATH}"

# 监控存储类型，可选项：memory（默认）、influxdb（推荐）、elasticsearch
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.metrics.type=${SENTINEL_METRICS_TYPE:memory}"

# InfluxDB 存储监控数据，如果您设置了 sentinel.metrics.type=influxdb，需要调整相关配置
JAVA_OPTS="${JAVA_OPTS} -Dinflux.url=${INFLUX_URL}"
JAVA_OPTS="${JAVA_OPTS} -Dinflux.token=${INFLUX_TOKEN}"
JAVA_OPTS="${JAVA_OPTS} -Dinflux.org=${INFLUX_ORG:sentinel}"
JAVA_OPTS="${JAVA_OPTS} -Dinflux.bucket=${INFLUX_BUCKET:sentinel}"
JAVA_OPTS="${JAVA_OPTS} -Dinflux.log-level=${INFLUX_LOG_LEVEL:NONE}"
JAVA_OPTS="${JAVA_OPTS} -Dinflux.read-timeout=${INFLUX_READ_TIMEOUT}"
JAVA_OPTS="${JAVA_OPTS} -Dinflux.write-timeout=${INFLUX_WRITE_TIMEOUT}"
JAVA_OPTS="${JAVA_OPTS} -Dinflux.connect-timeout=${INFLUX_CONNECT_TIMEOUT}"

# Elasticsearch 存储监控数据，如果您设置了 sentinel.metrics.type=elasticsearch，需要调整相关配置
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.metrics.elasticsearch.index-name=${SENTINEL_METRICS_ELASTICSEARCH_INDEX_NAME:sentinel_metric}"
JAVA_OPTS="${JAVA_OPTS} -Dspring.elasticsearch.rest.uris=${SPRING_ELASTICSEARCH_REST_URIS}"
JAVA_OPTS="${JAVA_OPTS} -Dspring.elasticsearch.rest.connection-timeout=${SPRING_ELASTICSEARCH_REST_CONNECTION_TIMEOUT:3000}"
JAVA_OPTS="${JAVA_OPTS} -Dspring.elasticsearch.rest.read-timeout=${SPRING_ELASTICSEARCH_REST_READ_TIMEOUT:5000}"
JAVA_OPTS="${JAVA_OPTS} -Dspring.elasticsearch.rest.username=${SPRING_ELASTICSEARCH_REST_USERNAME}"
JAVA_OPTS="${JAVA_OPTS} -Dspring.elasticsearch.rest.password=${SPRING_ELASTICSEARCH_REST_PASSWORD}"

# Kafka 缓冲监控数据，降低底层存储组件写入压力。可选项：none（默认不启用）、kafka（推荐）
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.metrics.sender.type=${SENTINEL_METRICS_SENDER_TYPE:none}"
JAVA_OPTS="${JAVA_OPTS} -Dsentinel.metrics.sender.kafka.topic=${SENTINEL_METRICS_SENDER_KAFKA_TOPIC:sentinel_metric}"
JAVA_OPTS="${JAVA_OPTS} -Dspring.kafka.producer.bootstrap-servers=${SPRING_KAFKA_PRODUCER_BOOTSTRAP_SERVERS:localhost:9092}"
JAVA_OPTS="${JAVA_OPTS} -Dspring.kafka.producer.batch-size=${SPRING_KAFKA_PRODUCER_BATCH_SIZE:4096}"
JAVA_OPTS="${JAVA_OPTS} -Dspring.kafka.producer.buffer-memory=${SPRING_KAFKA_PRODUCER_BUFFER_MEMORY:40960}"

exec java ${JAVA_OPTS} -noverify -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "org.ylzl.eden.demo.ColaApplication" "$@"
