package server.web.service;

import org.springframework.stereotype.Service;

import server.web.entity.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-07-03 21:45
 * @description:
 **/
@Service
public class LogService {

    private static List<Log> loglist =  new CopyOnWriteArrayList<Log>();

    /**
     * 获取日志，按照授权码，端口，日期分类
     * @return
     */
    public List<Log> getLogs() {
        return loglist;
    }

    /**
     * 插入日志
     * @return
     */
    public boolean addLog(String clientKey,String clientName ,int port,double flow){
        Log log = new Log(clientKey, clientName, port, flow, new Date());
        loglist.add(log);
        return true;
    }

}
