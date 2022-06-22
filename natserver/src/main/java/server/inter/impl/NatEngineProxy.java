package server.inter.impl;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-21 21:23
 * @description:  创建代理server
 **/
@Slf4j
public class NatEngineProxy  implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        log.info("启动后端服务");


        return null;
    }
}
