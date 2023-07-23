package com.ares.core.bean;

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
    private Object sender;
    @Getter
    @Setter
    private ByteBuf recvByteBuf;
    @Getter
    @Setter
    private long checkSum;

    private byte[] sendBody;


    public static AresPacket create(int msgId) {
        // AresPacket aresPacket = RECYCLER.get();
        // log.info("create object msg ={}", msgId);
        AresPacket aresPacket = new AresPacket();
        aresPacket.setMsgId(msgId);
        return aresPacket;
    }

    public ByteBuf kcpEncode() {
        short msgLen = 2;
        byte[] body = bodyEncode();
        if (body != null) {
            msgLen += body.length;
        }
        ByteBuf msgBuf = PooledByteBufAllocator.DEFAULT.buffer(msgLen);
        msgBuf.writeShort(msgId);
        if (body != null) {
            msgBuf.writeBytes(body);
        }
        return msgBuf;
    }

    public ByteBuf kcpCheckSumEncode() {
        short msgLen = 6;
        byte[] body = bodyEncode();
        if (body != null) {
            msgLen += body.length;
        }
        ByteBuf msgBuf = PooledByteBufAllocator.DEFAULT.buffer(msgLen);
        msgBuf.writeShort(msgId);
        msgBuf.writeInt((int) checkSum);
        if (body != null) {
            msgBuf.writeBytes(body);
        }
        return msgBuf;

    }

    /**
     * not include msgId and msg len
     *
     * @return
     */
    public byte[] bodyEncode() {
        return null;
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

    public ByteBuf tcpSendBodyEncode() {
        short msgLen = 4;
        byte[] body = bodyEncode();
        if (body != null) {
            msgLen += body.length;
        }
        ByteBuf msgBuf = PooledByteBufAllocator.DEFAULT.buffer(msgLen);
        msgBuf.writeShort(msgLen - 2);
        msgBuf.writeShort(msgId);
        if (body != null) {
            msgBuf.writeBytes(body);
        }
        return msgBuf;

    }

    public void release() {
        // log.info("release object msg ={}", msgId);
        clear();
        //recyclerHandle.recycle(this);
    }

    private AresPacket() {
    }
}
