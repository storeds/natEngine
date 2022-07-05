package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-19 10:37
 * @description: 代理服务器启动器帮助类
 **/
@Slf4j
public class ServerBootStrapHelper {

    /**
     * 启动监听
     * @param bossGroup           连接处理
     * @param workerGroup         事件处理
     * @param serverHost          服务端ip
     * @param serverPort          服务端端口
     * @param channelInitializer  初始化
     * @throws Exception
     */
    public synchronized void bootStart(EventLoopGroup bossGroup, EventLoopGroup workerGroup,
                                       String serverHost, int serverPort, ChannelInitializer channelInitializer) throws Exception{
        try {
            ServerBootstrap serverBootstrap = new  ServerBootstrap();

            // 添加处理和channel
            serverBootstrap.group(bossGroup,workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);

            // 添加初始化
            serverBootstrap.childHandler(channelInitializer);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);

            // 获取chanel进行处理
            Channel channel = serverBootstrap.bind(serverHost,serverPort).sync().channel();
        }catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.error("{} -- bootStart()中出现错误", this.getClass());
            e.printStackTrace();
        }
    }

}
