package client.loadbalancer;



import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 08:24
 * @description: 负载均衡接口
 **/
public interface LoadBalancer {

    Instance select(List<Instance> instances);

}
