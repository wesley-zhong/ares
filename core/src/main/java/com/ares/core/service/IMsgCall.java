package com.ares.core.service;

import com.ares.core.bean.AresRpcMethod;

public interface IMsgCall {
    void onMethodInit(int msgId, AresRpcMethod aresRpcMethod);

    AresRpcMethod getCalledMethod(int msgId);
}
