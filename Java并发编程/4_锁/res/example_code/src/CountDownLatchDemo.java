import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Class: CountDownLatchDemo
 * @Author: chaos
 * @Date: 2019/4/16 11:51
 * @Version 1.0
 */
public class CountDownLatchDemo {

    public void tasks(int nThread) throws InterruptedException {
        // 主线程使用的Latch，需要开门一次
        final CountDownLatch startGate = new CountDownLatch(1);
        // 子线程使用的Lathc，需要开门nThread次
        final CountDownLatch endGate = new CountDownLatch(nThread);

        ExecutorService service = Executors.newFixedThreadPool(nThread);

        for (int i = 0; i < nThread; i++) {
            service.submit(() -> {
                try {
                    try {
                        System.out.println("Start run task");
                        // 子线程等待主线程开门才能继续往下执行，否则阻塞
                        startGate.await();
                        System.out.println("Finish run task");
                    } finally {
                        // 子线程开门，必须所有子线程都countDown以后，门才会打开，主线程继续执行
                        endGate.countDown();
                        System.out.println("Open End latch");
                    }
                } catch (Exception e) {
                }
            });
        }

        Thread.sleep(1000);

        long start = System.nanoTime();
        System.out.println("Start tasks: " + start);
        System.out.println("Open start latch: " + start);
        // 主线程开门，子线程继续执行
        startGate.countDown();
        // 主线程等待子线程开门才能往下继续，否则阻塞
        endGate.await();
        long end = System.nanoTime();
        System.out.println("End tasks: " + end);
        service.shutdown();
    }

    public static void main(String[] args) throws Exception {
        CountDownLatchDemo demo = new CountDownLatchDemo();
        demo.tasks(5);
    }
}
