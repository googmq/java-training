/**
 * @Class: FinalizeDemo
 * @Author: chaos
 * @Date: 2019/4/21 17:12
 * @Version 1.0
 *
 * VM Args: -XX:+PrintGCDetails -Xloggc:ReferenceGCDemo_gc.log -XX:+PrintGCTimeStamps
 * cmd: java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:FinalizeDemo.log FinalizeDemo
 */
public class FinalizeDemo {

    // use some memory for the Object to see diff in GC log
    private static final int _50MB = 50 * 1024 *1024;
    private byte[] something = new byte[_50MB];

    public static FinalizeDemo SAVE_HOOK = null;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        SAVE_HOOK = this;
    }

    public static void main(String[] args) throws Throwable {
        SAVE_HOOK = new FinalizeDemo();
        // SAVE_HOOK set to null for GC, but finalize() will run making the instance referenced to SAVE_HOOK
        SAVE_HOOK = null;
        System.gc();
        Thread.sleep(500);
        // SAVE_HOOK set to null for GC
        SAVE_HOOK = null;
        // finalize() only run once, this time, the instance is dead
        System.gc();
    }
}
