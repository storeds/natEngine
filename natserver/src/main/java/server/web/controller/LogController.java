package server.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.web.entity.Log;
import server.web.service.LogService;

import java.util.List;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-07-03 21:26
 * @description: 日志
 **/
@RestController
@RequestMapping("/api/log")
@CrossOrigin
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * 获取日志，按照授权码，端口，日期分类
     * @return
     */
    @RequestMapping("/get")
    public List<Log> getLogs(){
        return logService.getLogs();
    }
}
