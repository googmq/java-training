/**
 * @Class: StackSOF
 * @Author: chaos
 * @Date: 2019/4/14 16:37
 * @Version 1.0
 *
 * VM Args: -Xss128k
 */
public class StackSOF {

    private int stackLength = 1;

    public void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) throws Throwable {
        StackSOF sof = new StackSOF();
        try {
            sof.stackLeak();
        } catch (Exception e) {
            System.out.println(sof.stackLength);
            throw e;
        }
    }
}
