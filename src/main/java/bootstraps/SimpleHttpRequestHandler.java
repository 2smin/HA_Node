package bootstraps;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.logging.log4j.Logger;

public class SimpleHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private LocalChannel masterEventManagerChannel;

    public SimpleHttpRequestHandler(LocalChannel masterEventManagerChannel) {
        this.masterEventManagerChannel = masterEventManagerChannel;
    }

    //TODO : send channel registerd event to masterEventManagerChannel, and sync with otehr nodes.

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        //TODO simpley send httpRequest to masterEventManagerChannel
        System.out.println("simple http request received...");

        masterEventManagerChannel.writeAndFlush(msg);
    }
}
