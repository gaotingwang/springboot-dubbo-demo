package com.gtw.dubbo.demo.group;

import com.gtw.dubbo.rpc.group.GroupService;
import com.gtw.dubbo.rpc.model.User;
import org.apache.dubbo.config.annotation.Service;

import java.util.ArrayList;
import java.util.List;

// 当一个接口有多种实现时，可以用 group 区分，也可用于分组发布、调用
@Service(version = "1.0", group = "group1", interfaceClass = GroupService.class)
public class GroupServiceImp1 implements GroupService {

    @Override
    public List<User> getUserInfo() {
        User user = new User();
        user.setUsername("李四");
        user.setPassword("456");
        user.setAge(20);

        List<User> list = new ArrayList<>();
        list.add(user);
        return list;
    }

}
