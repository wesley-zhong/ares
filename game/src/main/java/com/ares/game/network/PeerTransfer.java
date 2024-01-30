package com.ares.game.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.game.protoGen.ProtoInner;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

public class PeerTransfer {

    @Setter
    private AresTKcpContext gateWayContext;
    @Setter
    private AresTKcpContext worldContext;
    private ProtoInner.InnerMsgHeader msgHeader;

    public PeerTransfer() {

    }

    public PeerTransfer(AresTKcpContext gateWayContext, long pid) {
        if (gateWayContext == null) {
            return;
        }
        msgHeader = ProtoInner.InnerMsgHeader.newBuilder().setRoleId(pid).build();
        this.gateWayContext = gateWayContext;
    }

    public void disconnected() {
        this.gateWayContext = null;
    }

    public void close() {
        if (this.gateWayContext != null) {
            this.gateWayContext.close();
        }
    }

    public void sendToGateway(int msgId, Message body) {
        AresPacket aresPacket = AresPacket.create(msgId, msgHeader, body);
        gateWayContext.send(aresPacket);
    }

    public void sendToGateway(AresPacket aresPacket) {
        gateWayContext.send(aresPacket);
    }

    public void sendToGateway(ByteBuf body) {
        gateWayContext.send(body);
    }

    public void sendToGateway(Map<Integer, Message> msgs) {
        Set<Map.Entry<Integer, Message>> entries = msgs.entrySet();
        AresPacket[] packets = new AresPacket[entries.size()];
        int index = 0;
        for (Map.Entry<Integer, Message> entry : entries) {
            packets[index++] = AresPacket.create(entry.getKey(), msgHeader, entry.getValue());
        }
        gateWayContext.send(packets);
    }

    public void sendToWorld(int msgId, Message body) {
        AresPacket aresPacket = AresPacket.create(msgId, msgHeader, body);
        worldContext.send(aresPacket);
    }

    public void sendToWorld(AresPacket aresPacket) {
        worldContext.send(aresPacket);
    }

    public void sendToWorld(ByteBuf body) {
        worldContext.send(body);
    }

    public void sendToWorld(Map<Integer, Message> msgs) {
        Set<Map.Entry<Integer, Message>> entries = msgs.entrySet();
        AresPacket[] packets = new AresPacket[entries.size()];
        int index = 0;
        for (Map.Entry<Integer, Message> entry : entries) {
            packets[index++] = AresPacket.create(entry.getKey(), msgHeader, entry.getValue());
        }
        worldContext.send(packets);
    }
}
