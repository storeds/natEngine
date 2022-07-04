package server.handler;

import enumeration.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import message.NatMessage;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 08:13
 * @description: 服务端接收外部请求
 **/
@Slf4j
public class RemoteHandler extends ChannelInboundHandlerAdapter {

//    private static RemoteHandler remoteHandler = new RemoteHandler();



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
        send(MessageType.TYPE_CONNECTED,ctx.channel().id().asLongText(),null);
        log.info("{}" + "端口有请求进入，channelId为：" +"{}",this.getClass(), ctx.channel().id().asLongText());
    }


    /**
     * 读取外部连接数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //从外部连接接收到的数据
        byte[] data = (byte[]) msg;
        //调用发送方法转发到客户端
        send(MessageType.TYPE_DATA,ctx.channel().id().asLongText(),data);
        log.info("{}" + "{}" +"端口收到请求数据，数据量为" +"{}" + "字节",this.getClass(), remotePort,data.length);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        send(MessageType.TYPE_DISCONNECTED,ctx.channel().id().asLongText(),null);
        log.info("{}" + "{}" + "端口有请求离开，channelId为：" +"{}" + "字节",this.getClass(),remotePort,ctx.channel().id().asLongText());
    }

    /**
     * 将数据发送到内网
     * @param type
     * @param channelId
     * @param data
     */
    public void send(MessageType type, String channelId, byte[] data) throws Exception {

        // 如果原先的serverHandler为空说明无法发送
        if (serverHandler == null){
            log.warn("{} -- 客户端的channel不存在", this.getClass());
            return;
        }

        // 创建发送数据的message
        NatMessage message = new NatMessage();
        message.setType(type.getType());
        HashMap<String,Object> metaData = new HashMap<>();

        message.setRequestId(getRandom());

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

//        // 记录日志，只记录数据传输的请求
//        if (remoteHandler != null && MessageType.TYPE_DATA.getType() == type.getType()) {
//            // TODO 添加登录日志
//
//        }

    }


    /** 计数器 **/
    private static volatile int  count = 0;
    private static int lastTime = 0;

    /** 返回唯一值 **/
    public int getRandom() throws Exception {
        // 根据时间来获取随机值
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        int concurrent = 0;
        // 通过生成当前值
        if (hour < 10) {
            concurrent = hour * 10000000 + minute + 100000 + second + 1000;
        }else {
            concurrent = hour * 1000000 + minute + 100000 + second + 1000;
        }
        if (lastTime > concurrent){
            throw new Exception("出现时钟回滚异常");
        }

        // 计数器++
        if (count == 999) {
            count = 0;
        }else {
            count++;
        }

        // 生成随机的
        int res = concurrent + count;
        return res;
    }

}
