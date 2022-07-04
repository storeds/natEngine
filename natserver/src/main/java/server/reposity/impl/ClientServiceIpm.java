package server.reposity.impl;

import lombok.extern.slf4j.Slf4j;
import server.reposity.ClientService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 10:10
 * @description: 客户端存储信息
 **/
@Slf4j
public class ClientServiceIpm implements ClientService {

    /** 确保安全的写时复制list存储授权码 **/
     public static List<Object> REPOSITY_LIST = new CopyOnWriteArrayList<>();



    /**
     * 检验
     * @param clientKey  传输的clientKey
     * @return
     */
    @Override
    public boolean checkClientKey(String clientKey) {
        boolean ret = false;
        // 判断是不是拥有这个授权
        for (Object objs : REPOSITY_LIST) {
            List<CopyOnWriteArrayList> objslist = (CopyOnWriteArrayList)objs;
            for (Object obj : objslist ){
                // TODO 后续这里需要解耦， clientKey写死了
                ret = clientKey.equals(getFieldValueByName("clientKey", obj));
                if (ret == true) {
                    return true ;
                }
            }
        }
        return ret;
    }



    /**
     * 获取属性值名称
     * @param o       对象
     * @return
     */
    private static String[] getFieldName(Object o){
        Field[] fields=o.getClass().getDeclaredFields();
        String[] fieldNames=new String[fields.length];
        for(int i=0;i<fields.length;i++){
            fieldNames[i]=fields[i].getName();
        }
        return fieldNames;
    }


    /**
     * 更具属性名获取属性值
     * @param fieldName 属性名
     * @param o         对象
     * @return
     */
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            log.error("根据属性名获取属性值异常："+e.getMessage(),e);
            return null;
        }
    }
}
