package com.ares.core.tcp;

import com.ares.core.bean.AresPacket;
import com.ares.core.bean.AresRpcMethod;
import com.ares.core.exception.AresBaseException;
import com.ares.core.service.ServiceMgr;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public  abstract class AresTcpHandlerBase implements  AresTcpHandler{
    @Autowired
    protected ServiceMgr serviceMgr;
    @Autowired
    protected TcpNetWorkHandler tcpNetWorkHandler;
    protected static final String UTF8 = "UTF-8";

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
