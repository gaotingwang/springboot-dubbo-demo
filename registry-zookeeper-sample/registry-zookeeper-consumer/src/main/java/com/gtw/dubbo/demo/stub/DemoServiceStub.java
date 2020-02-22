package com.gtw.dubbo.demo.stub;

import com.gtw.dubbo.rpc.api.DemoService;
import org.springframework.util.StringUtils;

public class DemoServiceStub implements DemoService {

    private final DemoService demoService;

    // Stub 必须有接受 Proxy 参数的构造函数
    public DemoServiceStub(DemoService demoService) {
        this.demoService = demoService;
    }

    // 本地存根就是对rpc接口的proxy起一个装饰器作用
    // 在客户端执行部分逻辑, 之后交由proxy执行
    @Override
    public String sayHello(String name) {
        // 此代码在客户端执行, 可以在客户端做ThreadLocal本地缓存，或预先验证参数是否合法，等等，
        // 进一步判断是否要调用rpc的proxy接口
        if(StringUtils.isEmpty(name)) {
            return "name is null !";
        }
        return demoService.sayHello(name);
    }
}
