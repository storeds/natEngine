package server.reposity;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 10:08
 * @description:  客户端存储信息
 **/
public interface ClientService {

    /**
     * 核查clientKey
     * @param clientKey  传输的clientKey
     * @return           返回校验结果
     */
    public boolean checkClientKey(String clientKey);

}
