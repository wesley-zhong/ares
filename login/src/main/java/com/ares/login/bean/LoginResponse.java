package com.ares.login.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private int areaId;
    private String accountId;
    private  long roleId;
    private String secret;
    private String serverAddr;
}
