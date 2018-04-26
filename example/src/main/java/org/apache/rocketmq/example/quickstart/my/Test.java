package org.apache.rocketmq.example.quickstart.my;

public class Test {
    public static void main(String[] args) {

        try {
            test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test() {
        System.out.println("aaa");
        assert 1 == 2;
        System.out.println("bbb");

    }
}
