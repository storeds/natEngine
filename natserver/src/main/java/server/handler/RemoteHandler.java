package server.handler;

import enumeration.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import message.NatMessage;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 08:13
 * @description: 服务端接收外部请求
 **/
@Slf4j
public class RemoteHandler extends ChannelInboundHandlerAdapter {

    private static RemoteHandler remoteHandler;

    @PostConstruct
    public void init(){
        remoteHandler = this;
    }

    /** 服务处理handler   远程的端口   客户端的授权码 **/
    private ServerHandler serverHandler = null;
    private int remotePort;
    private String clientKey;

    /**
     * 添加值
     * @param serverHandler
     * @param remotePort
     * @param clientKey
     */
    public void setValue(ServerHandler serverHandler, int remotePort, String clientKey) {
        this.serverHandler = serverHandler;
        this.remotePort = remotePort;
        this.clientKey = clientKey;
    }

    /**
     * 连接初始化建立连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 将数据发送到内网
     * @param type
     * @param channelId
     * @param data
     */
    public void send(int type, String channelId, byte[] data) {

        // 如果原先的serverHandler为空说明无法发送
        if (serverHandler == null){
            log.warn("{} -- 客户端的channel不存在", this.getClass());
            return;
        }

        // 创建发送数据的message
        NatMessage message = new NatMessage();
        message.setType(type);
        HashMap<String,Object> metaData = new HashMap<>();

        // channelId存储channelId， 和远程端口
        metaData.put("channelId",channelId);
        metaData.put("remotePort",remotePort);
        message.setMetaData(metaData);

        // 数据不为空将数据添加进去
        if (data!=null){
            message.setData(data);
        }

        // 将数据发送出去
        this.serverHandler.getCtx().writeAndFlush(message);

        // 记录日志，只记录数据传输的请求
        if (remoteHandler != null && MessageType.TYPE_DATA.getType() == type) {
            // TODO 添加登录日志

        }

    }

}
