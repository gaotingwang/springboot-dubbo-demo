package com.gtw.dubbo.demo;

import com.gtw.dubbo.rpc.api.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
