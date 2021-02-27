# java8新特性

## 1.1新的时间和api

### 1.1.1旧的date类

```java
//先是date类,没啥用处一般不用
Date date = new Date();
int month = date.getMonth();
int day = date.getDay();
System.out.println(date);
System.out.println(month);
System.out.println(day);
```

Wed Feb 27 14:42:17 CST 2019
1
3

这个类我也没怎么用过

### 1.1.2calendar类和SimpleDateFormat

```java
Calendar calendar = Calendar.getInstance();
calendar.setTime(new Date());
int i = calendar.get(Calendar.DAY_OF_WEEK);
System.out.println(i);
```

4

```java
SimpleDateFormat sdf = new SimpleDateFormat();
String format = sdf.format(1000 * 60 * 60L);
System.out.println(format);
```

70-1-1 上午9:00

这中间又涉及到了一些西方的一周以周日开头,以及时区的问题,可以用但是要经常转化

### 1.1.3新的LocalDateTime

```java
LocalDate i = LocalDate.now();
LocalTime j = LocalTime.now();
LocalDateTime now = LocalDateTime.now();
System.out.println(i);
System.out.println(j);
System.out.println(now);
```

2019-02-27
16:25:15.565
2019-02-27T16:25:15.565

这就是不带时区的本地时间,带时区的时间是ZoneDateTime

### 1.1.4格式化和解析

```java
LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("E yyyy-MM-dd HH:mm");
		String format = dateTimeFormatter.format(now);
		LocalDate parse = LocalDate.parse("2019-02-27");
		LocalDateTime parse1 = LocalDateTime.parse("星期三 2019-02-27 17:28", dateTimeFormatter);
		System.out.println(format);
		System.out.println(parse);
		System.out.println(parse1);
```

星期三 2019-02-27 17:31
2019-02-27
2019-02-27T17:28

### 1.1.5开发中的转化

long类型的时间转成LocalDateTime

```java
LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(60L, 0, ZoneOffset.ofHours(8));
System.out.println(localDateTime);
```

1970-01-01T08:01

LocalDateTime转成long类型的时间

```java
long l = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
System.out.println(l);
```

60000



## 1.2lambda表达式

### 1.2.1一些简单的lambda表达式

```java
() -> 5
(x, y) -> x – y
x -> 2 * x
int x, int y) -> x + y
(String s) -> System.out.print(s)
```

```java
List<String> strings=new ArrayList<>();
strings.add("张三");
strings.add("李四");
strings.add("王五");
```

```java
//使用循环的方式打印
for (String player : strings) {
 System.out.print(player + "; ");
}
```

```java
// 使用 lambda 表达式以及函数操作
strings.forEach((player) -> System.out.print(player + "; "));
```

```java
// 在 Java 8 中使用双冒号操作符
strings.forEach(System.out::print);
```

张三; 李四; 王五; 张三; 李四; 王五; 张三李四王五

### 1.2.2利用lambda表达式写内部类

```java
//使用匿名内部类
new Thread(new Runnable() {
 @Override
 public void run() {
  System.out.println("Hello world !");
 }
}).start();

//使用 lambda expression
new Thread(() -> System.out.println("Hello world !")).start();
```

Hello world !
Hello world !

```java
String[] players = {"张三丰","李四","Gakki"};
  List<String> strings = Arrays.asList(players);
// 1.1 使用匿名内部类根据 name 排序 players
  strings.sort(new Comparator<String>() {
   @Override
   public int compare(String s1, String s2) {
    return (s1.compareTo(s2));
   }
  });
  for (String player : strings) {
   System.out.print(player + "; ");
  }

  //采用lambda表达式
  strings.sort((String s1, String s2) -> (s1.compareTo(s2)));
  strings.forEach(System.out::print);
```

Gakki; 张三丰; 李四; Gakki张三丰李四



## 1.3Stream API和lambda对集合进行操作

```java
List<Person> javaProgrammers = new ArrayList<Person>() {
  {
   add(new Person("Elsdon", "Jaycob", "Java programmer", "male", 1000, 20));
   add(new Person("Tamsen", "Brittany", "Java programmer", "female", 2000, 18));
   add(new Person("Floyd", "Donny", "Java programmer", "male", 3000, 36));
   add(new Person("Sindy", "Jonie", "Java programmer", "female", 2560, 40));
   add(new Person("Vere", "Hervey", "Java programmer", "male", 2800, 32));
   add(new Person("Maude", "Jaimie", "Java programmer", "female", 3500, 23));
   add(new Person("Shawn", "Randall", "Java programmer", "male", 2800, 16));
   add(new Person("Jayden", "Corrina", "Java programmer", "female", 1300, 49));
   add(new Person("Palmer", "Dene", "Java programmer", "male", 1800, 12));
   add(new Person("Addison", "Pam", "Java programmer", "female", 2300, 14));
  }
 };

 System.out.println("所有程序员的姓名:");
 javaProgrammers.forEach((p) -> System.out.printf("%s %s; ", p.getFirstName(), p.getLastName()));
 System.out.println();
 System.out.println("下面是月薪超过 $2000 的PHP程序员:");
 javaProgrammers.stream()
  .filter((p) -> (p.getSalary() > 2000))
  .forEach((p) -> System.out.printf("%s %s; ", p.getFirstName(), p.getLastName()));
}
```

所有程序员的姓名:
Elsdon Jaycob; Tamsen Brittany; Floyd Donny; Sindy Jonie; Vere Hervey; Maude Jaimie; Shawn Randall; Jayden Corrina; Palmer Dene; Addison Pam; 
下面是月薪超过 $2000 的PHP程序员:
Floyd Donny; Sindy Jonie; Vere Hervey; Maude Jaimie; Shawn Randall; Addison Pam; 