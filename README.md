# Spring Boot 集成 Dubbo

TODO：

- [x] 自动装配
- [x] 外部化配置
- [ ] Zookeeper 注册中心
- [ ] 健康检查
- [ ] 服务监控



## Maven 依赖

```xml
<dependencyManagement>
  <dependencies>
    <!-- Spring Boot -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>${spring-boot.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>

    <!-- Apache Dubbo  -->
    <dependency>
      <groupId>org.apache.dubbo</groupId>
      <artifactId>dubbo-dependencies-bom</artifactId>
      <version>${dubbo.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>

    <dependency>
      <groupId>org.apache.dubbo</groupId>
      <artifactId>dubbo</artifactId>
      <version>${dubbo.version}</version>
      <exclusions>
        <exclusion>
          <groupId>org.springframework</groupId>
          <artifactId>spring</artifactId>
        </exclusion>
        <exclusion>
          <groupId>javax.servlet</groupId>
          <artifactId>servlet-api</artifactId>
        </exclusion>
        <exclusion>
          <groupId>log4j</groupId>
          <artifactId>log4j</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <!-- Spring Boot -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
  </dependency>

  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  
  <!-- Dubbo Spring Boot Starter -->
  <dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>2.7.5</version>
  </dependency>
</dependencies>

<build>
  <plugins>
    <!-- spring Boot在编译的时候，是有默认JDK版本的，如果期望使用我们要的JDK版本的话，那么要配置 -->
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <configuration>
        <source>1.8</source>
        <target>1.8</target>
      </configuration>
    </plugin>
  </plugins>
</build>
```



## Getting Started

首先最好提供公共RPC API，以便服务提供方和消费方有相同接口标准，eg: [DemoService](https://github.com/gaotingwang/springboot-dubbo-demo/blob/master/rpc-api/src/main/java/com/gtw/dubbo/rpc/api/DemoService.java)。

### [服务提供方](https://github.com/gaotingwang/springboot-dubbo-demo/tree/master/auto-configure-sample/auto-configure-provider)

1. 实现 RPC 接口：

   ```java
   import com.gtw.dubbo.rpc.api.DemoService;
   import org.apache.dubbo.config.annotation.Service;
   import org.springframework.beans.factory.annotation.Value;
   
   @Service(version = "1.0.0")
   public class DefaultDemoService implements DemoService {
   
       public String sayHello(String name) {
           return String.format("Hello, %s", name);
       }
   }
   ```

   

2. 提供方配置 `application.properties` ：

   ```properties
   # Spring boot application
   spring.application.name=auto-configuration-provider
   server.port=8080
   
   # Dubbo Application
   ## The default value of dubbo.application.name is ${spring.application.name}
   ## dubbo.application.name=${spring.application.name}
   
   # Base packages to scan Dubbo Component: @org.apache.dubbo.config.annotation.Service
   dubbo.scan.base-packages=com.gtw.dubbo.demo.service
   
   # Dubbo Protocol
   dubbo.protocol.name=dubbo
   dubbo.protocol.port=20880
   
   ## Dubbo Registry
   dubbo.registry.address=N/A
   ```

   

### [服务消费方](https://github.com/gaotingwang/springboot-dubbo-demo/tree/master/auto-configure-sample/auto-configure-consumer)

1. 通过 `@Reference` 注入 RPC 接口 :

   ```java
   @SpringBootApplication
   @Slf4j
   public class ConsumerApplication {
   
       @Reference(version = "1.0.0", url = "dubbo://127.0.0.1:20880")
       private DemoService demoService;
   
       public static void main(String[] args) {
           SpringApplication.run(ConsumerApplication.class, args);
       }
   
       @Bean
       public ApplicationRunner runner() {
           return args -> log.info(demoService.sayHello("gtw"));
       }
   }
   ```

   

## 外部化配置

外部化配置即通过 `application.properties` 或者 `bootstrap.properties` 装配配置 Bean。将注册中心地址、元数据中心地址等配置集中管理，可以做到统一环境、减少开发侧感知。

外部化配置和其他本地配置在内容和格式上并无区别，可以简单理解为`dubbo.properties`的外部化存储，配置中心更适合将一些公共配置如注册中心、元数据中心配置等抽取以便做集中管理。

目前Dubbo支持的所有配置都是`.properties`格式的，包括`-D`、`Externalized Configuration`等，`.properties`中的所有配置项遵循一种`path-based`的配置格式：

```properties
# 应用级别
dubbo.{config-type}[.{config-id}].{config-item}={config-item-value}

dubbo.application.name=demo-provider
dubbo.registry.address=zookeeper://127.0.0.1:2181
dubbo.protocol.port=-1

# 服务级别
dubbo.service.{interface-name}[.{method-name}].{config-item}={config-item-value}
dubbo.reference.{interface-name}[.{method-name}].{config-item}={config-item-value}

dubbo.service.org.apache.dubbo.samples.api.DemoService.timeout=5000
dubbo.reference.org.apache.dubbo.samples.api.DemoService.timeout=6000

# 多配置项
dubbo.{config-type}s.{config-id}.{config-item}={config-item-value}

# 相当于 <dubbo:registry id="unit1" address="zookeeper://127.0.0.1:2181" />
dubbo.registries.unit1.address=zookeeper://127.0.0.1:2181
dubbo.registries.unit2.address=zookeeper://127.0.0.1:2182

dubbo.protocols.dubbo.name=dubbo
dubbo.protocols.dubbo.port=20880
dubbo.protocols.hessian.name=hessian
dubbo.protocols.hessian.port=8089
```



### 优先级

优先级从高到低：

- JVM -D参数，当部署或者启动应用时，它可以轻易地重写配置，比如，改变dubbo协议端口；
- XML, XML中的当前配置会重写dubbo.properties中的；
- Properties，默认配置，仅仅作用于以上两者没有配置时。

1：如果在classpath下有超过一个dubbo.properties文件，比如，两个jar包都各自包含了dubbo.properties，dubbo将随机选择一个加载，并且打印错误日志。

2：如果 `id`没有在`protocol`中配置，将使用`name`作为默认属性。



### `@EnableDubboConfig`

`@EnableDubboConfig` 配置 Bean

```java
/**
 * Dubbo 配置 Bean
 */
// multiple : 表示是否支持多Dubbo 配置 Bean 绑定。默认值为 false ，即单 Dubbo 配置 Bean 绑定
@EnableDubboConfig(multiple = false)
@PropertySource("META-INF/config.properties") // 指定dubbo配置是在config.properties文件中
@Configuration
public class DubboConfiguration {

}
```



