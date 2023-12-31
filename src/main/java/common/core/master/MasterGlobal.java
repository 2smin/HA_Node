package common.core.master;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManagerFactory;
import java.util.*;

public class MasterGlobal {

    private static final Logger logger = LogManager.getLogger(MasterGlobal.class.getName());
    private MasterGlobal (){}

    public static EventLoopGroup masterLoop = new NioEventLoopGroup(1);

    public static Gson gson = new Gson();

    public static MasterGlobal masterGlobal = new MasterGlobal();
    public static MasterGlobal getInstance(){
        return masterGlobal;
    }

    public static EntityManagerFactory emf;
    private Map<String, Channel> workerChannelMap = new HashMap<>();

    public void addWorkerChannel(String workerId, Channel channel){
        workerChannelMap.put(workerId, channel);
    }

    public void getWorkerChannel(String workerId){
        workerChannelMap.get(workerId);
    }

    public ArrayList<Channel> getAllWorkerChannel(){
        ArrayList<Channel> channelList = new ArrayList<>();
       for(Map.Entry<String, Channel> entry : workerChannelMap.entrySet()){
           channelList.add(entry.getValue());
       }
       return channelList;
    }

    public ArrayList<Channel> getAllWorkerChannelExcept(String workerId){
        ArrayList<Channel> channelList = new ArrayList<>();
        for(Map.Entry<String, Channel> entry : workerChannelMap.entrySet()){
            if(entry.getKey().equals(workerId)) continue;
            channelList.add(entry.getValue());
        }
        return channelList;
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
