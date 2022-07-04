package server.config;

import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-07-02 19:41
 * @description: 配置解析器
 **/
public class ConfigParser {

    private static Map<String,Object> config = null;
    private static ArrayList<Map<String,Object>> portArr = null;

    public ConfigParser() {
        try {
            //IDEA中运行
            String dir = System.getProperty("user.dir");
            // 获取配置
            File file = new File(dir+File.separator+"server.yaml");
            InputStream in = new FileInputStream(file);
            Yaml yaml = new Yaml();
            config = (Map<String,Object>) yaml.load(in);
            portArr = (ArrayList<Map<String, Object>>) get("config");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 在IDE启动获取配置文件
     * @return
     * @throws Exception
     */
    public File getProjectConfigFile() throws Exception{
        return ResourceUtils.getFile("classpath:config/server.yaml");
    }

    /**
     * 服务器中启动获取配置文件
     * @return
     */
    public File getServerConfigFile() {
        String classPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        classPath = classPath.substring(5,classPath.indexOf("server.jar"))+"server.yaml";
        return new File(classPath);
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
