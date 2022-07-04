package server.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-07-04 10:09
 * @description: 登录页跳转
 **/
@CrossOrigin
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(Model model, HttpServletResponse response) {
        return "forward:/natEngine/login";
    }

}
