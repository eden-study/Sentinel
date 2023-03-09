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
package com.alibaba.csp.sentinel.dashboard.datasource.entity;

import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;

import java.util.Date;

/**
 * @author leyou
 */
public class MachineEntity {
    private Long id;
    private Date gmtCreate;
    private Date gmtModified;
    private String app;
    private String ip;
    private String hostname;
    private Date timestamp;
    private Integer port;

	public MachineEntity(Long id, Date gmtCreate, Date gmtModified, String app, String ip, String hostname, Date timestamp, Integer port) {
		this.id = id;
		this.gmtCreate = gmtCreate;
		this.gmtModified = gmtModified;
		this.app = app;
		this.ip = ip;
		this.hostname = hostname;
		this.timestamp = timestamp;
		this.port = port;
	}

	public static MachineEntityBuilder builder() {
		return new MachineEntityBuilder();
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public MachineInfo toMachineInfo() {
        MachineInfo machineInfo = new MachineInfo();

        machineInfo.setApp(app);
        machineInfo.setHostname(hostname);
        machineInfo.setIp(ip);
        machineInfo.setPort(port);
        machineInfo.setLastHeartbeat(timestamp.getTime());
        machineInfo.setHeartbeatVersion(timestamp.getTime());

        return machineInfo;
    }

    @Override
    public String toString() {
        return "MachineEntity{" +
            "id=" + id +
            ", gmtCreate=" + gmtCreate +
            ", gmtModified=" + gmtModified +
            ", app='" + app + '\'' +
            ", ip='" + ip + '\'' +
            ", hostname='" + hostname + '\'' +
            ", timestamp=" + timestamp +
            ", port=" + port +
            '}';
    }

	public static class MachineEntityBuilder {
		private Long id;
		private Date gmtCreate;
		private Date gmtModified;
		private String app;
		private String ip;
		private String hostname;
		private Date timestamp;
		private Integer port;

		MachineEntityBuilder() {
		}

		public MachineEntityBuilder id(Long id) {
			this.id = id;
			return this;
		}

		public MachineEntityBuilder gmtCreate(Date gmtCreate) {
			this.gmtCreate = gmtCreate;
			return this;
		}

		public MachineEntityBuilder gmtModified(Date gmtModified) {
			this.gmtModified = gmtModified;
			return this;
		}

		public MachineEntityBuilder app(String app) {
			this.app = app;
			return this;
		}

		public MachineEntityBuilder ip(String ip) {
			this.ip = ip;
			return this;
		}

		public MachineEntityBuilder hostname(String hostname) {
			this.hostname = hostname;
			return this;
		}

		public MachineEntityBuilder timestamp(Date timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public MachineEntityBuilder port(Integer port) {
			this.port = port;
			return this;
		}

		public MachineEntity build() {
			return new MachineEntity(id, gmtCreate, gmtModified, app, ip, hostname, timestamp, port);
		}

		public String toString() {
			return "MachineEntity.MachineEntityBuilder(id=" + this.id + ", gmtCreate=" + this.gmtCreate + ", gmtModified=" + this.gmtModified + ", app=" + this.app + ", ip=" + this.ip + ", hostname=" + this.hostname + ", timestamp=" + this.timestamp + ", port=" + this.port + ")";
		}
	}
}
