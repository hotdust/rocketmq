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
package org.apache.rocketmq.example.quickstart.my.tracing;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * This class demonstrates how to send messages to brokers using provided {@link DefaultMQProducer}.
 */
public class Producer {
    public static void main(String[] args) throws MQClientException, InterruptedException, IOException, NoSuchFieldException, IllegalAccessException {

        /*
         * Instantiate with a producer group name.
         */
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");


        /*
         * Launch the instance.
         */
        // add 设置 name server
        producer.setNamesrvAddr("localhost:9876");
        producer.setSendLatencyFaultEnable(true);
        producer.start();

        Message msg = new Message("TopicTest" /* Topic */,
                "normal" /* Tag */,
                ("Hello RocketMQ Tag:normal").getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
        );

        msg.putUserProperty("tracing", "tracing success");
//        Field propsField = msg.getClass().getDeclaredField("properties");
//        propsField.setAccessible(true);
//        System.out.println("propsField.getName(): " + propsField.getName());
//        Object propsObj = propsField.get(msg);
//        if( propsObj instanceof Map ) {
//            Map<String, String> propsMap = (Map<String, String>) propsObj;
//            propsMap.put("tracing", "tracing success");
//        }

        try {
            producer.send(msg);
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        }

        producer.shutdown();
    }

}


