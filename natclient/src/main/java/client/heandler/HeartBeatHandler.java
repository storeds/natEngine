package client.heandler;

import enumeration.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-19 16:51
 * @description: 客户端心跳机制处理器
 **/
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    /** 添加工作组 **/
    private EventLoopGroup workerGroup = null;

    public HeartBeatHandler() {
    }

    public HeartBeatHandler(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    /** 设置心跳机制 **/
    private static final  Integer HEART_BEAT = MessageType.TYPE_KEEPALIVE.getType();

    /**
     * 写超时事件发生会使用会调用这个方法
     * @param ctx      上下文
     * @param evt      事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.warn("{} -- 写超时，发送心跳", this.getClass());
        ctx.writeAndFlush(HEART_BEAT);
    }

    /**
     * 异常处理，发生异常时直接关闭服务器连接(比如发送心跳失败，可能服务器下线了)
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("{} -- 连接异常，已中断 ",this.getClass());
        workerGroup.shutdownGracefully();
        ctx.fireExceptionCaught(cause);
        ctx.channel().close();
    }


}
