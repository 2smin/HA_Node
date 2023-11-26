package common.core.master;

import common.core.master.service.NodeService;
import common.enums.DefaultServiceUrl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MasterControlServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static Logger logger = LogManager.getLogger(MasterControlServerHandler.class.getName());

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("Node Initialized with master configuration server");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        logger.info("receive http request");

        String uri = msg.getUri();
        logger.info("uri : " + uri);

        if(uri.equalsIgnoreCase("/")){
            logger.info("send response with index.html");
            return;
        }else{
            Class<NodeService> clazz =  DefaultServiceUrl.getServiceClass(uri);
            if(clazz == null){
                logger.error("send response with 404");
                ctx.writeAndFlush(sendResponse(msg));
                return;
            }else{
                FullHttpResponse httpResponse = clazz.getDeclaredConstructor().newInstance().doService(msg);
                ctx.writeAndFlush(httpResponse);
            }

        }
    }

    private final HttpResponse sendResponse(FullHttpRequest request){
        DefaultHttpResponse httpResponse = new DefaultHttpResponse(
                request.getProtocolVersion(), HttpResponseStatus.NOT_FOUND);
        httpResponse.headers().set(request.headers());
        return httpResponse;
    }
}
