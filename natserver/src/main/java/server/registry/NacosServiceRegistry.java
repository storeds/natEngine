package server.registry;

import com.alibaba.nacos.api.exception.NacosException;
import enumeration.NatError;
import exception.NatException;
import lombok.extern.slf4j.Slf4j;
import utile.NacosUtil;

import java.net.InetSocketAddress;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 08:36
 * @description: Nacos服务注册中心注册服务
 **/
@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {

    /**
     * 进行服务注册
     * @param serviceName 服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            log.error("注册服务时有错误发生:", e);
            throw new NatException(NatError.REGISTER_SERVICE_FAILED);
        }
    }

}
