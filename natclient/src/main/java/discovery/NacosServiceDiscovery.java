package discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import enumeration.NatError;
import exception.NatException;
import loadbalancer.LoadBalancer;
import loadbalancer.RandomLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import utile.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 08:30
 * @description:
 **/
@Slf4j
public class NacosServiceDiscovery implements ServiceDiscovery {


    /** 使用负载均衡 **/
    private final LoadBalancer loadBalancer;

    /**
     * 构造对应的负载均衡函数
     * @param loadBalancer
     */
    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        // 默认使用随机负载均衡算法
        if(loadBalancer == null) this.loadBalancer = new RandomLoadBalancer();
        else this.loadBalancer = loadBalancer;
    }

    /**
     * 获取所有的实例
     * @param serviceName 服务名称
     * @return
     */
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            // 获取所有的实例
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            // 没有实例将会抛出异常
            if(instances.size() == 0) {
                log.error("找不到对应的服务: " + serviceName);
                throw new NatException(NatError.SERVICE_NOT_FOUND);
            }
            // 选择负载均衡的算法，获取服务
            Instance instance = loadBalancer.select(instances);

            // 返回服务
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }
}
