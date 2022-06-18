package server.handler;

import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.annotation.PostConstruct;

/**
 * @program: NatEngine
 * @author: cx
 * @create: 2022-06-18 08:13
 * @description: 服务端接收外部请求
 **/
public class RemoteHandler extends ChannelInboundHandlerAdapter {

    private static RemoteHandler remoteHandler;

    @PostConstruct
    public void init(){
        remoteHandler = this;
    }

}
