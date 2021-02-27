import java.util.LinkedList;
import java.util.List;

/**
 * @Class: MatDemo
 * @Author: chaos
 * @Date: 2019/4/30 17:43
 * @Version 1.0
 */
public class MatDemo {
    private byte[] bytes = new byte[1024 * 1024];

    public static void main(String[] args) {
        List<MatDemo> list = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new MatDemo());
            MatDemo demo = new MatDemo();
        }
        try {
            System.in.read();
        } catch (Exception e) {}
    }
}
