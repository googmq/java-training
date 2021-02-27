import java.util.LinkedList;
import java.util.List;

/**
 * @Class: MemoryDemo
 * @Author: chaos
 * @Date: 2019/4/27 14:20
 * @Version 1.0
 */
public class MemoryDemo {

    private byte[] bytes = new byte[64 * 1024];

    public static void main(String[] args) throws Exception {
        Thread.sleep(30 * 1000);
        List<MemoryDemo> list = new LinkedList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(new MemoryDemo());
            Thread.sleep(50);
        }
        System.gc();
    }
}
