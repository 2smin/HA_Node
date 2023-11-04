package common.core.master;

import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MasterGlobal {

    private static final Logger logger = LogManager.getLogger(MasterGlobal.class.getName());
    private MasterGlobal (){}

    public static MasterGlobal masterGlobal = new MasterGlobal();

    public static MasterGlobal getInstance(){
        return masterGlobal;
    }

    private Map<String, Channel> workerChannelMap = new HashMap<>();

    public void addWorkerChannel(String workerId, Channel channel){
        workerChannelMap.put(workerId, channel);
    }

    public void getWorkerChannel(String workerId){
        workerChannelMap.get(workerId);
    }

    public List<Channel> getAllWorkerChannel(){
        return (List<Channel>) workerChannelMap.values();
    }

    public Set<Map.Entry<String, Channel>> getEntries(){
        return workerChannelMap.entrySet();
    }

    public String issueWorkerId(){
        String workerId = UUID.randomUUID().toString();
        logger.info("issue worker id : " + workerId);
        return workerId;
    }
}
