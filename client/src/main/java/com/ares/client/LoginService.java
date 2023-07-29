package com.ares.client;

import com.ares.core.bean.AresPacket;
import com.game.proto.ProtoCommon;
import com.game.proto.ProtoTask;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

@Component
public class LoginService{


    public void loginRequest(Channel channel){
        ProtoTask.LoginRequest.Builder loginRequest = ProtoTask.LoginRequest.newBuilder()
                .setAccountId(1000L)
                .setLoginToken("abc")
                .setAccountId(1003L);

        AresPacket  aresPacket = AresPacket.create(ProtoCommon.ProtoCode.LOGIN_REQUEST_VALUE,loginRequest.build());
        channel.writeAndFlush(aresPacket);
    }

}
