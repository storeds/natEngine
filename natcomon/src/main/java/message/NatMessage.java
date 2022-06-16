package message;

import lombok.*;

import java.util.Map;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-15 20:48
 * @description:
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class NatMessage {

    /** 请求号 **/
    private Integer requestId;

    /** 消息类型 **/
    private Integer type;

    /** 元数据 **/
    private Map<String,Object> metaData;

    /** 消息内容 **/
    private byte[] data;


}
