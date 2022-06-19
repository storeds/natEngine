package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-15 20:50
 * @description: 消息状态
 **/

public enum MessageType {

    /** 注册 **/
    TYPE_REGISTER(1),
    /** 授权 **/
    TYPE_AUTH(2),
    /** 建立连接 **/
    TYPE_CONNECTED(3),
    /** 断开连接 **/
    TYPE_DISCONNECTED(4),
    /** 心跳 **/
    TYPE_KEEPALIVE(5),
    /** 数据传输 **/
    TYPE_DATA(6);
    private  Integer type;

    private MessageType(Integer type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }

    /**
     * 提前判断，用于解决
     * Case中出现的Constant expression required
     * @param value
     * @return
     */
    public static MessageType getByValue(int value){
        for(MessageType x : values()){
            if(x.getType() == value){
                return x;
            }
        }
        return null;
    }

}
