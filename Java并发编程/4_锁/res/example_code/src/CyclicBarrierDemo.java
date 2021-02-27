import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Class: CyclicBarrierDemo
 * @Author: chaos
 * @Date: 2019/4/16 15:29
 * @Version 1.0
 */
public class CyclicBarrierDemo {

    public void tasks(int nThread) throws InterruptedException {
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(nThread);

        ExecutorService service = Executors.newFixedThreadPool(nThread);

        for (int i = 0; i < nThread; i++) {
            service.submit(() -> {
                try {
                    try {
                        System.out.println("Start run task");
                        System.out.println("Wait for barrier");
                        // 所有的子线程都阻塞，必须等到所有的子线程都到达栅栏以后才能全部放行继续，否则都阻塞
                        cyclicBarrier.await();
                        System.out.println("Finish run task");
                    } finally {
                    }
                } catch (Exception e) {
                }
            });
        }
        service.shutdown();
    }

    public static void main(String[] args) throws Exception {
        CyclicBarrierDemo demo = new CyclicBarrierDemo();
        demo.tasks(10);
    }

}
