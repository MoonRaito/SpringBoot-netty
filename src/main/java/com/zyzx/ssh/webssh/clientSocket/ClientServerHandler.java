package com.zyzx.ssh.webssh.clientSocket;

import com.zyzx.ssh.webssh.common.ConstantParam;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * 描述:
 *  handler
 *
 *  extends
 *      SimpleChannelInboundHandler<String>
 *      改为
 *      ChannelInboundHandlerAdapter
 *
 * @author JSKJ
 * @create 2018-07-18 16:07
 */


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Sharable
public class ClientServerHandler extends ChannelInboundHandlerAdapter {


    private static Logger log = LoggerFactory.getLogger(ClientServerHandler.class);
    // 第一次发的包为认证包
//    private final AttributeKey<ClientPacket> firstPacket = AttributeKey.valueOf("firstPacket");

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.WRITER_IDLE){
                log.info("send");
                ctx.writeAndFlush("{'id':'123'}\r\n");
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)

        System.out.println("handlerRemoved : " + ctx.channel().remoteAddress() + " active !");
        super.handlerRemoved(ctx);
        ctx.close();

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {



        /***********************************
         **********         *****************
         **********         *****************
         **********         *********  我 ***
         **********         *********  是 ***
         ********             *******  入 ***
         *********           ********  口 ***
         **********         *****************
         ************     *******************
         *************   ********************
         ************** *********************
         ***********************************/

        log.info("read:"+msg.toString());
    }



    /*
     *
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     *
     * channelActive 和 channelInActive
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("建立连接  RamoteAddress : " + ctx.channel().remoteAddress() + " active !");

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常拉！！~~");
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }


    @Autowired
    private ClientConnect clientConnect;


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("掉线了...");
        //使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(() -> clientConnect.connect(ConstantParam.SERVICE_PORT, ConstantParam.SERVICE_HOST), 1L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

}
