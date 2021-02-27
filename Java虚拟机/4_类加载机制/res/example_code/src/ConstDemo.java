/**
 * @Class: ConstDemo
 * @Author: chaos
 * @Date: 2019/5/11 10:16
 * @Version 1.0
 */
class ConstValue {

    static {
        System.out.println("Const value init");
    }

    public static final String HELLO_WORLD = "hello world!";
}
public class ConstDemo {

    public static void main(String[] args) {
        System.out.println(ConstValue.HELLO_WORLD);
    }
}
