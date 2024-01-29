package com.ares.discovery;

import com.ares.discovery.utils.NetUtils;
import com.ares.core.utils.JsonUtil;
import com.ares.transport.bean.ServerNodeInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.CallStreamObserver;

import java.util.Map;

import static com.google.common.base.Charsets.UTF_8;

public class EtcdRegister {
    private final Client client;
    private final String appName;
    private final  int port;
    private final  int areaId;
    private ServerNodeInfo serverNodeInfo;
    public EtcdRegister(Client client, String appName, int port, int areaId) {
       this.client = client;
       this.appName = appName;
       this.port = port;
       this.areaId = areaId;
    }
    public  void startRegister(){
        serverNodeInfo  = new ServerNodeInfo();
        String addr = NetUtils.getIpAddress().get(0);
        String serviceId = NetUtils.createServiceId(appName,addr, port, areaId);

        serverNodeInfo.setIp(addr);
        serverNodeInfo.setPort(port);
        serverNodeInfo.setServiceId(serviceId);
        serverNodeInfo.setAreaId(areaId);
        serverNodeInfo.setServiceName(appName);

        putWithLease(serviceId, JsonUtil.toJsonString(serverNodeInfo));
    }
    public ServerNodeInfo getMyNodeInfo(){
        return serverNodeInfo;
    }

    public void updateServerNodeInfo(ServerNodeInfo serverNodeInfo){
        putWithLease(serverNodeInfo.getServiceId(), JsonUtil.toJsonString(serverNodeInfo));
    }
    public void updateServerNodeInfo(Map<String, String> metadata){
        serverNodeInfo.getMetaData().putAll(metadata);
        updateServerNodeInfo(serverNodeInfo);

    }

    /**
     * 将字符串转为客户端所需的ByteSequence实例
     * @param val
     * @return
     */
    public static ByteSequence bytesOf(String val) {
        return ByteSequence.from(val, UTF_8);
    }

    private Client getClient() {
        return client;
    }

    private void putWithLease(String key, String value) {
        Lease leaseClient = getClient().getLeaseClient();

        leaseClient.grant(10).thenAccept(result -> {
            // 租约ID
            long leaseId = result.getID();

            // 准备好put操作的client
            KV kvClient = getClient().getKVClient();

            // put操作时的可选项，在这里指定租约ID
            PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();

            // put操作
            kvClient.put(bytesOf(key), bytesOf(value), putOption)
                    .thenAccept(putResponse -> {
                        // put操作完成后，再设置无限续租的操作
                        leaseClient.keepAlive(leaseId, new CallStreamObserver<LeaseKeepAliveResponse>() {
                            @Override
                            public boolean isReady() {
                                return false;
                            }

                            @Override
                            public void setOnReadyHandler(Runnable onReadyHandler) {

                            }

                            @Override
                            public void disableAutoInboundFlowControl() {

                            }

                            @Override
                            public void request(int count) {
                            }

                            @Override
                            public void setMessageCompression(boolean enable) {

                            }

                            /**
                             * 每次续租操作完成后，该方法都会被调用
                             * @param value
                             */
                            @Override
                            public void onNext(LeaseKeepAliveResponse value) {
                               // System.out.println("续租完成");
                            }

                            @Override
                            public void onError(Throwable t) {
                                System.out.println(t);
                            }

                            @Override
                            public void onCompleted() {
                            }
                        });
                    });
        });
    }
}
