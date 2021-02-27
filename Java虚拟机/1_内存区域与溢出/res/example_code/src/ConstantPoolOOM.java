import java.util.LinkedList;
import java.util.List;

/**
 * @Package: PACKAGE_NAME
 * @Class: ConstantPoolOOM
 * @Author: chaos
 * @Date: 2019/4/14 17:19
 * @Version 1.0
 *
 * VM Args: -XX:MetaspaceSize=10M -XX:MaxMetaspaceSize=10M
 */
public class ConstantPoolOOM {
    public static void main(String[] args) {
        List<String> list = new LinkedList<String>();
        int i = 0;
        while (true) {
            list.add(String.valueOf(i++).intern());
        }
    }
}
