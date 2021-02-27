/**
 * @Class: NewThreadOOM
 * @Author: chaos
 * @Date: 2019/4/14 16:53
 * @Version 1.0
 *
 * VM Args: -Xms4096m -Xmx4096m
 */
public class NewThreadOOM {

    public void oomByNewThread() {
        while (true) {
            new Thread(() -> {
                while(true) {

                }
            }).start();
        }
    }

    public static void main(String[] args) {
        NewThreadOOM newThreadOOM = new NewThreadOOM();
        newThreadOOM.oomByNewThread();
    }
}
