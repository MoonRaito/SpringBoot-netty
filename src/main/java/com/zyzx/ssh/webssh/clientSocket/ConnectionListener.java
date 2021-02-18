package com.zyzx.ssh.webssh.clientSocket;

import com.zyzx.ssh.webssh.common.ConstantParam;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * 描述:
 *  重连监听
 *
 * @author yly
 * @create 2019-04-12 15:09
 */
@Component
public class ConnectionListener implements ChannelFutureListener {
    private static Logger log = LoggerFactory.getLogger(ConnectionListener.class);
    @Autowired
    private ClientConnect clientConnect;

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (channelFuture.isSuccess()) {
            log.info("服务端链接成功...");

            // 连接成功发送设备认证
            channelFuture.channel().writeAndFlush("{'id':'123'}\r\n");
            return;
        }

        final EventLoop loop = channelFuture.channel().eventLoop();
        loop.schedule(() -> {
            log.info("服务端链接不上，开始重连操作...");
            clientConnect.connect(ConstantParam.SERVICE_PORT, ConstantParam.SERVICE_HOST);
        }, 3L, TimeUnit.SECONDS);

    }
}
