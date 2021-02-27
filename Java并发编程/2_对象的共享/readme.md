# 原子性与可见性

## 可见性

- 线程中访问到的实例状态都是有效的，不会出现随机错乱的数据。

- 实例状态可能不是在当前线程中“有效”，而是在并发的另外一个线程中“有效”。

## 原子性

- 实例状态在当前线程中“有效”。

# 状态的可见性 

类实例状态的可见性非常复杂，往往会违背我们的直觉。

##  错误的状态

### 失效数据

- 非原子操作在执行到半途时释放了cpu资源
- 代码中出现了“重排序”现象（编译器、cpu和运行环境都可能进行“重排序”优化）

~~~
package com.delicloud.trainning;

/**
 * @Package: com.delicloud.trainning
 * @Class: NoVisibility
 * @Author: chaos
 * @Date: 2019/2/2 11:28
 * @Version 1.0
 */
public class NoVisibility {

    private static boolean ready;
    private static int number;
    private static int count;

    private static class ReaderThread extends Thread {
        @Override
        public void run() {
            while (!ready) {
                System.out.println(count++);
                Thread.yield();
            }
            System.out.println(number);
        }
    }

    public static void main(String[] args) {
        new ReaderThread().start();
        // ReaderThread线程可能一直不释放cpu资源，导致一直不退出，不断输出
        // 下面的两个set操作可能出现重排序，导致先设置ready的值，然后释放cpu资源给ReaderThread，输出0然后退出
        number = 100;
        ready = true;
    }
}
~~~

### 非原子的64位操作

JVM允许将非volatile类型的64位数值变量（double和long）的读写操作分解为两个32位操作。如果同时出现读写线程并出现竞态条件，可能导致读线程读到被写线程修改了一半的数据。此时数据的值既不属于读线程，也不属于写线程，是一个随机值。

## 保证状态的正确

### 加锁

通过对可能产生竞态条件的状态加锁，保证写线程和读线程不会并发执行，可以避免竞态条件的发生。

### volatile变量

volatile变量是一种弱同步机制，确保将变量的更新同步到其他线程。volitale变量的操作不会与其他操作产生“重排序”现象，也不会缓存到寄存器或其他硬件体系中，可以保证对volitale变量的读取永远都是最新值。

volatile变量解决不了读写线程的时序随机性导致的“非原子性”问题，但是可以避免读到失效数据或者随机的64位数据。

### 两种方式的比较

- 加锁可以保证原子性，也保证可见性
- volatile变量只能保证可见性，不能保证原子性

# 不共享实例状态

## 线程封闭

### 栈封闭

只能通过局部变量访问对象，对象的应用被封闭在当前方法的栈帧中。当代码从方法中返回时，对象无法被访问到。

不同的线程访问同一个方法，都有各自的方法栈帧，互相不会影响。

~~~java
public int loadStrs(String[] args) {
    Set<String> strs;
    int count = 0;
    
    // strs这个引用是方法的局部变量，从方法返回后无法访问到HashSet的实例
    strs = new HashSet();
    for(String str : strs) {
        strs.add(str);
    }
    count = strs.size();
    return count;
}
~~~



### ThreadLocal类

ThreadLocal是一种更规范的维持线程封闭的方法。ThreadLocal提供了get和set方法，为每个使用该变量的线程都存有一份独立的副本。

可以将ThreadLocal视为一个Map<Thread, T>对象，但是实际的实现并非如此，特定与线程的值都保存在Thread对象中，而不是一个Map中。

~~~java
private static ThreadLocal<Connection> connectionHolder
	= new ThreadLocal<Connection>() {
	......
};

public static Connection getConnection() {
    return connectionHolder.get();
}
~~~



## 不变对象

不可变对象一定是线程安全的。

当满足以下条件时，对象才是不可变的：

- 对象实例创建以后其的状态就不能修改
- 对象实例的所有域都是final类型
- 对象实例是正确创建的（实例化时没有this引用逸出）

### final域

使用final修饰变量就限制了变量，值无法被改变。

### volatile变量

volatile变量可以被改变，虽然没有保证不变性与原子性，但是改变的结果会同步到所有引用线程。

# 共享实例状态

## 常用的安全发布模式

要安全的发布一个对象实例，对此实例的引用和对象实例的状态都必须同时对其他线程可见。不允许如下情况：

~~~java
public class A {
    private B b;
    
    public A(B b) {
        super();
        this.b = b;
    }
    
    public B getB() {
        return this.b;
    }
}

public class B {
    private int i;
    
    public int getI() {
        return this.i;
    } 
}

public static void main(String[] args) {
    B b = new B();
    A a = new A(b);
    // 这个地方绕过a和b拿到了i的引用，可以不通知a和b直接操作i
    int i = a.getB().getI();
}
~~~

- 在静态初始化函数中初始化一个对象的引用：饿汉式初始化，避免多线程初始化竞争
- 将对象的引用保持到volatile类型的域或者AtomReferance对象中：保证原子性
- 将对象的应用保持到某个正确构造对象的final类型域中：不变状态
- 将对象的引用保存到一个被锁保护的域中：保证可见性

## 事实不可变对象实例

如果对象从技术上是不可变的，称为”不可变对象“（Immutable Object），典型的例子就是String。

如果对象实例从技术上来看是可变的，但是其状态在发布后不会再改变，那么把这种对象实例称为”事实不可变对象“（Effectively Immutable Object）。

即使没有额外的同步机制，任何线程都可以安全的使用被安全发布的事实不可变对象实例。

## 对象实例发布原则

可变对象实例的发布取决与它的可变性：

- 不可变对象可以通过任意机制发布
- 事实不可变对象必须通过安全方式来发布
- 可变对象必须通过安全方式来发布，并且必须是线程安全的或是有锁保护起来

## 安全地共享对象实例

在并发程序中使用和共享对象实例时，可以尝试以下的策略：

- 线程封闭：线程封闭的对象实例只被一个线程拥有，不被其他线程共享。
- 只读共享：在没有额外同步机制的前提下，对象实例被多个线程共享读操作。因为对象实例不会被修改，所以不会出现竞态条件（racing condition）。只读共享只能使用不可变对象和事实不可变对象。
- 线程安全共享：对象实例在内部方法中实现同步，多个线程可以通过对象实例的公共接口来操作，由对象实例内部的同步机制保证可见性。
- 保护对象：被保护的对象必须通过持有特定的锁来访问，通过锁机制显式的保证同步。