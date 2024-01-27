package com.ares.gateway.service;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.game.protoGen.ProtoInner;
import com.game.protoGen.ProtoTask;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface SessionService {
     void loginRequest(AresTKcpContext aresTKcpContext ,ProtoTask.LoginRequest loginRequest);
     void loginSuccess(ProtoInner.InnerGameLoginResponse innerWorldLoginResponse);
     void sendPlayerMsg(long roleId, int msgId, Message body);
     void sendPlayerMsg(long roleId, AresPacket aresPacket);
     void sendPlayerMsg(long roleId, ByteBuf body);
     AresTKcpContext getRoleContext(long roleId);

}
