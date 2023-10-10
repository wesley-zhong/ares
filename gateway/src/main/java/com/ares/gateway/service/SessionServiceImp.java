package com.ares.gateway.service;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.transport.client.AresTcpClient;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import com.game.protoGen.ProtoTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionServiceImp implements  SessionService {
    @Autowired
    private AresTcpClient aresTcpClient;
    public void loginRequest(AresTKcpContext aresTKcpContext ,ProtoTask.LoginRequest loginRequest){
        log.info("==============loginRequest");
        /****
         * do something
         */

        ProtoInner.InnerGameLoginRequest innerLoginRequest = ProtoInner.InnerGameLoginRequest.newBuilder()
                .setRoleId(loginRequest.getRoleId())
                .setSid(1000).build();

        aresTcpClient.send(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE, 100,innerLoginRequest);

        ProtoTask.LoginResponse.Builder response = ProtoTask.LoginResponse.newBuilder()
                .setErrorCode(0)
                .setRoleId(123)
                .setServerTime(System.currentTimeMillis());
        aresTKcpContext.send(AresPacket.create(ProtoCommon.ProtoCode.LOGIN_RESPONSE_VALUE, response.build()));

    }
}
