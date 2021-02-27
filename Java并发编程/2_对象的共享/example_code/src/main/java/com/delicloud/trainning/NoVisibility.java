package com.delicloud.trainning;

/**
 * @Package: com.delicloud.trainning
 * @Class: NoVisibility
 * @Author: chaos
 * @Date: 2019/2/2 11:28
 * @Version 1.0
 */
public class NoVisibility {

    private static boolean ready;
    private static int number;
    private static int count;

    private static class ReaderThread extends Thread {
        @Override
        public void run() {
            while (!ready) {
                System.out.println(count++);
                Thread.yield();
            }
            System.out.println(number);
        }
    }

    public static void main(String[] args) {
        new ReaderThread().start();
        // ReaderThread线程可能一直不释放cpu资源，导致一直不退出，不断输出
        // 下面的两个set操作可能出现重排序，导致先设置ready的值，然后释放cpu资源给ReaderThread，输出0然后退出
        number = 100;
        ready = true;
    }
}
