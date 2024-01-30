package com.ares.world.network;

import com.ares.core.bean.AresPacket;
import com.ares.core.tcp.AresTKcpContext;
import com.game.protoGen.ProtoInner;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

public  class PeerTransfer {
    private   ProtoInner.InnerMsgHeader peerHeader;

    @Setter
    private AresTKcpContext context;
    public PeerTransfer(AresTKcpContext context, long pid){
        if(context == null){
            return;
        }
        this.context = context;
        peerHeader =ProtoInner.InnerMsgHeader.newBuilder().setRoleId(pid).build();
    }
    public void disconnected(){
        this.context = null;
    }

    public void close(){
        if(this.context != null){
            this.context.close();
            this.context = null;
        }
    }

   public   void send(int msgId, Message body){
       AresPacket aresPacket =AresPacket.create(msgId,peerHeader, body);
       context.send(aresPacket);
     }

    public   void send(Map<Integer,Message> msgs){
        Set<Map.Entry<Integer, Message>> entries = msgs.entrySet();
        AresPacket[] packets = new AresPacket[msgs.size()];
        int index =0;
        for(Map.Entry<Integer, Message> entry: entries){
            packets[index++] =AresPacket.create(entry.getKey(),peerHeader, entry.getValue());
        }
        context.send(packets);
    }
}
