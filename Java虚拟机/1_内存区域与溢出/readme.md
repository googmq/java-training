# 虚拟机运行时数据区

![](res/1.jpg)

## 线程相关区

### 程序计数器（Program Counter Register）

当前线程所执行的字节码的行号指示器。在虚拟机概念模型里面，字节码解释器工作时就是通过改变这个计数器的值来选择下一条需要执行的字节码指令。分支、循环、跳转、异常处理、线程恢复等基础功能都需要依赖程序计数器。

每个线程都有自己的程序计数器，各个现成的程序计数器互相独立，互不影响。

如果线程正在执行的是一个Java方法，程序计数器记录的是正在执行的虚拟机字节码指令的地址；如果正在执行的是Native方法，程序计数器的值微孔（Undefined）。

程序计数器是唯一一个在Java虚拟机规范中没有规定任何OutOfMemoryError情况的区域。

### 虚拟机栈（VM Stack）

Java虚拟机栈也是线程私有的，与线程的生命周期相同。

虚拟机栈描述的是Java方法执行的内存模型：每个方法在执行的同时都会创建一个栈帧，用于存储局部变量表、操作数栈、动态链接、方法出入口等信息。每一个方法从调用到执行完成的过程，就对一个栈帧在虚拟机栈中从入栈到出栈的过程。

如果线程请求的栈深度大于虚拟机允许的深度，将抛出StackOverflowError异常；当可以动态扩展的虚拟机栈无法申请到足够内存时，将抛出OutOfMemoryError异常。

### 本地方法栈（Native Method Stack）

本地方法栈也是线程私有的，与线程的生命周期相同。

本地方法栈的作用与虚拟机栈类似，不同的是本地方法栈管理的是虚拟机使用到的native方法。

如果线程请求的栈深度大于虚拟机允许的深度，将抛出StackOverflowError异常；当本地方法栈无法申请到足够内存时，将抛出OutOfMemoryError异常。

## 线程无关区

### 堆（Heap）

Java堆是Java虚拟机所管理的内存中最大的一块，被所有线程共享。此块区域的唯一目的就是存放对象实例，几乎所有对象实例都在这里分配内存。

Java堆是垃圾收集器管理的主要区域，被各种不同的垃圾回收实现方式划分为不同的模型。在常用的分代收集算法中，堆被分为新生代和老年代。

当堆中没有内存完成对象实例的内存分配时，并且堆无法再扩展，将会抛出OutOfMemoryError异常。

### 方法区（Method Area）

与Java堆一样，是各个线程的共享区域，用于存储被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。

对于HotSpot虚拟机，方法区也被称为“永久代”。这仅仅是因为HotSpot虚拟机的实现选择把GC的分代回收机制扩展到了方法区，或者说用永久代来实现方法区。

当方法区无法满足内存分配的需求时，将抛出OutOfMemoryError异常。

### 直接内存

直接内存并不是Java虚拟机运行时数据区的一部分，也不是Java虚拟机规范中定义的内存区域。

在使用NIO的I/O方式或者创建新线程时，会直接分配虚拟机外的操作系统内存。本机直接内存的分配不会受Java堆大小的限制，但是会受到本机总内存大小、操作系统内存可分配上限等的限制。

当虚拟机使用的其他各个区域的内存和使用的直接内存超过物理内存限制，再继续动态拓展某个区域会失败并出现OutOfMemoryError异常。

## 主内存与工作内存

主内存与工作内存的区分和上面的运行时内存划分不同。可以理解主内存就是内存中的堆区域或方法区；工作内存是具体的虚拟机实现为了优化程序执行而使用的寄存器、高速缓存等硬件条件。

主内存是被所有线程共享，而工作线程是每个线程私有的。在多线程的情况下，需要特别注意主内存与工作内存之间的同步。

# 各区域的内存溢出异常

## 虚拟机栈和本地方法栈溢出

只要不断的递归函数，陷入“死递归”的状态，就会产生StackOverFlowError异常。

```java
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
```

## 堆溢出

Java堆用于存储对象实例，只要不断的创建对象，并且保证GC Roots到对象之间有可达路径来避免垃圾回收机制清除这些对象，那么对象占用内存到达最大堆的容量限制后就会产生内存溢出异常。

```java
import java.util.LinkedList;
import java.util.List;

/**
 * VM Args: -Xms20m -Xmx20m -xx:+HeapDumpOnOutOfMemoryError
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
```

## 方法区溢出
创建过多的常量会导致常量池无法再分配内存，从而出现OOM。

```java
import java.util.LinkedList;
import java.util.List;

/**
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
```

## 直接内存溢出

### 创建过多线程

创建一个新线程时，虚拟机会在堆创建一个Thread对象同时创建一个操作系统线程，而这个系统线程的内存用的不是JVMMemory，而是系统中剩下的内存。所以系统中剩下的内存越少，能够创建新内存的数量也越小。

```java
/**
 * @Class: NewThreadOOM
 * @Author: chaos
 * @Date: 2019/4/14 16:53
 * @Version 1.0
 *
 * VM Args: -Xms4096m -Xmx4096m -x:+HeapDumpOnOutOfMemoryError
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
```





