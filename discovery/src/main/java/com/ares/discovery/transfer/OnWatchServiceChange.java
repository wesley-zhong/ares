package com.ares.discovery.transfer;

import com.ares.transport.bean.ServerNodeInfo;
import io.etcd.jetcd.watch.WatchEvent;

public interface OnWatchServiceChange {
     Void onWatchServiceChange(WatchEvent.EventType eventType, ServerNodeInfo serverNodeInfo);
}
