package com.ares.core.tcp;

import com.ares.core.bean.AresPacket;
import com.ares.core.bean.AresRpcMethod;
import com.ares.core.exception.AresBaseException;
import com.ares.core.service.ServiceMgr;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
public class TcpRequestTcpHandler implements AresTcpHandler {
    @Autowired
    private ServiceMgr serviceMgr;
    @Autowired
    private TcpNetWorkHandler tcpNetWorkHandler;
    protected static final String UTF8 = "UTF-8";


    /**
     * this for rpc asynCall by msgId
     */
    @Override
    public void handleMsgRcv(AresPacket aresPacket) {
        int length = 0;
        try {
            AresRpcMethod calledMethod = serviceMgr.getCalledMethod(aresPacket.getMsgId());
            if (calledMethod == null) {
                tcpNetWorkHandler.handleMsgRcv(aresPacket);
                return;
            }
//            Class<?> paramClass = calledMethod.getParamClass();
//            //no parameters
//            if (paramClass == null) {
//                calledMethod.getAresServiceProxy().callMethod(null, calledMethod);
//                return;
//            }
//            if (paramClass == ByteBuf.class) {
//                calledMethod.getAresServiceProxy().callMethod(aresPacket.getRecvByteBuf(), calledMethod);
//                return;
//            }
//            if (paramClass == AresPacket.class) {
//                calledMethod.getAresServiceProxy().callMethod(aresPacket, calledMethod);
//                return;
//            }
            length = aresPacket.getRecvByteBuf().readableBytes();
            Object paraObj = calledMethod.getParser().parseFrom(new ByteBufInputStream(aresPacket.getRecvByteBuf(), length));
            calledMethod.getAresServiceProxy().callMethod(paraObj, calledMethod);
        } catch (AresBaseException e) {
            log.error("===error  length ={} msgId={} ", length, aresPacket.getMsgId(), e);
        } catch (Throwable e) {
            log.error("==error length ={} msgId ={}  ", length, aresPacket.getMsgId(), e);
        }
    }

    @Override
    public void onClientConnected(AresTKcpContext ctx) {
        tcpNetWorkHandler.onClientConnected(ctx);
    }

    @Override
    public void onClientClosed(AresTKcpContext channelHandlerContext) {
        tcpNetWorkHandler.onClientClosed(channelHandlerContext);
    }

    @Override
    public void onServerConnected(Channel channel) {
        tcpNetWorkHandler.onServerConnected(channel);
    }

    @Override
    public void onServerClosed(Channel channel) {
        tcpNetWorkHandler.onServerClosed(channel);
    }

    @Override
    public boolean isChannelValidate(AresTKcpContext channel) {
        return tcpNetWorkHandler.isChannelValidate(channel);
    }
}
