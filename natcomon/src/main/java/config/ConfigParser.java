package config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.util.ResourceUtils;
/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-15 21:42
 * @description: 配置文件解析器
 **/
public class ConfigParser {

    /** 存放配置  端口   和监听类型 **/
    private static Map<String,Object> config = null;
    private static ArrayList<Map<String,Object>> portArr = null;
    private static String key = "";

    public ConfigParser(String key) {
        try {
            ConfigParser.key = key;
            File file = null;
            // 获取服务端配置
            if (key.equals("server-host")) {
                //定位当前文件夹路径
                String dir = System.getProperty("user.dir");
                // 获取配置
                file = new File(dir+File.separator+"server.yaml");
                // 获取客户端配置
            }else {
                //定位当前文件夹路径
                String dir = System.getProperty("user.dir");
                // 获取配置
                file = new File(dir+File.separator+"client.yaml");
            }


            InputStream in = new FileInputStream(file);
            Yaml yaml = new Yaml();
            config = (Map<String,Object>) yaml.load(in);
            portArr = (ArrayList<Map<String, Object>>) get("config");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public File getProjectConfigFile() throws Exception{
        return ResourceUtils.getFile("classpath:config/nat-server.yaml");
    }

    public static Object get(String key){
        if (config==null){
            new ConfigParser(key);
        }
        return config.get(key);
    }

    public static ArrayList<Map<String,Object>> getPortArray(){
        // 默认服务端获取端口
        if (portArr==null){
            new ConfigParser("server-host");
        }
        return portArr;
    }

}
