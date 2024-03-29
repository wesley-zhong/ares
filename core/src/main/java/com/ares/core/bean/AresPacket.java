package com.ares.core.bean;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * create/release manually like ByteBuf
 */
@Slf4j
public class AresPacket {
    @Getter
    @Setter
    private int msgId;
    @Getter
    @Setter
    private Message sendBody;
    @Getter
    @Setter
    private Message sendHeader;
    @Getter
    @Setter
    private ByteBuf recvByteBuf;
    @Getter
    @Setter
    private long checkSum;


    public static AresPacket create(int msgId, Message body) {
        // AresPacket aresPacket = RECYCLER.get();
        // log.info("create object msg ={}", msgId);
        AresPacket aresPacket = new AresPacket();
        aresPacket.msgId  = msgId;
        aresPacket.sendBody = body;
        return aresPacket;
    }
    public static AresPacket create(int msgId, Message header, Message body){
        AresPacket aresPacket = new AresPacket();
        aresPacket.msgId  = msgId;
        aresPacket.sendBody = body;
        aresPacket.sendHeader = header;
        return aresPacket;
    }

    public static AresPacket create(int msgId){
        AresPacket aresPacket = new AresPacket();
        aresPacket.msgId  = msgId;
        return aresPacket;
    }


    /**
     * not include msgId and msg len
     *
     * @return
     */
    public byte[] bodyEncode() {
        if (sendBody == null) {
            return null;
        }
       return sendBody.toByteArray();
    }

    public byte[] headerEncode(){
        if(sendHeader == null){
            return  null;
        }
        return  sendHeader.toByteArray();
    }

    private void clear() {
        /***
         *  this package will be reused more times so do not clear data ,
         *  but when the recvByteBuf (this packet created by network read) is not null we should release it
         */
        if (recvByteBuf != null) {
            recvByteBuf.release();
            recvByteBuf = null;
        }
    }

    public void release() {
        // log.info("release object msg ={}", msgId);
        clear();
        //recyclerHandle.recycle(this);
    }

    private AresPacket() {
    }
}
