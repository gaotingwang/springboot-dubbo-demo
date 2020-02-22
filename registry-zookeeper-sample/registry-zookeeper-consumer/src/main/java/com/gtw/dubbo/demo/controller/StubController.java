package com.gtw.dubbo.demo.controller;

import com.gtw.dubbo.rpc.api.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StubController {

    // 每客户端并发执行（或占用连接的请求数）不能超过 10 个
    @Reference(version = "${demo.service.version}", cache = "lru", timeout = 10000,
            stub = "com.gtw.dubbo.demo.stub.DemoServiceStub")
    private DemoService demoService;

    @GetMapping(value = "/stub-say-hello")
    public String sayHello(@RequestParam(required = false) String name) {
        return demoService.sayHello(name);
    }

}
