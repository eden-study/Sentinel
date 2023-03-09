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
package com.alibaba.csp.sentinel.slots.block.flow.controller;

import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.util.TimeUtil;

/**
 * @author jialiang.linjl
 * @since 1.4.0
 */
public class WarmUpRateLimiterController extends WarmUpController {

    private final int timeoutInMs;
    private final AtomicLong latestPassedTime = new AtomicLong(-1);

    public WarmUpRateLimiterController(double count, int warmUpPeriodSec, int timeOutMs, int coldFactor) {
        super(count, warmUpPeriodSec, coldFactor);
        this.timeoutInMs = timeOutMs;
    }

    @Override
    public boolean canPass(Node node, int acquireCount) {
        return canPass(node, acquireCount, false);
    }

    @Override
    public boolean canPass(Node node, int acquireCount, boolean prioritized) {
		// 获取上个滑动窗口的 QPS
        long previousQps = (long) node.previousPassQps();
		// 生成 Token
        syncToken(previousQps);

        long currentTime = TimeUtil.currentTimeMillis();

        long restToken = storedTokens.get();
        long costTime = 0;
        long expectedTime = 0;
		// 如果令牌桶的 Token 大于警戒线，说明预热还未结束
        if (restToken >= warningToken) {
            long aboveToken = restToken - warningToken;

            // current interval = restToken*slope+1/count
			// 计算 1s 内生成 Token 的数量
            double warmingQps = Math.nextUp(1.0 / (aboveToken * slope + 1.0 / count));
			// 计算相隔两个请求的相隔时长
            costTime = Math.round(1.0 * (acquireCount) / warmingQps * 1000);
        } else {
			// 计算相隔两个请求的相隔时长
            costTime = Math.round(1.0 * (acquireCount) / count * 1000);
        }
		// 期望通过的最小时间
        expectedTime = costTime + latestPassedTime.get();
		// 如果当前时间大于期望时间，说明 QPS 还未超过阈值，直接通过
        if (expectedTime <= currentTime) {
            latestPassedTime.set(currentTime);
            return true;
        } else {
			// 如果当前时间小于期望时间，表示请求过快，需要排队等待指定时间
            long waitTime = costTime + latestPassedTime.get() - currentTime;
			// 如果等待时长超过设置的最大时长，不通过
            if (waitTime > timeoutInMs) {
                return false;
            } else {
                long oldTime = latestPassedTime.addAndGet(costTime);
                try {
                    waitTime = oldTime - TimeUtil.currentTimeMillis();
					// 判断等待时间是否已经超过最大值
                    if (waitTime > timeoutInMs) {
                        latestPassedTime.addAndGet(-costTime);
                        return false;
                    }
					// 占用等待时间成功
                    if (waitTime > 0) {
                        Thread.sleep(waitTime);
                    }
                    return true;
                } catch (InterruptedException e) {
                }
            }
        }
        return false;
    }
}
