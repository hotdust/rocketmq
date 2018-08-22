package org.apache.rocketmq.example.quickstart.my;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws Exception {
//        int DATA_SIZE = 10;
//        ByteBuffer data = ByteBuffer.allocate(DATA_SIZE);
//        for (int i = 0; i < DATA_SIZE; i++) {
//            data.put((byte) 1);
//        }
//
//        byte[] buffer = new byte[6];
//        data.flip();
//        data.get(buffer);
//        buffer = new byte[6];
//        data.get(buffer);
//        System.out.println("complete");


        ByteBuffer buffer = ByteBuffer.allocate(8);
        long data = 128;
        buffer.putLong(data);

        System.out.println("position:" + buffer.position() + ", limit:" + buffer.limit());

    }
}
