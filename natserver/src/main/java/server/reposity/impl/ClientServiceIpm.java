package server.reposity.impl;

import server.reposity.ClientService;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 10:10
 * @description: 客户端存储信息
 **/
public class ClientServiceIpm implements ClientService {

    /** 确保安全的写时复制list存储授权码 **/
     private static final CopyOnWriteArrayList reposityList = new CopyOnWriteArrayList();



    /**
     * 检验
     * @param clientKey  传输的clientKey
     * @return
     */
    @Override
    public boolean checkClientKey(String clientKey) {
        // 判断是不是拥有这个授权
        boolean ret = reposityList.contains(clientKey);
        return ret;
    }
}
