package com.ares.client.controller;

import com.ares.client.Client;
import com.ares.client.performance.PerformanceTestService;
import com.ares.core.annotation.MsgId;
import com.ares.core.bean.AresPacket;
import com.ares.core.service.AresController;

import com.game.protoGen.ProtoCommon;
import com.game.protoGen.ProtoTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class CommController implements AresController {
    @Autowired
    private Client client;
    @Autowired
    private PerformanceTestService performanceTestService;

    @MsgId(ProtoCommon.ProtoCode.LOGIN_RESPONSE_VALUE)
    public void userLoginResponse(ProtoTask.LoginResponse response) {
        log.info("------login response ={}", response);

        ProtoTask.PerformanceTestReq helloPerformance = ProtoTask.PerformanceTestReq.newBuilder()
                .setSomeBody("hello performance")
                .setSomeId(11111).build();
        AresPacket aresPacket = AresPacket.create(ProtoCommon.ProtoCode.PERFORMANCE_TEST_REQ_VALUE, helloPerformance);
        client.getChannel().writeAndFlush(aresPacket);

        //ProtoTask.D
        ProtoTask.DirectToWorldReq req = ProtoTask.DirectToWorldReq.newBuilder().setResBody("OOOOOOOOOOOOOOOOO").setSomeId(13223333).build();
        AresPacket directWorld = AresPacket.create(ProtoCommon.ProtoCode.DIRECT_TO_WORLD_REQ_VALUE, req);
        client.getChannel().writeAndFlush(directWorld);

     //   performanceTestService.startSend();
    }

    @MsgId(ProtoCommon.ProtoCode.PERFORMANCE_TEST_RES_VALUE)
    public void onGameResponse(ProtoTask.PerformanceTestRes res) {
        log.info("==============  PERFORMANCE_TEST_RES_VALUE  response ={} ", res);
    }

    @MsgId(ProtoCommon.ProtoCode.DIRECT_TO_WORLD_RES_VALUE)
    public void onWorldResponse(ProtoTask.DirectToWorldRes res) {
        log.info("==============  DIRECT_TO_WORLD_RES_VALUE response ={} ", res);
    }
}
