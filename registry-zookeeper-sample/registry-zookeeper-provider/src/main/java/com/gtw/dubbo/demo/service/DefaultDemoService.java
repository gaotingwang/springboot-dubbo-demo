package com.gtw.dubbo.demo.service;

import com.gtw.dubbo.rpc.api.DemoService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;

// 每个接口都应定义版本号，为后续不兼容升级提供可能
// 限制服务器端接受的连接不能超过 10 个
// 服务器端并发执行（或占用线程池线程数）不能超过 10 个
@Service(version = "1.0.0", connections = 10, executes = 10)
public class DefaultDemoService implements DemoService {

    @Value("${dubbo.application.name}")
    private String serviceName;

    public String sayHello(String name) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s = String.format("[%s] : Hello, %s", serviceName, name);
        System.out.println(s);
        return s;
    }
}
