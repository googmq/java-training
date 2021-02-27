import java.util.concurrent.locks.ReentrantLock;

/**
 * @Class: ThreadDemo
 * @Author: chaos
 * @Date: 2019/4/27 17:46
 * @Version 1.0
 */
public class ThreadDemo {

    private static final String lockStr1 = "lockStr1";
    private static final String lockStr2 = "lockStr2";
    private static final String lockStr3 = "lockStr3";

    private static final ReentrantLock lock1 = new ReentrantLock();
    private static final ReentrantLock lock2 = new ReentrantLock();

    public void busyThread() {
        new Thread(() -> {
            while (true) {}
        }, "busy_thread").start();
    }

    public void waitThread() {
        new Thread(() -> {
            synchronized (lockStr1) {
                try {
                    lockStr1.wait();
                } catch (Exception e) {}
            }
        }, "wait_thread").start();
    }

    public void deadLockThread () {
        new Thread(() -> {
            synchronized (lockStr2) {
                try {
                    Thread.sleep(500);
                    synchronized (lockStr3) {

                    }
                } catch (Exception e) {}
            }
        }, "dead_lock_thread1").start();

        new Thread(() -> {
            synchronized (lockStr3) {
                try {
                    Thread.sleep(500);
                    synchronized (lockStr2) {

                    }
                } catch (Exception e) {}
            }
        }, "dead_lock_thread2").start();
    }

    public void deadLockThread1 () {
        new Thread(() -> {
            try {
                lock1.lock();
                Thread.sleep(500);
                lock2.lock();
            } catch(Exception e){

            } finally {
                lock2.unlock();
                lock1.unlock();
            }
        }, "dead_lock1_thread1").start();

        new Thread(() -> {
            try {
                lock2.lock();
                Thread.sleep(500);
                lock1.lock();
            } catch(Exception e){

            } finally {
                lock1.unlock();
                lock2.unlock();
            }
        }, "dead_lock1_thread2").start();
    }

    public static void main(String[] args) {
        ThreadDemo demo = new ThreadDemo();
        demo.busyThread();
        demo.waitThread();
        demo.deadLockThread();
        demo.deadLockThread1();
    }

}
