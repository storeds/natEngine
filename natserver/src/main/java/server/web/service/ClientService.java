package server.web.service;

import org.springframework.stereotype.Service;

import server.web.entity.Client;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-07-03 21:35
 * @description: 客户服务端
 **/
@Service
public class ClientService {

    private static List<Client> clientlist =  new CopyOnWriteArrayList<Client>();


    /**
     * 添加客户端
     * @param client
     * @return
     */
    public boolean addClient(Client client){
        clientlist.add(client);
        return true;
    }

    /**
     * 删除客户端
     * @param id
     * @return
     */
    public boolean deleteClient(int id){
        clientlist.removeIf(client -> client.getId() == id);
        return true;
    }

    /**
     * 校验key
     * @param clientKey
     * @return
     */
    public boolean checkClientKey(String clientKey) {
        for (Client client : clientlist){
            if (client.getClientKey().equals(clientKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询所有用户
     * @return
     */
    public List<Client> getClients() {
        return clientlist;
    }
}
