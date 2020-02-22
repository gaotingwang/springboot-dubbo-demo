package com.gtw.dubbo.demo.controller;

import com.gtw.dubbo.rpc.api.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    // 每客户端并发执行（或占用连接的请求数）不能超过 10 个
    @Reference(version = "${demo.service.version}", check = false, cache = "lru", connections = 10, actives = 10)
    private DemoService demoService;

    @GetMapping(value = "/say-hello")
    public String sayHello(@RequestParam String name) {
        return demoService.sayHello(name);
    }

}
