package com.ares.team.network;


import com.ares.common.bean.ServerType;
import com.ares.transport.peer.PeerConnBase;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class PeerConn extends PeerConnBase {
    private final Map<Long, ChannelHandlerContext> playerIdContext = new ConcurrentHashMap<>();

    public void routerToGame(long roleId, int msgId, Message body) {
        routerTo(ServerType.GAME, roleId, msgId, body);
    }

    @Override
    public ChannelHandlerContext loadBalance(int serverType, long roleId, Map<String, ChannelHandlerContext> channelConMap) {
        //rewrite
        ChannelHandlerContext channelHandlerContext = playerIdContext.get(roleId);
        if(channelHandlerContext == null || !channelHandlerContext.channel().isActive()){
            if (CollectionUtils.isEmpty(channelConMap)) {
                log.error(" loadBalance serverType ={} roleId ={} not found", serverType, roleId);
                return null;
            }
            ChannelHandlerContext routerContext = channelConMap.values().iterator().next();
            playerIdContext.put(roleId, routerContext);
            return  routerContext;
        }
        return channelConMap.values().iterator().next();
    }
}
