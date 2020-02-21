package com.gtw.dubbo.rpc.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private String username;
    private transient String password;
    private int age;
}
