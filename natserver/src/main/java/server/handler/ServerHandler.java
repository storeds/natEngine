package server.handler;


import enumeration.MessageType;
import enumeration.NatError;
import exception.NatException;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import message.NatMessage;
import org.apache.http.nio.protocol.Pipelined;
import org.springframework.stereotype.Component;
import server.ServerBootStrapHelper;
import server.config.ConfigParser;
import server.reposity.ClientService;
import server.reposity.impl.ClientServiceIpm;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static enumeration.MessageType.TYPE_DISCONNECTED;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 08:14
 * @description: 服务端处理客户端请求
 **/
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /** 创建一个自己来使用 **/
    private static ServerHandler serverHandler = new ServerHandler();

    /** 客户端的连接池 **/
    private static ConcurrentHashMap<String,Integer> clients = new ConcurrentHashMap<>();

    /** 用于管理channel 和 remote channel **/
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /** bossGroup处理端口连接线程 ， workerGroup实际的处理器 **/
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    /** 客户端标识 **/
    private String clientKey;

    /** 代理客户端上下文 **/
    private ChannelHandlerContext ctx;

    /** 判断是不是已注册 **/
    private boolean isRegister = false;

    /** 客户端中心 **/
    private ClientService clientService = new ClientServiceIpm();

    /** 服务端启动器 **/
    private ServerBootStrapHelper remoteHelper = new ServerBootStrapHelper();


    /**
     * 获取上下文信息
     * @return
     */
    public ChannelHandlerContext getCtx() {
        return ctx;
    }


    /**
     * 连接时触发下面的方法 激活
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        log.info("{}--有客户端建立连接，客户端地址为:{}", this.getClass(), ctx.channel().remoteAddress());
    }

    /**
     * 数据读取和转发
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
        if (message.getType().equals(MessageType.TYPE_REGISTER.getType())) {
            // 客户端进行注册
            processRegister(message);
        }else if ( isRegister ) {
            // 客户端请求信息
            switch (Objects.requireNonNull(MessageType.getByValue(message.getType()))){
                // 客户端请求断开连接
                case TYPE_DISCONNECTED :
                    processDisconnect(message);
                    break;
                // 心跳，不做处理
                case TYPE_KEEPALIVE :
                    break;
                // 处理数据
                case TYPE_DATA :
                    processData(message);
                    break;
                default:
                    log.warn("非法请求");
            }
        } else {
            log.warn("{} -- 有未授权的客户端尝试发送消息，断开连接", this.getClass());
            ctx.close();
        }

    }

    /**
     *  连接中断
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 移除通信channel
        channels.remove(ctx.channel());
        ctx.channel().close();

        // 移除断开客户端的授权码
        clients.remove(clientKey);

        // 取消正在监听的端口，否则第二次连接时无法再次绑定端口
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.info("{} 客户端连接中断 {}", this.getClass(), ctx.channel().remoteAddress());
    }

    /**
     * 连接异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 关闭通道
        ctx.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

        // 打印异常日志
        log.warn("{} -- 连接异常，已中断: {}",this.getClass() ,ctx.channel().remoteAddress());
        cause.printStackTrace();
    }





    /**
     * 处理客户端注册请求
     * @param message
     * @throws Exception
     */
    public void processRegister(NatMessage message) throws Exception{

        // 定义元数据存放和当前serverHandler
        HashMap<String,Object> metaData = new HashMap<>();
        ServerHandler serverHandler = this;

        // 客户端标识
        String clientKey = message.getMetaData().get("clientKey").toString();

        // 检验客户端是否合法
        if (isLegal(clientKey)){
            // 获取配置的host
            String host = (String) ConfigParser.get("server-host");
            ArrayList<Integer> ports = (ArrayList<Integer>)  message.getMetaData().get("ports");
            try {

                // 循环遍历端口并添加
                for (int port : ports) {

                    ChannelInitializer channelInitializer = new ChannelInitializer() {

                        @Override
                        protected void initChannel(Channel channel) throws Exception {

                            // 创建远程处理的handler，用于数据的处理
                            RemoteHandler remoteHandler = new RemoteHandler();
                            remoteHandler.setValue(serverHandler, port, clientKey);
                            ChannelPipeline pipeline = channel.pipeline();

                            // 添加handler 解码器 编码器 远程连接handler
                            pipeline.addLast(new ByteArrayDecoder());
                            pipeline.addLast(new ByteArrayEncoder());
                            pipeline.addLast(remoteHandler);

                            // 添加注册的channel
                            channels.add(channel);
                        }
                    };
                    remoteHelper.bootStart(bossGroup,workerGroup,host,port,channelInitializer);
                }

                metaData.put("isSuccess",true);
                isRegister = true;
                log.info("{} -- 客户端注册成功，clientKey为{}", this.getClass(),  clientKey);
            }catch (Exception e) {
                // 向元数据中添加数据
                metaData.put("isSuccess",false);
                metaData.put("reason",e.getMessage());
                log.error("{} -- 客户端注册服务失败", clientKey);
                throw new NatException(NatError.CLIENT_REGISTER_SERVICE_FAILED, e);
            }
        } else {
            metaData.put("isSuccess",false);
            metaData.put("reason","client-key不合法，请两分钟后重试");
            log.error("{} -- 客户端注册不合法", clientKey);
            // TODO 这里抛异常需要处理，主要是能让其返回给客户端
            throw new NatException(NatError.CLIENT_REGISTER_ILLEGAL);
        }
        // 定义返回数据和获取对象
        NatMessage res = new NatMessage();
        MessageType type = MessageType.TYPE_AUTH;

        // 添加返回数据
        res.setType(type.getType());
        res.setMetaData(metaData);
        res.setRequestId(getRandom());
        ctx.writeAndFlush(res);
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

    /**
     * 判断客户端是否有授权
     * @param clientKey
     * @return
     */
    public synchronized boolean isLegal(String clientKey){
        // 返回的结果
        boolean flag = serverHandler.clientService.checkClientKey(clientKey);

        if (flag) {
            // 判断是不是唯一的客户端
            if (isExist(clientKey)){
                log.error("不允许同一授权码重复登录");
                return false;
            }
            // 检测成功后将其put进去
            clients.put(clientKey, 1);
            this.clientKey = clientKey;
            return true;
        }
        return false;
    }


    /**
     * 判断授权码是不是已经存在连接
     * @param clientKey
     * @return
     */
    public boolean isExist(String clientKey){
        if (clients.get(clientKey)!=null){
            return true;
        }
        return false;
    }

    /**
     * 处理客户端断开请求
     * @param message
     */
    public void processDisconnect(NatMessage message){
        channels.close( new ChannelMatcher() {
            @Override
            public boolean matches(Channel channel) {
                return false;
            }
        });
        log.info("{} -- 有客户端请求断开，clientKey为 {} ",this.getClass(), clientKey );
    }


    /**
     * 处理客户端发送的数据
     * @param message
     */
    public void processData(NatMessage message){
        // 判断发送的消息是不是有值
        if (message.getData()==null || message.getData().length<=0
                || !isExist(message.getMetaData().get("clientKey").toString())
        ){
            return;
        }
       // 根据channelId转发到channelGroup上注册的相应remote channel(外部请求)
        channels.writeAndFlush(message.getData(),new ChannelMatcher() {
            @Override
            public boolean matches(Channel channel) {
                if (channel.id().asLongText().equals(message.getMetaData().get("channelId"))){
                    // TODO 后续添加日志
                    String clientKey = ((Integer) message.getMetaData().get("clientKey")).toString();
                    return true;
                }
                return false;
            }
        });
        log.info("{} --收到客户端返回数据，数据量为 {} --字节",this.getClass(),  message.getData().length);
    }

}
