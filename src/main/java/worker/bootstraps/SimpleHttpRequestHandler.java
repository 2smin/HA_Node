package worker.bootstraps;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = LogManager.getLogger(SimpleHttpRequestHandler.class.getName());
    private LocalChannel localChannelToCore;

    public SimpleHttpRequestHandler(LocalChannel localChannelToCore) {
        this.localChannelToCore = localChannelToCore;
    }

    //TODO : send channel registerd event to masterEventManagerChannel, and sync with otehr nodes.

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        //TODO simpley send httpRequest to masterEventManagerChannel
        logger.info("simple http request received...");

        //Simple httpResponse
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        httpResponse.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
        httpResponse.headers().add(HttpHeaders.Names.CONTENT_LENGTH, "hello world".getBytes().length);
        httpResponse.content().writeBytes("hello world".getBytes());

        ctx.writeAndFlush(httpResponse);
    }
}
