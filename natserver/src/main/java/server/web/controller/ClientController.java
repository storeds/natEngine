package server.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import server.web.entity.Client;
import server.web.service.ClientService;

import java.util.Date;
import java.util.List;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-07-03 21:24
 * @description:
 **/
@RestController
@RequestMapping("/api/client")
@CrossOrigin
public class ClientController {

    @Autowired
    private ClientService clientService;

    /**
     * 添加客户端
     * @param client
     * @return
     */
    @PostMapping("/add")
    public boolean addClient(@RequestBody Client client){
        client.setStatus("已授权");
        client.setStartTime(new Date());
        return clientService.addClient(client);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public boolean delete(@RequestParam("clientId") int id){
        return clientService.deleteClient(id);
    }

    /**
     * 查询所有的用户
     * @return
     */
    @RequestMapping("/get")
    public List<Client> getClients(){
        return clientService.getClients();
    }



}
