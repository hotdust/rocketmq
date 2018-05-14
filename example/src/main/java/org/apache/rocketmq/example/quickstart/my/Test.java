package org.apache.rocketmq.example.quickstart.my;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    TimeUnit.SECONDS.sleep(2);
                    latch.countDown();
                    TimeUnit.SECONDS.sleep(2);
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        latch.await();
        System.out.println("complete");

    }
}
