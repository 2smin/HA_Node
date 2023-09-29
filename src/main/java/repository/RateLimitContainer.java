package repository;

import io.github.bucket4j.Bucket;

import java.util.HashMap;
import java.util.Map;

public class RateLimitContainer {

    private RateLimitContainer() {}

    public static class Holder {
        public static final RateLimitContainer INSTANCE = new RateLimitContainer();
    }

    public RateLimitContainer getInstance(){
        return Holder.INSTANCE;
    }

    Map<String, Bucket> ratelimiters = new HashMap<>();

    public void addRateLimiter(String key, Bucket bucket){
        ratelimiters.put(key, bucket);
    }

    public Bucket getRateLimiter(String key){
        return ratelimiters.get(key);
    }

}
