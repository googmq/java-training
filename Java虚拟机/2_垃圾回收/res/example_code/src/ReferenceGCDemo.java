/**
 * @Class: ReferenceGCDemo
 * @Author: chaos
 * @Date: 2019/4/21 16:06
 * @Version 1.0
 *
 * VM Args: -XX:+PrintGCDetails -Xloggc:ReferenceGCDemo_gc.log
 * cmd: java -XX:+PrintGCDetails -Xloggc:ReferenceGCDemo_gc.log ReferenceGCDemo
 */
public class ReferenceGCDemo {

    public Object instance = null;

    // use some memory for the Object to see diff in GC log
    private static final int _1MB = 1024 *1024;
    private byte[] something = new byte[_1MB];

    public static void main(String[] args) {
        ReferenceGCDemo demo1 = new ReferenceGCDemo();
        ReferenceGCDemo demo2 = new ReferenceGCDemo();

        demo1.instance = demo2;
        demo2.instance = demo1;

        demo1 = null;
        // as one Object is not set to null, the both will not be recycled
        // demo2 = null;

        System.gc();
    }
}
