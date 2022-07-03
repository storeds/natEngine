package client.out;

import client.config.ConfigParser;
import client.heandler.ClientHandler;
import client.heandler.HeartBeatHandler;
import codec.CommonDecoder;
import codec.CommonEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-19 16:52
 * @description: 运行帮助类
 **/
public class ClientRunHelper {

    private static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;
    private static final int LENGTH_FIELD_OFFSET = 0;
    private static final int LENGTH_FIELD_LENGTH = 4;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 4;

    private static final int READER_IDLE_TIME = 0;
    private static final int WRITER_IDLE_TIME = 30;
    private static final int ALL_IDLE_TIME = 0;

    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public void start(){
        ClientBootStrapHelper clientBootStrapHelper = new ClientBootStrapHelper();
        String serverHost = (String) ConfigParser.get("server-host");
        int serverPort = (Integer) ConfigParser.get("server-port");

        ChannelInitializer channelInitializer = new ChannelInitializer() {

            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                pipeline.addLast(new IdleStateHandler(READER_IDLE_TIME,WRITER_IDLE_TIME,ALL_IDLE_TIME, TimeUnit.SECONDS));
                pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH,LENGTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP));
                pipeline.addLast(new CommonDecoder());
                pipeline.addLast(new CommonEncoder());
                pipeline.addLast(new ClientHandler());
                pipeline.addLast(new HeartBeatHandler(workerGroup));
            }
        };
        clientBootStrapHelper.start(workerGroup,channelInitializer,serverHost,serverPort);
    }



}
