package worker.auth;

import common.enums.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticationHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LogManager.getLogger(AuthenticationHandler.class.getName());
    private Constants.AuthenticationType authenticationType;
    private Authenticator authenticator;

    public AuthenticationHandler(Constants.AuthenticationType authenticationType){
        this.authenticationType = authenticationType;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("authentication handler registered");
        switch (this.authenticationType){
            case API_KEY:
                authenticator = new ApiKeyAuthenticator();
                break;
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
        logger.info("authentication handler received http request message");

        if(authenticator.authenticate(httpRequest)){
            logger.info("authentication success");
            ctx.fireChannelRead(httpRequest);
        }else{
            logger.error("authentication failed");
            ctx.writeAndFlush(createResponse(httpRequest));
        }
    }

    private FullHttpResponse createResponse(FullHttpRequest httpRequest){
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(),
                HttpResponseStatus.UNAUTHORIZED);
        httpResponse.headers().set(httpRequest.headers());
        httpResponse.content().writeBytes(
                "{\"message\":\"authentication failed\"}".getBytes()
        );
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, httpResponse.content().readableBytes());
        return httpResponse;
    }
}
