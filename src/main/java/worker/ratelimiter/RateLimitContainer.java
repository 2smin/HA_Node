package worker.ratelimiter;

import common.core.worker.WorkerGlobal;
import common.enums.Constants;
import common.sync.SyncMessageDto;
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

    static {
        instance.addRateLimiter("testApiKey", new Limiter(new RateLimitConfig(5, TimeUnit.SECONDS, 10)));
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
    public void receiveEvent(String actionKey, Action action) {
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
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("node sync failed");
        }
    }

    @Override
    public void sendEvent(String actionKey, Action action) {

        try{
            SyncMessageDto messageDto = new SyncMessageDto();
            messageDto.setAction(action);
            messageDto.setSyncElement(Constants.SyncElement.RATE_LIMITER);
            messageDto.setActionKey(actionKey);
            messageDto.setWorkerId(WorkerGlobal.getInstance().getCurrentWorkerId());

            WorkerGlobal.getInstance().getSynchronizerChannel().writeAndFlush(messageDto);

        }catch (Exception e){
            logger.error("un error occurred while sending sync event");
            logger.error(e.getMessage());
        }
    }
}
