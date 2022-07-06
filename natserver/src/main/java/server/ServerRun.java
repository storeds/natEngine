package server;

import codec.CommonDecoder;
import codec.CommonEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import server.config.ConfigParser;
import server.handler.HeartBeatHandler;
import server.handler.ServerHandler;

import java.util.concurrent.TimeUnit;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-19 10:39
 * @description: 代理服务器启动入口
 **/
@Slf4j
public class ServerRun {

    private static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;
    private static final int LENGTH_FIELD_OFFSET = 0;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 4;

    private static final int READER_IDLE_TIME = 40;
    private static final int WRITER_IDLE_TIME = 0;
    private static final int ALL_IDLE_TIME = 0;

    /**
     * 启动方法
     * @throws Exception
     */
    public void start()throws Exception{
        // 获取服务端ip 和服务端端口
        String serverHost = (String) ConfigParser.get("server-host");
        int serverPort  = (Integer) ConfigParser.get("server-port");

        // 添加启动器和连接器
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                // 添加心跳和粘包粘包处理器
                pipeline.addLast(new IdleStateHandler(READER_IDLE_TIME,WRITER_IDLE_TIME,ALL_IDLE_TIME, TimeUnit.SECONDS));
                pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET
                        ,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP));
                // 添加解码器和编码器
                pipeline.addLast(new CommonDecoder());
                pipeline.addLast(new CommonEncoder());
                // 添加客户端连接器和心跳处理器
                pipeline.addLast(new ServerHandler());
                pipeline.addLast(new HeartBeatHandler());
            }
        };

        // 创建服务端启动器
        ServerBootStrapHelper serverBootStrapHelper = new ServerBootStrapHelper();
        serverBootStrapHelper.bootStart(bossGroup,workerGroup,serverHost,serverPort,channelInitializer);

    }

}
