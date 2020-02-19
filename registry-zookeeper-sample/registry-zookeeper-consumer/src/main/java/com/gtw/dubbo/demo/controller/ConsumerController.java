package com.gtw.dubbo.demo.controller;

import com.gtw.dubbo.rpc.api.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @Reference(version = "${demo.service.version}")
    private DemoService demoService;

    @GetMapping(value = "/say-hello")
    public String sayHello(@RequestParam String name) {
        return demoService.sayHello(name);
    }
}
