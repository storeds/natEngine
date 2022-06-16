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

    private static Map<String,Object> config = null;
    private static ArrayList<Map<String,Object>> portArr = null;

    public ConfigParser() {
        try {
            //定位当前文件夹路径(zrp)
            String dir = System.getProperty("user.dir");
            // 获取配置
            File file = new File(dir+File.separator+"nat-client.yaml");
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
            new ConfigParser();
        }
        return config.get(key);
    }

    public static ArrayList<Map<String,Object>> getPortArray(){
        if (portArr==null){
            new ConfigParser();
        }
        return portArr;
    }

}
