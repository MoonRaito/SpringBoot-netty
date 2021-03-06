package com.zyzx.ssh.webssh.clientSocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 描述:
 *  initializer
 *
 * @author JSKJ
 * @create 2018-07-18 16:06
 */
@Component
public class ClientServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final int READ_IDEL_TIME_OUT = 30; // 读超时
    private static final int WRITE_IDEL_TIME_OUT = 30;// 写超时
    private static final int ALL_IDEL_TIME_OUT = 30; // 所有超时

    private final ClientServerHandler clientServerHandler;

    @Autowired
    public ClientServerInitializer(ClientServerHandler clientServerHandler) {
        this.clientServerHandler = clientServerHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();


        // 心跳
        pipeline.addLast(new IdleStateHandler(READ_IDEL_TIME_OUT,WRITE_IDEL_TIME_OUT, ALL_IDEL_TIME_OUT
                , TimeUnit.SECONDS));

        ByteBuf in = Unpooled.copiedBuffer("\r\n".getBytes());
        pipeline.addLast(new DelimiterBasedFrameDecoder(2048,in));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast(clientServerHandler);


    }

}
