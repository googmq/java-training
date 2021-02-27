# BIO & NIO

## BIO

socket.accept()、socket.read()、socket.write()三个主要函数都是同步阻塞的，当一个连接在处理I/O的时候，系统是阻塞的，如果是单线程的话必然就挂死在那里；但CPU是被释放出来的，开启多线程，就可以让CPU去处理更多的事情。

```java
{
 ExecutorService executor = Excutors.newFixedThreadPollExecutor(100);//线程池

 ServerSocket serverSocket = new ServerSocket();
 serverSocket.bind(8088);
 while(!Thread.currentThread.isInturrupted()){//主线程死循环等待新连接到来
 Socket socket = serverSocket.accept();
 executor.submit(new ConnectIOnHandler(socket));//为新的连接创建新的线程
}

class ConnectIOnHandler extends Thread{
    private Socket socket;
    public ConnectIOnHandler(Socket socket){
       this.socket = socket;
    }
    public void run(){
      while(!Thread.currentThread.isInturrupted()&&!socket.isClosed()){死循环处理读写事件
          String someThing = socket.read()....//读取数据
          if(someThing!=null){
             ......//处理数据
             socket.write()....//写数据
          }

      }
    }
}
```

## NIO

NIO的读写函数可以立刻返回，这就给了我们不开线程利用CPU的最好机会：如果一个连接不能读写（socket.read()返回0或者socket.write()返回0），我们可以把这件事记下来，记录的方式通常是在Selector上注册标记位，然后切换到其它就绪的连接（channel）继续进行读写。 

```java
interface ChannelHandler{
      void channelReadable(Channel channel);
      void channelWritable(Channel channel);
   }
   class Channel{
     Socket socket;
     Event event;//读，写或者连接
   }

   //IO线程主循环:
   class IoThread extends Thread{
   public void run(){
   Channel channel;
   while(channel=Selector.select()){//选择就绪的事件和对应的连接
      if(channel.event==accept){
         registerNewChannelHandler(channel);//如果是新连接，则注册一个新的读写处理器
      }
      if(channel.event==write){
         getChannelHandler(channel).channelWritable(channel);//如果可以写，则执行写事件
      }
      if(channel.event==read){
          getChannelHandler(channel).channelReadable(channel);//如果可以读，则执行读事件
      }
    }
   }
   Map<Channel，ChannelHandler> handlerMap;//所有channel的对应事件处理器
  }
```

## 对比

BIO的读写都是阻塞操作，但是可以保证不管数据有多少，都可以读到想要的大小。NIO的读写非阻塞，但是只能保证读到当前可用的数据，每次读取的数据大小和完整性没有保证。

因此，NIO更适合每次通讯都是小数据量的高并发场景（聊天、游戏），而在单次大数据量的通信场景（发送文件）中需要额外的逻辑来合并数据。

# 基础概念

Java NIO 由以下几个核心部分组成：

## Channel

数据流通的通道，常用的有以下几种：

- FileChannel： 从文件中读写数据 
- DatagramChannel： 能通过UDP读写网络中的数据 
- SocketChannel： 能通过TCP读写网络中的数据 
- ServerSocketChannel：可以监听新进来的TCP连接，对每一个新进来的连接都会创建一个SocketChannel

## Buffer

数据从Channel读出后、写入前的缓冲，数据可以一次大量的读出、写入而不用频繁进行小数据操作。

## Selector

网络通信时的事件分发选择器。允许一个线程同时操作多个Channel。

# Channel

- 既可以从Channel中读取数据，又可以写数据到Channel。但流的读写通常是单向的。
- Channel可以异步地读写。
- Channel中的数据总是要先读到一个Buffer，或者总是要从一个Buffer中写入。

```java
RandomAccessFile aFile = new RandomAccessFile("data/nio-data.txt", "rw");
FileChannel inChannel = aFile.getChannel(); 

ByteBuffer buf = ByteBuffer.allocate(48);

int bytesRead = inChannel.read(buf);
while (bytesRead != -1) { 
	System.out.println("Read " + bytesRead);
	buf.flip(); 
	while(buf.hasRemaining()){
		System.out.print((char) buf.get());
	} 
	buf.clear();
	bytesRead = inChannel.read(buf);
}
aFile.close();

```

使用Channel一定要配合Buffer使用，需要非常消息的操作Buffer中被操作数据的定位。

# Buffer

NIO使用Buffer和Channel进行数据交互，数据从Channel读到Buffer，从Buffer写到Channel。

## capacity position & limit

capacity、position和limit是Buffer最重要的三个属性。

- capacity：Buffer的最大容量
- position：当前可操作（读、写）的Buffer位置索引值
- limit：最大可操作（读、写）的Buffer位置索引值

![](res/1.png)

使用Buffer读写数据一般遵循以下四个步骤：

1. 写入数据到Buffer
2. 调用`flip()`方法
3. 从Buffer中读取数据
4. 调用`clear()`方法或者`compact()`方法

