package com.router.network;

import com.ares.common.bean.ServerType;
import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.bean.AresPacket;
import com.ares.core.service.ServiceMgr;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.thread.LogicProcessThreadPool;
import com.game.protoGen.ProtoInner;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;


@Slf4j
public class RouterMsgHandler implements AresTcpHandler {
    @Autowired
    protected ServiceMgr serviceMgr;

    @Autowired
    private PeerConn peerConn;

    protected static final String UTF8 = "UTF-8";

    @Override
    public void handleMsgRcv(AresTKcpContext aresTKcpContext) throws IOException {
        AresPacket aresPacket = aresTKcpContext.getRcvPackage();
        AresMsgIdMethod calledMethod = serviceMgr.getCalledMethod(aresPacket.getMsgId());

        aresPacket.getRecvByteBuf().skipBytes(6);
        int headerLen = aresPacket.getRecvByteBuf().readShort();
        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), headerLen));
        long pid = header.getRoleId();
        if (calledMethod == null) {
            int toServerType = header.getRouterTo();
            if (toServerType == ServerType.TEAM.getValue()) {
                peerConn.sendToTeam(pid, aresPacket);
                return;
            }
            if (toServerType == ServerType.GAME.getValue()) {
                peerConn.sendToGame(pid, aresPacket);
                return;
            }
            log.error("XXXXXXXXXXXXXXX msgId ={} to server type ={} error", aresPacket.getMsgId(), toServerType);
            return;
        }
        int length = aresPacket.getRecvByteBuf().readableBytes();
        Object paraObj = calledMethod.getParser().parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), length));
        LogicProcessThreadPool.INSTANCE.execute(aresTKcpContext, calledMethod, pid, paraObj);
    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {
    }

    @Override
    public void onClientConnected(AresTKcpContext aresTKcpContext) {
        log.info("---onClientConnected ={} ", aresTKcpContext);
    }

    @Override
    public void onClientClosed(AresTKcpContext aresTKcpContext) {
        log.info("-----onClientClosed={} ", aresTKcpContext);
    }

    @Override
    public boolean isChannelValidate(AresTKcpContext aresTKcpContext) {
        return true;
    }


    @Override
    public void onServerClosed(Channel aresTKcpContext) {
    }
}
