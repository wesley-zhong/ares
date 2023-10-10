package com.ares.gateway.service;

import com.ares.core.tcp.AresTKcpContext;
import com.game.protoGen.ProtoTask;

public interface SessionService {
     void loginRequest(AresTKcpContext aresTKcpContext , ProtoTask.LoginRequest loginRequest);
}
