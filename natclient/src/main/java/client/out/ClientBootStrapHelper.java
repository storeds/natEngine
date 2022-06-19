package client.out;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-19 16:52
 * @description: 连接启动帮助类
 **/
@Slf4j
public class ClientBootStrapHelper {

    private Channel channel = null;

    public void start(EventLoopGroup workerGroup, ChannelInitializer channelInitializer,
                      String host, int port){

        if (host==null || port==0){
            System.out.println("配置信息有误");
            return;
        }

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);

            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.handler(channelInitializer);

            channel = bootstrap.connect(host,port).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future ->{
                channel.deregister();
                channel.close();
            });
        }catch (Exception e){
            close();
            workerGroup.shutdownGracefully();
            log.error("{}-- 关闭线程组内所有连接", this.getClass());
            e.printStackTrace();
        }

    }

    public synchronized void close(){
        if (channel != null){
            channel.close();
        }
    }

}
