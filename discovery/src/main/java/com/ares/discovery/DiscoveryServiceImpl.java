package com.ares.discovery;

import com.ares.transport.bean.ServerNodeInfo;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;
import java.util.function.BiFunction;

@Slf4j
public class DiscoveryServiceImpl implements DiscoveryService, ApplicationRunner {
    private Client etcdClient;
    private EtcdRegister etcdRegister;
    private EtcdDiscovery etcdDiscovery;
    private BiFunction<WatchEvent.EventType, ServerNodeInfo, Void> onNodeChangeFun;
    private List<String> watchServicePrefix;

    public void init(String[] endpoints, String appName, int port, int areaId, List<String> watchServicePrefix, BiFunction<WatchEvent.EventType, ServerNodeInfo, Void> onNodeChangeFun) {
        etcdClient = Client.builder().endpoints(endpoints).build();
        etcdRegister = new EtcdRegister(etcdClient, appName, port, areaId);
        etcdDiscovery = new EtcdDiscovery(etcdClient, onNodeChangeFun);
        this.watchServicePrefix = watchServicePrefix;
    }

    @Override
    public Client getEtcdClient() {
        return etcdClient;
    }

    @Override
    public EtcdDiscovery getEtcdDiscovery() {
        return etcdDiscovery;
    }

    @Override
    public EtcdRegister getEtcdRegister() {
        return etcdRegister;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        etcdRegister.startRegister();
        etcdDiscovery.watchService(watchServicePrefix);
    }
}
