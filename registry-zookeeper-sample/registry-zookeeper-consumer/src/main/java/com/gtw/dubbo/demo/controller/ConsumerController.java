package com.gtw.dubbo.demo.controller;

import com.gtw.dubbo.rpc.api.DemoService;
import com.gtw.dubbo.rpc.service.AsyncService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class ConsumerController {

    @Reference(version = "${demo.service.version}", check = false, cache = "lru", connections = 10)
    private DemoService demoService;

    @Reference(async = true, timeout = 10000)
    private AsyncService asyncService;

    @GetMapping(value = "/say-hello")
    public String sayHello(@RequestParam String name) {
        return demoService.sayHello(name);
    }

    @GetMapping(value = "/async-say-hello")
    public String AsyncSayHello(@RequestParam String name) throws ExecutionException, InterruptedException {
        // 调用直接返回CompletableFuture
        CompletableFuture<String> future = asyncService.sayHello("async call request : " + name);
        // 增加回调
        future.whenComplete((v, t) -> {
            if (t != null) {
                t.printStackTrace();
            } else {
                System.out.println("Response: " + v);
            }
        });
        // 早于结果输出
        System.out.println("Executed before response return.");
        return future.get();

//        // 此调用会立即返回null
//        asyncService.sayHello(name);
//        // 拿到调用的Future引用，当结果返回后，会被通知和设置到此Future
//        Future<String> future = RpcContext.getContext().getFuture();
//        return future.get();
    }
}
