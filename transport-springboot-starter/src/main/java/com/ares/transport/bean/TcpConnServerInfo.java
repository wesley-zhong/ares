package com.ares.transport.bean;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TcpConnServerInfo {
    private Channel channel;
    private ServerNodeInfo serverNodeInfo;
}
