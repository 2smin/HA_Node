package bootstraps;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.StringUtils;
import repository.RateLimitConfig;
import repository.RateLimitContainer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RateLimitHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Bucket bucket;

    private int tokens;
    private int capacity;
    private String timeUnit;

    public RateLimitHandler(RateLimitConfig config){
        System.out.println("rate limit handler created");
        try{
            this.tokens = config.getTokens() != 0 ? config.getTokens() : 10;
            this.capacity = config.getCapacity() != 0 ? config.getCapacity() : 10;
            this.timeUnit = StringUtils.defaultIfEmpty(config.getTimeUnit().toString(), "SECONDS");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("rate limit handler activated");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("rate limit handler registered");
        Refill refill = Refill.intervally(tokens, checkTimeUnit());
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Duration checkTimeUnit(){
        switch (timeUnit.toString()){
            case "SECONDS":
                return Duration.ofSeconds(1);
            case "MINUTES":
                return Duration.ofMinutes(1);
            case "HOURS":
                return Duration.ofHours(1);
            case "DAYS":
                return Duration.ofDays(1);
            default:
                return Duration.ofSeconds(1);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if(bucket.tryConsume(1)){
            ctx.fireChannelRead(msg);
        }else{
            System.out.println("rate limit exceeded");
        }
    }

}
