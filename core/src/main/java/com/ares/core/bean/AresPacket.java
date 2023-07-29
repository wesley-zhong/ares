package com.ares.core.bean;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
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
    private int msgId; //unsigned short
    @Getter
    @Setter
    private Message senderObj;
    @Getter
    @Setter
    private ByteBuf recvByteBuf;
    @Getter
    @Setter
    private long checkSum;

    private byte[] sendBody;


    public static AresPacket create(int msgId, Message body) {
        // AresPacket aresPacket = RECYCLER.get();
        // log.info("create object msg ={}", msgId);
        AresPacket aresPacket = new AresPacket();
        aresPacket.msgId  = msgId;
        aresPacket.senderObj = body;
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
        if (senderObj == null) {
            return null;
        }
       return senderObj.toByteArray();
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
