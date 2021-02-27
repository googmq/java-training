import java.util.LinkedList;
import java.util.List;

/**
 * @Class: HeapOOM
 * @Author: chaos
 * @Date: 2019/4/14 16:37
 * @Version 1.0
 *
 * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 */
public class HeapOOM {

    static class OOMObject {

    }

    public static void main(String[] args) {
        List<OOMObject> list = new LinkedList<OOMObject>();
        while(true) {
            list.add(new OOMObject());
        }
    }
}
