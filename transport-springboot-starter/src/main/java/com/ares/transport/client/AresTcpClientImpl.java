package com.ares.transport.client;

import com.ares.common.bean.ServerInfo;
import com.ares.core.bean.AresPacket;
import com.ares.transport.bean.TcpConnServerInfo;
import com.google.protobuf.Message;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class AresTcpClientImpl extends AresTcpClientBase {
    private final AresTcpClientConn aresTcpClientConn;

    public AresTcpClientImpl(List<ServerInfo> serverInfos, AresTcpClientConn aresTcpClientConn) {
        super(serverInfos);
        this.aresTcpClientConn = aresTcpClientConn;
    }

    public void init() {
        super.init();
    }

    @Override
    protected Channel connect(ServerInfo serverInfo) {
        return aresTcpClientConn.connect(serverInfo.getIp(), serverInfo.getPort());
    }

    @Override
    public void send(int msgId, int serverId, Message body) {
        TcpConnServerInfo tcpConnServerInfo = tcpConnServerInfoMap.get(serverId);
        if (tcpConnServerInfo == null || !tcpConnServerInfo.getChannel().isActive()) {
            log.error("serverId ={} not found", serverId);
            return;
        }
        super.send(tcpConnServerInfo.getChannel(), msgId, body);
    }

    @Override
    public void send(int serverId, AresPacket... packets) {
        TcpConnServerInfo tcpConnServerInfo = tcpConnServerInfoMap.get(serverId);
        if (tcpConnServerInfo == null || !tcpConnServerInfo.getChannel().isActive()) {
            log.error("serverId ={} not found", serverId);
            return;
        }
        super.send(tcpConnServerInfo.getChannel(), packets);
    }


    @Override
    public void send(int serverId, AresPacket packet) {
        TcpConnServerInfo tcpConnServerInfo = tcpConnServerInfoMap.get(serverId);
        if (tcpConnServerInfo == null || !tcpConnServerInfo.getChannel().isActive()) {
            log.error("serverId ={} not found", serverId);
            return;
        }
        super.send(tcpConnServerInfo.getChannel(), packet);
    }
}
