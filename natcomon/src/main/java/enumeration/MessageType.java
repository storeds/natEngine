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
@AllArgsConstructor
@Getter
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
    private final Integer type;

}
