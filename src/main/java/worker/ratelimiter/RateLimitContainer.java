package worker.ratelimiter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import common.sync.Action;
import common.sync.Synchronizer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;



public class RateLimitContainer extends Synchronizer {

    private static Logger logger = LogManager.getLogger(RateLimitContainer.class.getName());
    private RateLimitContainer() {}

    private static RateLimitContainer instance = new RateLimitContainer();

    // unnecessary lazy loading
//    public static class Holder {
//        public static final RateLimitContainer INSTANCE = new RateLimitContainer();
//    }
    public static RateLimitContainer getInstance(){
//        return Holder.INSTANCE;
        return instance;
    }

    private RateLimitConfig config;
    private Map<String, Limiter> ratelimiters = new HashMap<>();

    public void addRateLimiter(String userApiKey, Limiter limiter){
        ratelimiters.put(userApiKey, limiter);
    }

    public void setConfig(RateLimitConfig config){
        this.config = config;
    }

    public void checkExist(String apiKey) {
        Objects.requireNonNull(apiKey, "apiKey cannot be null");
        if(ratelimiters.containsKey(apiKey)){
            return;
        }else{
            logger.info("create new ratelimiter : " + apiKey);
            if(config==null) config = new RateLimitConfig(10, TimeUnit.SECONDS,10);
            Limiter limiter = new Limiter(config);
            ratelimiters.put(apiKey,limiter);
        }
    }

    public boolean tryConsume(String apiKey, int num){
        return ratelimiters.get(apiKey).tryConsume(num);
    }

    @Override
    public void doSync(String actionKey, Action action) {
        try{
            switch (action){
                case REGISTER:
                    ratelimiters.put(actionKey,new Limiter(this.config));
                    break;
                case UNREGISTER:
                    ratelimiters.remove(actionKey);
                    break;
                case UPDATE:
                    ratelimiters.get(actionKey).tryConsume(1);
                    break;
            }
        }catch (Exception e){
            throw new RuntimeException("node sync failed");
        }
    }
}
