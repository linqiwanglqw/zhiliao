package com.lin.zhiliaoAdmin.service;

import org.springframework.security.core.GrantedAuthority;

public class MySimpleGrantedAuthority implements GrantedAuthority {
    private String authority;
    private String path;

    public MySimpleGrantedAuthority() {
    }

    public MySimpleGrantedAuthority(String authority) {
        this.authority = authority;
    }

    public MySimpleGrantedAuthority(String authority, String path) {
        this.authority = authority;
        this.path = path;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public String getPath() {
        return path;
    }
}
