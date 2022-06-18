package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 08:14
 * @description: 服务端处理客户端请求
 **/
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static ServerHandler serverHandler;


    /**
     * 在spring加载过程中赋值
     */
    @PostConstruct
    public void init(){
        serverHandler = this;
    }


    /** 客户端的连接池 **/
    private static ConcurrentHashMap<String,Integer> clients = new ConcurrentHashMap<>();

    /** 用于管理channel 和 remote channel **/
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    /** bossGroup处理端口连接线程 ， workerGroup处理连接信息 **/
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    /** 客户端标识 **/
    private String clientKey;

    /** 代理客户端上下文 **/
    private ChannelHandlerContext ctx;

    /** 判断是不是已注册 **/
    private boolean isRegister = false;

    /**
     * 获取上下文信息
     * @return
     */
    public ChannelHandlerContext getCtx() {
        return ctx;
    }


    /**
     * 连接时触发下面的方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        log.info("{}--有客户端建立连接，客户端地址为:{}", this.getClass(), ctx.channel().remoteAddress());
    }

}
