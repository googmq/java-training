# JavaBean，SpringBean，和ProxyFactoryBean

## JavaBean

传统javabean主要作为值传递参数， 要求每个属性都有getter和setter方法（有时还会要求提供默认的无参构造函数）。

```java
public class Person {
    private String name;
    private int age;
    
    public Person() {
        super();
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getAge() {
        return this.age;
    }
}
```

## SpringBean

在Spring中SpringBean无处不在，可以作为值传递对象，也可以作为业务处理单元，任何组件都可以时SpringBean。SpringBean不要求每个属性都有getter和setter方法，因为Spring可以使用自己的方法注入属性的值。

所有可以被Spring容器实例化并管理的java类都可以称为SpringBean。

```java
@Service
public class PersionService{
    @Autowired
    private PersonRepos personRepos;
    
    public void savePerson(Person person) {
        this.personRepos.save(person);
    }
}
```

## ProxyFactoryBean

ProxyFactoryBean是SpringBean的一种特例。

当Java对象需要被注册成SpringBean时，在对象实例化后，会根据是否需要进行代理，来决定是否包装实例为ProxyFactoryBean。

1. 当不需要进行代理时，bean factory中直接使用Java对象实例作为bean实例
2. 当需要进行代理时，bean factory中使用被代理增强过的代理对象实例作为bean实例