当向buffer写入数据时，buffer会记录下写了多少数据。一旦要读取数据，需要通过flip()方法将Buffer从写模式切换到读模式。在读模式下，可以读取之前写入到buffer的所有数据。

一旦读完了所有的数据，就需要清空缓冲区，让它可以再次被写入。有两种方式能清空缓冲区：调用clear()或compact()方法。clear()方法会清空整个缓冲区。compact()方法只会清除已经读过的数据。任何未读的数据都被移到缓冲区的起始处，新写入的数据将放到缓冲区未读数据的后面。

***Buffer的使用主要就是position和limit属性来确定可操作范围，然后在读写过程中不断移动这两个属性的过程。***

## Buffer的类型

Java NIO 有以下Buffer类型

- ByteBuffer
- MappedByteBuffer
- CharBuffer
- DoubleBuffer
- FloatBuffer
- IntBuffer
- LongBuffer
- ShortBuffer

 这些Buffer类型代表了不同的数据类型的Buffer，即可以直接使用以上类型来直接操作Buffer中的字节。***但是我建议还是使用ByteBuffer，自己来控制Byte到其他数据类型的转换。***

## Buffer的操作

### 分配空间

要想获得一个Buffer对象首先要进行分配。 每一个Buffer类都有一个allocate方法，可以直接分配所需大小的Buffer。

 ```java
ByteBuffer buf = ByteBuffer.allocate(48);
 ```

### 向Buffer写数据

#### 从Channel写到Buffer

 ```java
int bytesRead = inChannel.read(buf); //read into buffer.
 ```

#### 通过put方法写Buffer

 ```java
buf.put(127);
 ```

put方法有很多版本，允许你以不同的方式把数据写入到Buffer中。

#### flip()方法

flip方法将Buffer从写模式切换到读模式。调用flip()方法会将position设回0，并将limit设置成之前position的值。 

### 从Buffer读数据

####  从Buffer读取数据到Channel 

```java
int bytesWritten = inChannel.write(buf); //read from buffer into channel.
```

####  使用get()方法从Buffer中读取数据 

```java
byte aByte = buf.get();
```

get方法有很多版本，允许你以不同的方式从Buffer中读取数据。 

### 重置Buffer

#### rewind()方法

Buffer.rewind()将position设回0，所以你可以重读Buffer中的所有数据。limit保持不变，仍然表示能从Buffer中读取多少个元素（byte、char等）。

#### clear()与compact()方法

一旦读完Buffer中的数据，需要让Buffer准备好再次被写入。可以通过clear()或compact()方法来完成。

如果调用的是clear()方法，position将被设回0，limit被设置成 capacity的值。换句话说，Buffer 被清空了。Buffer中的数据并未清除，只是这些标记告诉我们可以从哪里开始往Buffer里写数据。

如果Buffer中有一些未读的数据，调用clear()方法，数据将“被遗忘”，意味着不再有任何标记会告诉你哪些数据被读过，哪些还没有。

如果Buffer中仍有未读的数据，且后续还需要这些数据，但是此时想要先先写些数据，那么使用compact()方法。

compact()方法将所有未读的数据拷贝到Buffer起始处。然后将position设到最后一个未读元素正后面。limit属性依然像clear()方法一样，设置成capacity。现在Buffer准备好写数据了，但是不会覆盖未读的数据。

### 其他操作

#### mark()与reset()

通过调用Buffer.mark()方法，可以标记Buffer中的一个特定position。之后可以通过调用Buffer.reset()方法恢复到这个position。 

```java
buffer.mark(); //call buffer.get() a couple of times, e.g. during parsing.
buffer.reset();  //set position back to mark.
```

#### equals()与compareTo()

***equals()***

当满足下列条件时，表示两个Buffer相等：

1. 有相同的类型（byte、char、int等）。
2. Buffer中剩余的byte、char等的个数相等。
3. Buffer中所有剩余的byte、char等都相同。

如你所见，equals只是比较Buffer的一部分，不是每一个在它里面的元素都比较。实际上，它只比较Buffer中的剩余元素。

***compareTo()方法***

compareTo()方法比较两个Buffer的剩余元素(byte、char等)， 如果满足下列条件，则认为一个Buffer“小于”另一个Buffer：

1. 第一个不相等的元素小于另一个Buffer中对应的元素 。
2. 所有元素都相等，但第一个Buffer比另一个先耗尽(第一个Buffer的元素个数比另一个少)。

***剩余元素是从 position到limit之间的元素）***

# Selector

Selector（选择器）是Java NIO中能够检测一到多个NIO通道，并能够知晓通道是否为诸如读写事件做好准备的组件。这样，一个单独的线程可以管理多个channel，从而管理多个网络连接。 

每个网络连接都使用一个线程的时候，在进行网络通信读写时，线程会阻塞。使用Selector管理多个网络连接时，只有网络连接可读写时，才会有工作线程执行工作，其他时候只有Selector线程一个线程在阻塞。

