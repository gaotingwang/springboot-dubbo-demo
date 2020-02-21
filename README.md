# Spring Boot 集成 Dubbo

TODO：

- [x] 自动装配
- [x] 外部化配置
- [x] Zookeeper 注册中心
- [x] 健康检查
- [ ] 服务监控



Dubbo 特性熟悉使用：

- [x] [启动检查](http://dubbo.apache.org/zh-cn/docs/user/demos/preflight-check.html)
- [x] [负载均衡](http://dubbo.apache.org/zh-cn/docs/user/demos/loadbalance.html)
- [x] [异步调用](https://github.com/gaotingwang/springboot-dubbo-demo/commit/47a49dcf5a9139de75bc2f00bb92bd3c5a6618d0)
- [x] [结果缓存](http://dubbo.apache.org/zh-cn/docs/user/demos/result-cache.html)
- [x] [连接](http://dubbo.apache.org/zh-cn/docs/user/demos/config-connections.html)、[并发控制](http://dubbo.apache.org/zh-cn/docs/user/demos/concurrency-control.html)
- [x] [令牌验证](http://dubbo.apache.org/zh-cn/docs/user/demos/token-authorization.html)
- [x] [版本控制](http://dubbo.apache.org/zh-cn/docs/user/demos/multi-versions.html)
- [x] [分组特性]()
- [ ] 本地存根
- [ ] 本地伪装
- [ ] 限流熔断



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


建议在 Provider 端配置的 Provider 端属性有：

```xml
<dubbo:protocol threads="200" /> 
<dubbo:service interface="com.alibaba.hello.api.HelloService" version="1.0.0" ref="helloService"
    executes="200" >
    <dubbo:method name="findAllPerson" executes="50" />
</dubbo:service>
```

1. `threads`：服务线程池大小
2. `executes`：一个服务提供者并行执行请求上限，即当 Provider 对一个服务的并发调用达到上限后，新调用会阻塞，此时 Consumer 可能会超时。在方法上配置 `dubbo:method` 则针对该方法进行并发限制，在接口上配置 `dubbo:service`，则针对该服务进行并发限制



在 Provider 端尽量多配置 Consumer 端属性，原因如下：

- 作服务的提供方，比服务消费方更清楚服务的性能参数，如调用的超时时间、合理的重试次数等
- 在 Provider 端配置后，Consumer 端不配置则会使用 Provider 端的配置，即 Provider 端的配置可以作为 Consumer 的缺省值。否则，Consumer 会使用 Consumer 端的全局设置，这对于 Provider 是不可控的，并且往往是不合理的

建议在 Provider 端配置的 Consumer 端属性有：

1. `timeout`：方法调用的超时时间
2. `retries`：失败重试次数，默认3次
3. `loadbalance`：负载均衡算法，缺省是随机 `random`。还可以配置轮询 `roundrobin`、最不活跃优先`leastactive` 和一致性哈希 `consistenthash` 等
4. `actives`：消费者端的最大并发调用限制，即当 Consumer 对一个服务的并发调用到上限后，新调用会阻塞直到超时，在方法上配置 `dubbo:method` 则针对该方法进行并发限制，在接口上配置 `dubbo:service`，则针对该服务进行并发限制



### [服务消费方](https://github.com/gaotingwang/springboot-dubbo-demo/tree/master/auto-configure-sample/auto-configure-consumer)

1. 通过 `@Reference` 注入 RPC 接口 :

   ```java
   @SpringBootApplication
   @Slf4j
   public class ConsumerApplication {
   
     	// 服务启动过程中验证服务提供者的可用性
     	// 防止A -> B，B -> A 情况，服务无法启动，check=false关闭启动检查
       @Reference(version = "1.0.0", url = "dubbo://127.0.0.1:20880", check = false)
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



## Zookeeper 注册中心

流程说明：

- 服务提供者启动时: 向 `/dubbo/com.foo.DemoService/providers` 目录下写入自己的 URL 地址
- 服务消费者启动时: 订阅 `/dubbo/com.foo.DemoService/providers` 目录下的提供者 URL 地址。并向 `/dubbo/com.foo.DemoService/consumers` 目录下写入自己的 URL 地址
- 监控中心启动时: 订阅 `/dubbo/com.foo.DemoService` 目录下的所有提供者和消费者 URL 地址。

支持以下功能：

- 当提供者出现断电等异常停机时，注册中心能自动删除提供者信息
- 当注册中心重启时，能自动恢复注册数据，以及订阅请求
- 当会话过期时，能自动恢复注册数据，以及订阅请求
- 当设置 `<dubbo:registry check="false" />` 时，记录失败注册和订阅请求，后台定时重试
- 可通过 `<dubbo:registry username="admin" password="1234" />` 设置 zookeeper 登录信息
- 可通过 `<dubbo:registry group="dubbo" />` 设置 zookeeper 的根节点，不配置将使用默认的根节点。
- 支持 `*` 号通配符 `<dubbo:reference group="*" version="*" />`，可订阅服务的所有分组和所有版本的提供者



### 使用

在 provider 和 consumer 中增加 zookeeper 包依赖：

```xml
<!-- Zookeeper dependencies -->
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-dependencies-zookeeper</artifactId>
    <version>${dubbo.version}</version>
    <type>pom</type>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

修改provider的服务注册地址：

```properties
embedded.zookeeper.port = 2181
dubbo.registry.address=zookeeper://127.0.0.1:${embedded.zookeeper.port}
dubbo.registry.file = ${user.home}/dubbo-cache/${spring.application.name}/dubbo.cache
```

修改consumer的接口获取地址，`@Reference`中可以不再指定接口获取的URL：

```properties
embedded.zookeeper.port = 2181
dubbo.registry.address=zookeeper://127.0.0.1:${embedded.zookeeper.port}
dubbo.registry.file=${user.home}/dubbo-cache/${spring.application.name}/dubbo.cache
```



## 健康检查



### Integrate with Maven

```XML
<!-- actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-actuator</artifactId>
    <version>2.7.5</version>
</dependency>
```



### Health Checks Enabled

`management.health.dubbo.enabled` is a enabled configuration to turn on or off health checks feature, its' default is `true`.

If you'd like to disable health checks , you chould apply `management.health.dubbo.enabled` to be `false`:

```
management.health.dubbo.enabled = false
```



### Endpoints Enabled

Dubbo Spring Boot providers actuator endpoints , however some of them are disable. If you'd like to enable them , please add following properties into externalized configuration :

```
# Enables Dubbo All Endpoints
management.endpoints.web.exposure.include=*
management.endpoint.dubbo.enabled = true
management.endpoint.dubboshutdown.enabled = true
management.endpoint.dubboconfigs.enabled = true
management.endpoint.dubboservices.enabled = true
management.endpoint.dubboreferences.enabled = true
management.endpoint.dubboproperties.enabled = true
```



### Endpoints

Actuator endpoint `dubbo` supports Actuator Endpoints :

| ID                | Enabled | HTTP URI                     | HTTP Method | Description                         |
| ----------------- | ------- | ---------------------------- | ----------- | ----------------------------------- |
| `dubbo`           | `true`  | `/actuator/dubbo`            | `GET`       | Exposes Dubbo's meta data           |
| `dubboproperties` | `true`  | `/actuator/dubbo/properties` | `GET`       | Exposes all Dubbo's Properties      |
| `dubboservices`   | `false` | `/actuator/dubbo/services`   | `GET`       | Exposes all Dubbo's `ServiceBean`   |
| `dubboreferences` | `false` | `/actuator/dubbo/references` | `GET`       | Exposes all Dubbo's `ReferenceBean` |
| `dubboconfigs`    | `true`  | `/actuator/dubbo/configs`    | `GET`       | Exposes all Dubbo's `*Config`       |
| `dubboshutdown`   | `false` | `/actuator/dubbo/shutdown`   | `POST`      | Shutdown Dubbo services             |





