package com.router.bean;

import com.ares.common.bean.ServerType;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouterPlayerInterTransferInfo {
    private ChannelHandlerContext gameCtx;
    private ChannelHandlerContext team;


    public ChannelHandlerContext getContextByType(int serverType) {

        if (serverType == ServerType.GAME.getValue()) {
            return gameCtx;
        }
        if (serverType == ServerType.TEAM.getValue()) {
            return team;
        }
        return null;
    }

    public void setContext(int serverType, ChannelHandlerContext channelHandlerContext) {
        if (serverType == ServerType.GAME.getValue()) {
            this.gameCtx = channelHandlerContext;
            return;
        }
        if (serverType == ServerType.TEAM.getValue()) {
            this.team = channelHandlerContext;
            return;
        }
        log.error("serverType ={} not allowed here", serverType);
    }
}
