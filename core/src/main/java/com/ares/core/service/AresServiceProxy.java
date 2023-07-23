package com.ares.core.service;


import com.ares.core.annotation.CalledMsgId;
import com.ares.core.bean.AresRpcMethod;
import com.ares.core.constdata.FConst;
import com.ares.core.exception.AresBaseException;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.protobuf.Parser;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

@Slf4j
public class AresServiceProxy {
    private final AresController aresController;
    private MethodAccess methodAccess;


    public AresServiceProxy(AresController aresController) {
        this.aresController = aresController;
    }

    public void init(IMsgCall iMsgCall) {
        Method[] methods = aresController.getClass().getDeclaredMethods();
        log.info("============ init  service ={}  begin", aresController.getClass().getSimpleName());
        methodAccess = MethodAccess.get(aresController.getClass());

        for (Method method : methods) {
            CalledMsgId calledMsgId = method.getDeclaredAnnotation(CalledMsgId.class);
            if (calledMsgId == null) {
                continue;
            }
            int msgId = calledMsgId.value();
            String methodName = method.getName();
            AresRpcMethod aresRpcMethod = new AresRpcMethod();
            if (!Modifier.isPublic(method.getModifiers())) {
                log.warn("method name ={} called msgId ={} is not public will be ignored", methodName, msgId);
                continue;
            }
            log.info("--------  ready to init methodName ={} callMsgId ={}", methodName, msgId);
            aresRpcMethod.setMethodIndex(methodAccess.getIndex(methodName));
            int paramCount = method.getParameterCount();
            if (paramCount != 1) {
                throw new AresBaseException(FConst.ERROR_CODE_PARMAS_COUNT_MORE_THAN_ONE, FConst.ERROR_MSG_PARMAS_COUNT_MORE_THAN_ONE);
            }
            Type paramsType = method.getGenericParameterTypes()[0];
            Class<?> paramClass = (Class<?>) paramsType;
            aresRpcMethod.setAresServiceProxy(this);
            Parser<?> parser = AresRpcMethod.pbParser(paramClass);
            aresRpcMethod.setParser(parser);
            iMsgCall.onMethodInit(msgId, aresRpcMethod);
        }
        log.info("============ init  service ={}  end", aresController.getClass().getSimpleName());
    }

    public Object callMethod(Object paramObj, AresRpcMethod aresRpcMethod) {
        return methodAccess.invoke(aresController, aresRpcMethod.getMethodIndex(), paramObj);
    }
}
