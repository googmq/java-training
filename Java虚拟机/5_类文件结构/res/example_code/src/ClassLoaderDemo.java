import java.io.InputStream;

/**
 * @Class: ClassLoaderDemo
 * @Author: chaos
 * @Date: 2019/5/17 15:52
 * @Version 1.0
 */
public class ClassLoaderDemo {

    public static void main(String[] args) throws Exception {
        ClassLoader myClassLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                // using parent class loader
                return super.loadClass(name);

                // using defined class loader
//                try {
//                    String fileName = name + ".class";
//                    InputStream is = getClass().getResourceAsStream(fileName);
//                    if (null == is) {
//                        return super.loadClass(name);
//                    }
//                    byte[] bytes = new byte[is.available()];
//                    is.read(bytes);
//                    return defineClass(name, bytes, 0, bytes.length);
//                } catch (Exception e) {
//                    throw new ClassNotFoundException(name);
//                }
            }
        };

        // Load a class obj with my class loader
        Object obj = myClassLoader.loadClass("ClassLoaderDemo").newInstance();
        // show class info
        System.out.println(obj.getClass());
        // compare obj with the origin class
        System.out.println(obj instanceof ClassLoaderDemo);
    }
}
