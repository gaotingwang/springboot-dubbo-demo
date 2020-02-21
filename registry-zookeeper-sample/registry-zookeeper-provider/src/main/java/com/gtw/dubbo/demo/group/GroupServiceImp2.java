package com.gtw.dubbo.demo.group;

import com.gtw.dubbo.rpc.group.GroupService;
import com.gtw.dubbo.rpc.model.User;
import org.apache.dubbo.config.annotation.Service;

import java.util.ArrayList;
import java.util.List;

// 当一个接口有多种实现时，可以用 group 区分，也可用于分组发布、调用
@Service(version = "2.0", group = "group2", interfaceClass = GroupService.class)
public class GroupServiceImp2 implements GroupService {

    @Override
    public List<User> getUserInfo() {
        User user = new User();
        user.setUsername("张三");
        user.setPassword("123");
        user.setAge(18);

        List<User> list = new ArrayList<>();
        list.add(user);
        return list;
    }

}
