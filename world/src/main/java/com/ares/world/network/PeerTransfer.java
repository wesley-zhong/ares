package com.ares.world.network;

import com.ares.core.bean.AresPacket;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.Set;

public  class PeerTransfer {

    private ChannelHandlerContext context;
    public PeerTransfer(ChannelHandlerContext context){
        if(context == null){
            return;
        }
        this.context = context;
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
       AresPacket aresPacket =AresPacket.create(msgId, body);
       context.writeAndFlush(aresPacket);
     }

    public   void send(Map<Integer,Message> msgs){
        Set<Map.Entry<Integer, Message>> entries = msgs.entrySet();
        for(Map.Entry<Integer, Message> entry: entries){
            AresPacket aresPacket =AresPacket.create(entry.getKey(), entry.getValue());
            context.write(aresPacket);
        }
        context.flush();
    }
}
