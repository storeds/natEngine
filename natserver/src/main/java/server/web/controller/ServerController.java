package server.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import server.ServerRun;

import javax.annotation.PostConstruct;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-07-03 21:26
 * @description:
 **/
public class ServerController {

    @Autowired
    private ServerRun serverRun;

    private static ServerController serverController;

    @PostConstruct
    public void init(){
        serverController = this;
        serverController.serverRun = this.serverRun;
        System.out.println("初始化完成");
        try {
            System.out.println("natEngine启动");
            serverController.serverRun.start();
        }catch (Exception e){
            System.out.println("natEngine启动失败");
            e.printStackTrace();
        }
    }

}
