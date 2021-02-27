import java.util.concurrent.*;

/**
 * @Class: SemaphoreDemo
 * @Author: chaos
 * @Date: 2019/4/16 15:06
 * @Version 1.0
 */
public class SemaphoreDemo {

    public void task(int nThread) {
        ExecutorService service = Executors.newFixedThreadPool(nThread);
        Semaphore semaphore = new Semaphore(nThread / 2);

        for (int i = 0; i < nThread; i++) {
            service.submit(() -> {
                try {
                    // 子线程尝试获取信号量，没有获取信号量的线程阻塞，等待持有信号量的其他线程释放信号量
                    semaphore.acquire();
                    System.out.println("Start run task");
                    Thread.sleep(1000);
                    System.out.println("Finish run task");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 持有信号量的线程释放信号量，让其他线程可以获取
                    semaphore.release();
                }
            });
        }
        service.shutdown();
    }

    public static void main(String[] args) throws Exception {
        SemaphoreDemo demo = new SemaphoreDemo();
        demo.task(10);
    }
}
