package com.ares.client;

import com.ares.core.bean.AresPacket;
import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoInner;
import  com.game.protoGen.ProtoTask;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

@Component
public class LoginService{


    public void loginRequest(Channel channel, long roleId){
        ProtoTask.LoginRequest.Builder loginRequest = ProtoTask.LoginRequest.newBuilder()
                .setAccountId(1000L)
                .setLoginToken("abc")
                .setRoleId(roleId);
        ProtoCommon.MsgHeader header = ProtoCommon.MsgHeader.newBuilder()
                .setMsgId(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE)
                .build();

        AresPacket  aresPacket = AresPacket.create(header,loginRequest.build());
        channel.writeAndFlush(aresPacket);
    }
}
