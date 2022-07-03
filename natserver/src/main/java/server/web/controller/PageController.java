package server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-07-03 21:25
 * @description:
 **/
@Controller
@RequestMapping("/natEngine")
public class PageController {

    @RequestMapping("/index")
    public String goIndex(){
        return "index";
    }

    @RequestMapping("/admin")
    public String goAdmin(){
        return "admin";
    }

    @RequestMapping("/client")
    public String goClient(){
        return "client";
    }

    @RequestMapping("/log")
    public String goLog(){
        return "log";
    }

    @RequestMapping("/login")
    public String goLogin(){
        return "login";
    }

}
