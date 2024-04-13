package com.ares.login.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SdkLoginResponse {
    private String accountId;
    private long roleId;
    private int areaId;
    private String secret; //server  token
}
