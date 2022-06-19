package server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 07:59
 * @description: 服务端心跳机制处理器
 **/
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    /** 读超时次数，超过指定次数表示客户端离线或者网络不稳定，主动断开客户端连接 **/
    private int waitCount = 1;
    private static final int MAX_WAIT_COUNT = 3;

    /** 心跳的时间 **/
    private static final long MAX_TIME_MILLIS_LIMIT = 60*1000;

    /** 上次的超时时间次数 重置 **/
    private long lastDisConnectTimeMillis = 0;

    /** 上次的超时时间 **/
    private long currentMillis = 0;


    /**
     * 出现超时会调用该方法，超时触发
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 获取当前的超时时间
        currentMillis = System.currentTimeMillis();

        // 两次超时时间间隔过长，重置超时次数与最后一次超时时间
        if ((currentMillis-lastDisConnectTimeMillis)>MAX_TIME_MILLIS_LIMIT) {
            if (waitCount == 1) {
                waitCount += 1;
                log.warn("{} -- 首次超时",this.getClass());
            } else {
                waitCount = 2;
                log.warn("{} -- 已重置超时次数与最后一次超时时间");
            }
            lastDisConnectTimeMillis = currentMillis;
        // 超时时间超过三次
        }else if (waitCount >= MAX_WAIT_COUNT){
            ctx.channel().close();
            log.error("{} -- 连续读取超时次数达到三次， 已主动断开与客户端的连接");
        }else {
            log.warn("{} -- 读取超时次数" , waitCount);
            waitCount += 1;
            lastDisConnectTimeMillis = currentMillis;
        }
    }


    /**
     * 连接异常，出现异常时直接关闭客户端
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        log.error("{} -- 客户端连接异常", this.getClass());
        cause.printStackTrace();
    }

}
