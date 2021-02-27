/**
 * @Class: SubClassDemo
 * @Author: chaos
 * @Date: 2019/5/11 9:51
 * @Version 1.0
 */

class SuperClass {

    public static int value = 1;

    static {
        System.out.println("Super class init");
    }

}

class SubClass extends SuperClass{

    static {
        System.out.println("Sub class init");
    }
}

public class SubClassDemo extends SuperClass{

    public static void main(String[] args) {
        System.out.println(SubClass.value);
    }
}
