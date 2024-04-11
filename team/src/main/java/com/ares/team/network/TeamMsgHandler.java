package com.ares.team.network;

import com.ares.core.bean.AresMsgIdMethod;
import com.ares.core.bean.AresPacket;
import com.ares.core.service.ServiceMgr;
import com.ares.core.tcp.AresTKcpContext;
import com.ares.core.tcp.AresTcpHandler;
import com.ares.core.thread.LogicProcessThreadPool;
import com.ares.discovery.DiscoveryService;
import com.ares.transport.bean.ServerNodeInfo;
import com.game.protoGen.ProtoInner;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;


@Slf4j
public class TeamMsgHandler implements AresTcpHandler {
    @Autowired
    private ServiceMgr serviceMgr;
    @Autowired
    private DiscoveryService discoveryService;

    @Override
    public void handleMsgRcv(AresTKcpContext aresTKcpContext) throws IOException {
        AresPacket aresPacket = aresTKcpContext.getRcvPackage();
        AresMsgIdMethod calledMethod = serviceMgr.getCalledMethod(aresPacket.getMsgId());
        aresPacket.getRecvByteBuf().skipBytes(6);
        if (calledMethod == null) {
            log.error("msgId ={} not found call function", aresPacket.getMsgId());
            return;
        }
        int headerLen = aresPacket.getRecvByteBuf().readShort();
        long pid = 0;
        if (headerLen > 0) {
            ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), headerLen));
            pid = header.getRoleId();
        }

        int length = aresPacket.getRecvByteBuf().readableBytes();
        Object paraObj = calledMethod.getParser().parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), length));
        LogicProcessThreadPool.INSTANCE.execute(aresTKcpContext, calledMethod, pid, paraObj);
    }

    @Override
    public void onServerConnected(Channel aresTKcpContext) {
        ServerNodeInfo myselfNodeInfo = discoveryService.getEtcdRegister().getMyselfNodeInfo();
        ProtoInner.InnerServerHandShakeReq handleShake = ProtoInner.InnerServerHandShakeReq.newBuilder()
                .setServiceId(myselfNodeInfo.getServiceId())
                .setServiceName(myselfNodeInfo.getServiceName()).build();

        ProtoInner.InnerMsgHeader header = ProtoInner.InnerMsgHeader.newBuilder().build();
        AresPacket aresPacket = AresPacket.create(ProtoInner.InnerProtoCode.INNER_SERVER_HAND_SHAKE_REQ_VALUE, header, handleShake);
        aresTKcpContext.writeAndFlush(aresPacket);
        log.info("######  handshake send to {}  msg: {}", aresTKcpContext, handleShake);
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
