package com.zyzx.ssh.webssh.clientSocket;

import com.zyzx.ssh.webssh.common.ConstantParam;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * 描述:
 *  netty 连接
 *
 * @author JSKJ
 * @create 2018-12-03 19:34
 */
@Component
public class ClientConnect implements CommandLineRunner {


    private static Logger log = LoggerFactory.getLogger(ClientConnect.class);

    @Autowired
    private ClientServerInitializer clientServerInitializer;
    @Autowired
    private ConnectionListener connectionListener;

    private static final int MAX_FRAME_LENGTH = 9999;
    private static final int LENGTH_FIELD_LENGTH = 0;
    private static final int LENGTH_FIELD_OFFSET = 4;
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;


    private Channel channel;

    public Channel connect(int port,String host) {
        initConnect( port,host);
        return this.channel;
    }


    public void initConnect(int port,String host) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            b.option(ChannelOption.TCP_NODELAY,true);
            b.handler(clientServerInitializer);
//            ChannelFuture f = b.connect(host,port).sync();
//            f.channel().closeFuture().sync();

            ChannelFuture f = b.connect(host,port);
            f.addListener(connectionListener);
            channel = f.channel();
        } catch (Exception e) {
            log.info("初始化连接错误!",e);
        }
    }

    @Override
    public void run(String... strings) throws Exception {
        this.connect(ConstantParam.SERVICE_PORT, ConstantParam.SERVICE_HOST);
    }
}
