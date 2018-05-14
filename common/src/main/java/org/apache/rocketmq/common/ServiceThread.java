/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.common;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.rocketmq.common.constant.LoggerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceThread implements Runnable {
    private static final Logger STLOG = LoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);
    private static final long JOIN_TIME = 90 * 1000;

    protected final Thread thread;
    protected final CountDownLatch2 waitPoint = new CountDownLatch2(1);
    // 这个状态主要是被 GroupCommitService 使用，是用来表示是否还需要进行等待一段时间。
    // 如果为 true，说明是有消息进来了，就不需要等待了，应该进行刷盘操作。
    // 如果为 false，则说明没有消息进行，等待一段时间后，再进行刷盘操作。
    protected volatile AtomicBoolean hasNotified = new AtomicBoolean(false);
    protected volatile boolean stopped = false;

    public ServiceThread() {
        this.thread = new Thread(this, this.getServiceName());
    }

    public abstract String getServiceName();

    public void start() {
        this.thread.start();
    }

    public void shutdown() {
        this.shutdown(false);
    }

    public void shutdown(final boolean interrupt) {
        this.stopped = true;
        STLOG.info("shutdown thread " + this.getServiceName() + " interrupt " + interrupt);

        if (hasNotified.compareAndSet(false, true)) {
            waitPoint.countDown(); // notify
        }

        try {
            if (interrupt) {
                this.thread.interrupt();
            }

            long beginTime = System.currentTimeMillis();
            if (!this.thread.isDaemon()) {
                this.thread.join(this.getJointime());
            }
            long eclipseTime = System.currentTimeMillis() - beginTime;
            STLOG.info("join thread " + this.getServiceName() + " eclipse time(ms) " + eclipseTime + " "
                + this.getJointime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public long getJointime() {
        return JOIN_TIME;
    }

    public void stop() {
        this.stop(false);
    }

    public void stop(final boolean interrupt) {
        this.stopped = true;
        STLOG.info("stop thread " + this.getServiceName() + " interrupt " + interrupt);

        if (hasNotified.compareAndSet(false, true)) {
            waitPoint.countDown(); // notify
        }

        if (interrupt) {
            this.thread.interrupt();
        }
    }

    public void makeStop() {
        this.stopped = true;
        STLOG.info("makestop thread " + this.getServiceName());
    }

    public void wakeup() {
        if (hasNotified.compareAndSet(false, true)) {
            waitPoint.countDown(); // notify
        }
    }

    protected void waitForRunning(long interval) {
        // 如果有消息进来
        if (hasNotified.compareAndSet(true, false)) {
            // 交换处理队列
            this.onWaitEnd();
            // 直接返回，进行后续操作，就不进行等待一段时间了。
            return;
        }

        // reset方法是为了恢复初始化状态。
        // 因为原始的 CountDownLatch 在使用一次后就不能使用了，为了能重复使用，新加了一个 reset 方法。
        // 把状态设置成初始状态，这样 await 又可以使用了。
        //entry to wait
        waitPoint.reset();

        try {
            waitPoint.await(interval, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 超过等待时间后，恢复 hasNotified 状态为没有消息进入状态。
            hasNotified.set(false);
            // 交换处理队列
            this.onWaitEnd();
        }
    }

    protected void onWaitEnd() {
    }

    public boolean isStopped() {
        return stopped;
    }
}
