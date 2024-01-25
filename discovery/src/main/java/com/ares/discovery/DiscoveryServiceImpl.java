package com.ares.discovery;

import com.ares.transport.bean.ServerNodeInfo;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.watch.WatchEvent;

import java.util.List;
import java.util.function.BiFunction;

public class DiscoveryServiceImpl implements DiscoveryService {
    private Client etcdClient;
    private EtcdRegister etcdRegister;
    private EtcdDiscovery etcdDiscovery;
    private BiFunction< WatchEvent.EventType, ServerNodeInfo, Void> onNodeChangeFun;

    public void init(String[] endpoints, String appName, int port,int areaId, List<String>watchServicePrefix, BiFunction< WatchEvent.EventType, ServerNodeInfo, Void> onNodeChangeFun) {
        etcdClient = Client.builder().endpoints(endpoints).build();
        etcdRegister = new EtcdRegister(etcdClient, appName, port, areaId);
        etcdRegister.startRegister();

        etcdDiscovery = new EtcdDiscovery(etcdClient,onNodeChangeFun);
        etcdDiscovery.watchService(watchServicePrefix);

    }

    @Override
    public Client getEtcdClient() {
        return etcdClient;
    }

    @Override
    public EtcdDiscovery getEtcdDiscovery() {
        return etcdDiscovery;
    }
}
