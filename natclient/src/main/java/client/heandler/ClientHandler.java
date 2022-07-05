package client.heandler;

import client.config.ConfigParser;
import client.out.ClientBootStrapHelper;

import enumeration.MessageType;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import message.NatMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-19 11:53
 * @description: 服务器连接处理器
 **/
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    /** 全局管理channels 和存储端口的map **/
    private ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private ConcurrentHashMap<Integer,Integer> portMap = new ConcurrentHashMap<>();

    /** 所有localChannel共享，减少线程上下文切换  每个外部请求channelId与其处理器handler的映射关系 **/
    private EventLoopGroup localGroup = new NioEventLoopGroup();
    private ConcurrentHashMap<String,LocalHandler> localHandlerMap = new ConcurrentHashMap<>();

    /** chanel上下文返回上下文 **/
    private ChannelHandlerContext ctx = null;
    public ChannelHandlerContext getCtx() {
        return ctx;
    }


    /**
     * 建立连接初始化 激活
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        // 创建消息 添加消息类型
        NatMessage message = new NatMessage();
        message.setRequestId(getRandom());
        // 注册
        message.setType(MessageType.TYPE_REGISTER.getType());
        // 获取授权码
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("clientKey", ConfigParser.get("client-key"));
        // 添加端口映射
        ArrayList<Integer> serverPortArr = new ArrayList<>();
        for (Map<String,Object> item : ConfigParser.getPortArray()){
            serverPortArr.add((Integer) item.get("server-port"));
            //保存端口映射关系
            portMap.put((Integer) item.get("server-port"),(Integer) item.get("client-port"));
        }

        // 添加元数据
        metaData.put("ports",serverPortArr);
        message.setMetaData(metaData);
        ctx.writeAndFlush(message);
        log.info("{} -- 与服务器连接建立成功，正在进行注册...", this.getClass());
    }

    /** 计数器 **/
    private static volatile int  count = 0;
    private static volatile int lastTime = 0;

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
        lastTime = concurrent;

        // 计数器++
        if (count == 999) {
            count = 0;
        }else {
            count = count + 1;
        }

        // 生成随机的
        int res = concurrent + count;
        return res;
    }

    /**
     * 读取数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 内网穿透的消息
        NatMessage message = (NatMessage) msg;

        // TODO 后续优化为策略模式
        // 判断消息的类型
        switch (Objects.requireNonNull(MessageType.getByValue(message.getType()))){
            // 授权
            case TYPE_AUTH :
                processAuth(message);
                break;
            // 外部请求进入，开始与内网建立连接
            case TYPE_CONNECTED :
                processConnected(message);
                break;
            // 断开连接
            case TYPE_DISCONNECTED :
                processDisConnected(message);
                break;
            // 心跳请求
            case TYPE_KEEPALIVE :
                //心跳，不做处理
                break;
            // 数据传输
            case TYPE_DATA :
                processData(message);
                break;
            default:
                throw new Exception("未知异常消息类型");
        }
    }


    /**
     * 连接中断 断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.close();
        localGroup.shutdownGracefully();
        log.info("{} -- 与服务器连接断开", this.getClass());
    }


    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} -- 连接异常", this.getClass());
        cause.printStackTrace();
        //传递异常
        ctx.fireExceptionCaught(cause);
        ctx.channel().close();
    }

    /**
     * 授权结果处理
     * @param message
     */
    public void processAuth(NatMessage message){
        if ((Boolean) message.getMetaData().get("isSuccess")){
            log.info("{} -- 注册成功", this.getClass());
        }else {
            ctx.fireExceptionCaught(new Throwable());
            ctx.channel().close();
            log.error("{}注册失败，原因： -- {}", this.getClass(), message.getMetaData().get("reason"));
        }
    }


    /**
     * 服务器通知客户端与本地服务建立连接
     * @param message
     */
    public void processConnected(NatMessage message){
        // 创建客户端处理
        ClientHandler clientHandler = this;
        ClientBootStrapHelper localHelper = new ClientBootStrapHelper();

        ChannelInitializer channelInitializer = new ChannelInitializer() {

            @Override
            protected void initChannel(Channel channel) throws Exception {

                LocalHandler localHandler = new LocalHandler(clientHandler,message.getMetaData().get("channelId").toString());
                ChannelPipeline pipeline = channel.pipeline();

                pipeline.addLast(new ByteArrayEncoder());
                pipeline.addLast(new ByteArrayDecoder());
                pipeline.addLast(localHandler);

                channels.add(channel);
                localHandlerMap.put(message.getMetaData().get("channelId").toString(),localHandler);
            }
        };

        String localhost = (String) ConfigParser.get("local-host");
        //这里根据portMap将远程服务器端口作为key获取对应的本地端口
        int remotePort = (Integer) message.getMetaData().get("remotePort");
        int localPort = portMap.get(remotePort);
        localHelper.start(localGroup,channelInitializer,localhost,localPort);
        log.info("{} 服务器 -- {} 端口进入连接，正在向本地 -- {}端口建立连接",this.getClass() ,remotePort ,localPort);
    }

    /**
     * 处理外部请求与代理服务器断开连接通知
     * @param message
     */
    public void processDisConnected(NatMessage message){
        // 获取channelId
        String channelId = message.getMetaData().get("channelId").toString();
        LocalHandler handler = localHandlerMap.get(channelId);
        // 无就移除
        if (handler!=null){
            handler.getLocalCtx().close();
            localHandlerMap.remove(channelId);
        }
    }


    /**
     * 处理服务器传输的请求数据
     * @param message
     */
    public void processData(NatMessage message){
        if (message.getData()==null || message.getData().length<=0){
            return;
        }
        String channelId = message.getMetaData().get("channelId").toString();
        LocalHandler localHandler = localHandlerMap.get(channelId);
        if (localHandler!=null){
            //将数据转发到对应内网服务器
            localHandler.getLocalCtx().writeAndFlush(message.getData());
        }
        log.info("{} -- 收到服务器数据，数据量为 {} 字节",this.getClass() ,message.getData().length);
    }

}
