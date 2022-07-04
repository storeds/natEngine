package server.web.service;

import org.springframework.stereotype.Service;
import server.web.entity.Admin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-07-03 21:28
 * @description: 管理员
 **/
@Service
public class AdminService {

    /**
     * 默认是 root  123456
     */
    private static List<Admin> admin =  new CopyOnWriteArrayList<Admin>(){{
        add(new Admin(1,"root","123456"));
    }};

    /**
     * 登录方法
     * @param username
     * @param password
     * @return
     */
    public boolean login(String username,String password){
        Admin admin1 = admin.get(0);
        if (admin1.getUsername().equals(username) && admin1.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

}
