package com.ares.transport.bean;

import com.ares.common.bean.ServerInfo;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TcpConnServerInfo {
    private Channel channel;
    private ServerInfo serverInfo;
}