## 创建Selector

通过调用Selector.open()方法创建一个Selector 。

```java
Selector selector = Selector.open();
```

## 向Selector注册通道

为了将Channel和Selector配合使用，必须将channel注册到selector上。通过SelectableChannel.register()方法来实现。

```java
channel.configureBlocking(false);
SelectionKey key = channel.register(selector, Selectionkey.OP_READ | SelectionKey.OP_WRITE);
```

与Selector一起使用时，Channel必须处于非阻塞模式下。这意味着不能将FileChannel与Selector一起使用，因为FileChannel不能切换到非阻塞模式。而Socket通道都可以。 

Selector一共可以监听一下四种类型的事件：

-  SelectionKey.OP_CONNECT：客户端Channel成功连接到服务器
-  SelectionKey.OP_ACCEPT：服务器准备好接受新的客户端Channel连接
-  SelectionKey.OP_READ：服务器Channel中有数据可读
-  SelectionKey.OP_WRITE：服务器Channel可以写数据

## SelectionKey

当向Selector注册Channel时，register()方法会返回一个SelectionKey对象。这个对象包含了一些你感兴趣的属性：

- interest集合：注册到Selector的事件的集合

  ```java
  int interestSet = selectionKey.interestOps();
  boolean isInterestedInAccept  = (interestSet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT；
  boolean isInterestedInConnect = interestSet & SelectionKey.OP_CONNECT;
  boolean isInterestedInRead    = interestSet & SelectionKey.OP_READ;
  boolean isInterestedInWrite   = interestSet & SelectionKey.OP_WRITE;
  ```

- ready集合：已经可以就绪的操作的集合

  ```java
  int readySet = selectionKey.readyOps();
  boolean isAcceptable  = (readySet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT；
  boolean isConnectable = readySet & SelectionKey.OP_CONNECT;
  boolean isReadable    = readySet & SelectionKey.OP_READ;
  boolean isWritable   = readySet & SelectionKey.OP_WRITE;
  
  selectionKey.isAcceptable();
  selectionKey.isConnectable();
  selectionKey.isReadable();
  selectionKey.isWritable();
  ```

- Channel：注册的Channel

  ```java
  Channel  channel  = selectionKey.channel();
  ```

- Selector：注册的Seletor

  ```java
  Selector selector = selectionKey.selector();
  ```

- 附加的对象（可选）:将一个对象或者更多信息附着到SelectionKey上，供后续使用

  ```java
  selectionKey.attach(theObject);
  Object attachedObj = selectionKey.attachment();
  
  SelectionKey key = channel.register(selector, SelectionKey.OP_READ, theObject);
  ```

## 通过Selector选择通道

一旦向Selector注册了一或多个通道，就可以调用几个重载的select()方法，返回事件就绪的通道的数量。 

- int select()： 阻塞到至少有一个通道在你注册的事件上就绪了 
- int select(long timeout)： 和select()一样，除了最长会阻塞timeout毫秒
- int selectNow()： 不会阻塞，不管什么通道就绪都立刻返回 （没有就绪的返回0）

select()方法返回的int值表示有多少通道已经就绪，即自上次调用select()方法后有多少通道变成就绪状态。 

## selectedKeys()

一旦调用了select()方法，并且返回值表明有一个或更多个通道就绪了，然后可以通过调用selector的selectedKeys()方法，访问“已选择键集（selected key set）”中的就绪通道。 

```java
Set selectedKeys = selector.selectedKeys();
```

可以遍历这个已选择的键集合来访问就绪的通道。 

```java
Set selectedKeys = selector.selectedKeys();
Iterator keyIterator = selectedKeys.iterator();
while(keyIterator.hasNext()) {
    SelectionKey key = keyIterator.next();
    if(key.isAcceptable()) {
        // a connection was accepted by a ServerSocketChannel.
    } else if (key.isConnectable()) {
        // a connection was established with a remote server.
    } else if (key.isReadable()) {
        // a channel is ready for reading
    } else if (key.isWritable()) {
        // a channel is ready for writing
    }
    keyIterator.remove();
}
```

Selector不会自己从已选择键集中移除SelectionKey实例。必须在处理完通道时自己移除。下次该通道变成就绪时，Selector会再次将其放入已选择键集中。

SelectionKey.channel()方法返回的通道需要转型成你要处理的类型，如ServerSocketChannel或SocketChannel等。

## wakeUp()

某个线程调用select()方法后阻塞了，即使没有通道已经就绪，也有办法让其从select()方法返回。只要让其它线程在同一个Selector实例上调用wakeup()方法即可。阻塞在select()方法上的线程会立马返回。

如果有其它线程调用了wakeup()方法，但当前没有线程阻塞在select()方法上，下个调用select()方法的线程会立即“醒来（wake up）”。

## close()

用完Selector后调用其close()方法会关闭该Selector，且使注册到该Selector上的所有SelectionKey实例无效。通道本身并不会关闭。