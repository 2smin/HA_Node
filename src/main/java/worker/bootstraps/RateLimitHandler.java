package worker.bootstraps;

import common.sync.Action;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import worker.ratelimiter.RateLimitContainer;


public class RateLimitHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LogManager.getLogger(RateLimitHandler.class.getName());
    private RateLimitContainer rateLimitContainer = RateLimitContainer.getInstance();

    public RateLimitHandler(){

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("rate limit handler activated");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("rate limit handler registered");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        logger.info("rate limit handler read httpRequest");

        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        HttpHeaders requestHeaders = httpRequest.headers();

        //TODO : check apiKey header exist at front of pipeline (ex. auth handler)
        if(!requestHeaders.contains("apiKey")) throw new RuntimeException("apiKey header not found");
        String apiKey = requestHeaders.get("apiKey");

        if(rateLimitContainer.doLimit(apiKey)){
            logger.info("rate limit check success");
            rateLimitContainer.sendEvent(apiKey, Action.UPDATE);
            ctx.fireChannelRead(httpRequest);
        }else{
            logger.error("rate limit exceeded");
            ctx.writeAndFlush(createResponse(httpRequest));
        }

    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        logger.error("rate limit handler exception caught");
//        cause.printStackTrace();
//        logger.error(cause.getMessage());
//    }

//    private void sendSync(){
//        SyncMessageDto syncMessageDto = new SyncMessageDto();
//        syncMessageDto.setAction(Action.UPDATE);
//        syncMessageDto.setWorkerId(WorkerGlobal.getInstance().getCurrentWorkerId());
//        syncMessageDto.setSyncElement(Constants.SyncElement.RATE_LIMITER);
//
//        WorkerSyncManager.getInstance().sendSyncEvent(syncMessageDto);
//    }

    private FullHttpResponse createResponse(FullHttpRequest httpRequest){
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(httpRequest.protocolVersion(),
                HttpResponseStatus.TOO_MANY_REQUESTS);
        httpResponse.headers().set(httpRequest.headers());
        httpResponse.content().writeBytes(
                "{\"message\":\"too many request\"}".getBytes()
        );
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, httpResponse.content().readableBytes());
        return httpResponse;
    }
}
