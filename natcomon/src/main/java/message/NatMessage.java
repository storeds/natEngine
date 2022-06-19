package message;

import enumeration.MessageType;
import lombok.*;

import java.util.Map;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-15 20:48
 * @description:
 **/
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

    public NatMessage() {
    }

    public NatMessage(Integer requestId) {
        this.requestId = requestId;
    }

    public NatMessage(Integer requestId, Integer type) {
        this.requestId = requestId;
        this.type = type;
    }

    public NatMessage(Integer requestId, Integer type, Map<String, Object> metaData) {
        this.requestId = requestId;
        this.type = type;
        this.metaData = metaData;
    }

    public NatMessage(Integer requestId, Integer type, Map<String, Object> metaData, byte[] data) {
        this.requestId = requestId;
        this.type = type;
        this.metaData = metaData;
        this.data = data;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
