# Spring Boot 集成 Dubbo

TODO：

- [x] 自动装配
- [ ] 外部化配置
- [ ] Zookeeper 注册中心
- [ ] 健康检查
- [ ] 控制端点



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

   

###[服务消费方](https://github.com/gaotingwang/springboot-dubbo-demo/tree/master/auto-configure-sample/auto-configure-consumer)

1. 通过 `@Reference` 注入 RPC 接口 :

   ```java
   @SpringBootApplication
   @Slf4j
   public class ConsumerApplication {
   
       @Reference(version = "1.0.0", url = "dubbo://127.0.0.1:12345")
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

   

