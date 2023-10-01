package bootstraps;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import ratelimiter.Limiter;
import ratelimiter.RateLimitConfig;
import ratelimiter.RateLimitContainer;

import java.time.Duration;

public class RateLimitHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private RateLimitContainer rateLimitContainer = RateLimitContainer.getInstance();

    public RateLimitHandler(){

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("rate limit handler activated");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("rate limit handler registered");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        HttpHeaders requestHeaders = msg.headers();
        System.out.println("rate limit handler read0");
        //TODO : check apiKey header exist at front of pipeline (ex. auth handler)
        if(!requestHeaders.contains("apiKey")) throw new RuntimeException("apiKey header not found");
        String apiKey = requestHeaders.get("apiKey");

        rateLimitContainer.checkExist(apiKey);
        if(rateLimitContainer.tryConsume(apiKey, 1)) {
            ctx.fireChannelRead(msg);
        }else{
            throw new RuntimeException("rate limit exceeded");
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("rate limit handler exception caught");
        System.out.println(cause.getMessage());
    }
}
