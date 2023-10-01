package ratelimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;

public class Limiter {

    private Limiter() {}
    private Bucket bucket;

    public Limiter(RateLimitConfig config){
        int tokens = 0;
        int capacity = 0;
        String timeUnit = null;

        System.out.println("rate limit handler created");
        try{
            tokens = config.getTokens() != 0 ? config.getTokens() : 10;
            capacity = config.getCapacity() != 0 ? config.getCapacity() : 10;
            timeUnit = StringUtils.defaultIfEmpty(config.getTimeUnit().toString(), "SECONDS");

        }catch (Exception e){
            e.printStackTrace();
        }

        Refill refill = Refill.intervally(tokens, checkTimeUnit(timeUnit));
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        Bucket bucket = Bucket.builder()
                .addLimit(limit)
                .build();
        this.bucket = bucket;
    }

    private Duration checkTimeUnit(String timeUnit){
        switch (timeUnit){
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

    protected boolean tryConsume(int tokens){
        System.out.println("try consume");
        return this.bucket.tryConsume(tokens);
    }

    protected long getAvailableTokens(){
        return this.bucket.getAvailableTokens();
    }


}
