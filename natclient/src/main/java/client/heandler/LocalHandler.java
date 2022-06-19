package client.heandler;

import config.ConfigParser;
import enumeration.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import message.NatMessage;

import java.util.HashMap;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-19 16:52
 * @description: 实际内网服务器channel连接处理器
 **/
@Slf4j
public class LocalHandler extends ChannelInboundHandlerAdapter {

    private ClientHandler clientHandler = null;
    private String remoteChannelId = null;
    private ChannelHandlerContext localCtx;


    public LocalHandler(ClientHandler clientHandler, String channelId) {
        this.clientHandler = clientHandler;
        this.remoteChannelId = channelId;
    }

    public ChannelHandlerContext getLocalCtx() {
        return localCtx;
    }

    /**
     * 连接建立初始化
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.localCtx = ctx;
        log.info("{} -- {} 与本地端口建立连接成功", this.getClass(), ctx.channel().remoteAddress());
    }

    /**
     * 读取内网服务器请求和数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 添加传输数据
        byte[] data = (byte[]) msg;
        NatMessage message = new NatMessage();
        message.setType(MessageType.TYPE_DATA.getType());

        // 创建元数据
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("channelId",remoteChannelId);
        metaData.put("clientKey", ConfigParser.get("client-key"));

        message.setMetaData(metaData);
        message.setData(data);

        // 收到内网服务器响应后返回给服务端
        this.clientHandler.getCtx().writeAndFlush(message);
        log.info("{} 收到本地 --  {} 的数据，数据量为 {} 字节" ,this.getClass(), ctx.channel().remoteAddress(), data.length);
    }



    /**
     * 连接异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        log.error("{} -- 连接中断",this.getClass());
        cause.printStackTrace();
    }

    /**
     * 连接断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 添加消息类型
        NatMessage message = new NatMessage();
        message.setType(MessageType.TYPE_DISCONNECTED.getType());
        // 添加元数据
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("channelId",remoteChannelId);
        message.setMetaData(metaData);
        // 返回数据
        this.clientHandler.getCtx().writeAndFlush(message);
        log.info("{} -- 与本地连接断开： {}", this.getClass(), ctx.channel().remoteAddress());
    }

}