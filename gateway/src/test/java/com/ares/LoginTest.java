package com.ares;

import com.ares.gateway.client.TcpClient;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class LoginTest {
    private TcpClient  tcpClient;
    @Before
    public void before(){
        log.info("------ before");
       // tcpClient

    }

    @Test
    public void test(){
        assert true;
    }
}
