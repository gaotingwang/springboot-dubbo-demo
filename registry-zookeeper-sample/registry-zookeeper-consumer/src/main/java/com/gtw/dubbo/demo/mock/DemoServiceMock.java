package com.gtw.dubbo.demo.mock;

import com.gtw.dubbo.rpc.api.DemoService;

public class DemoServiceMock implements DemoService {

    // Mock 是 Stub 的一个子集，便于服务提供方在客户端执行容错逻辑
    // 只针对服务调用发生RpcException时才会执行服务降级,其他Exception不会执行降级
    @Override
    public String sayHello(String name) {
        return "接口调用异常，无法say hello";
    }
}
