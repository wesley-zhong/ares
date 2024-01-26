package com.ares.world.service;

import com.ares.common.bean.ServerType;
import com.ares.world.network.PeerConn;
import com.game.protoGen.ProtoInner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerService {
    @Autowired
    private WorldPlayerMgr worldPlayerMgr;
    @Autowired
    private PeerConn peerConn;

    public void playerLogin(long roleId, ProtoInner.InnerLoginWorldRequest longinReq) {
        ProtoInner.InnerWorldLoginResponse.Builder builder = ProtoInner.InnerWorldLoginResponse.newBuilder();
        builder.setRoleId(longinReq.getRoleId());
        ProtoInner.InnerWorldLoginResponse response = builder.build();
        peerConn.sendToGame(roleId,ProtoInner.InnerProtoCode.INNER_TO_WORLD_LOGIN_RES_VALUE, response);
    }
}
