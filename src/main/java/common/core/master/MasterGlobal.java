package common.core.master;

import io.netty.channel.Channel;

import java.util.*;

public class MasterGlobal {

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
        //TODO: save to Database;
        return UUID.randomUUID().toString();
    }
}
