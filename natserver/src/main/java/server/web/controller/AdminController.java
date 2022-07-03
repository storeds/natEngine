package server.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.web.service.AdminService;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-07-03 21:21
 * @description:
 **/
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     * @param username
     * @param password
     * @param request
     * @return
     */
    @RequestMapping("/login")
    public boolean login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request){
        boolean flag = adminService.login(username,password);
        if (flag){
            request.getSession().setAttribute("user",username);
        }
        return flag;
    }


    /**
     * 检查登录状态
     * @param request
     * @return
     */
    @RequestMapping("/isLogin")
    public String isLogin(HttpServletRequest request){
        Object user = request.getSession().getAttribute("user");
        if (user!=null){
            return (String) user;
        }
        return "null";
    }

}
