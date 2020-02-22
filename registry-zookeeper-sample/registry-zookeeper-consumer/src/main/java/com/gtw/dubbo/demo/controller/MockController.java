package com.gtw.dubbo.demo.controller;

import com.gtw.dubbo.rpc.api.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockController {

    @Reference(version = "${demo.service.version}",
            timeout = 1000,
            stub = "com.gtw.dubbo.demo.stub.DemoServiceStub",
            mock = "com.gtw.dubbo.demo.mock.DemoServiceMock")
    private DemoService demoService;

    @GetMapping(value = "/mock-say-hello")
    public String sayHello(@RequestParam(required = false) String name) {
        return demoService.sayHello(name);
    }

}
