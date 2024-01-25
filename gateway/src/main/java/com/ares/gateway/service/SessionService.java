package com.ares.gateway.service;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.game.protoGen.ProtoInner;
import com.game.protoGen.ProtoTask;
import com.google.protobuf.Message;

public interface SessionService {
     void loginRequest(AresTKcpContext aresTKcpContext ,ProtoTask.LoginRequest loginRequest);
     void sendPlayerMsg(long roleId, int msgId, Message body);
     void sendPlayerMsg(long roleId, AresPacket aresPacket);

}
