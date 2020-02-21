package com.gtw.dubbo.demo.controller;

import com.gtw.dubbo.rpc.group.GroupService;
import com.gtw.dubbo.rpc.model.User;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GroupController {

    @Reference(group = "group1")
    private GroupService groupService1;
//    @Reference(group = "group2", interfaceClass = GroupService.class)
//    private GroupService groupService2;

    @GetMapping(value = "/users")
    public List<User> getUsers() {
        return groupService1.getUserInfo();
    }

}
