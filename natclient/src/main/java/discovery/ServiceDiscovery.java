package discovery;

import java.net.InetSocketAddress;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 08:28
 * @description: 服务发现接口
 **/
public interface ServiceDiscovery {

    /**
     * 根据服务名称查找服务实体
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress lookupService(String serviceName);
}
