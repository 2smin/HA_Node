package common.sync.master;

import common.core.master.MasterGlobal;
import common.enums.Constants;
import common.sync.SyncMessageDto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterSyncManager {

    private static Logger logger = LogManager.getLogger(MasterSyncManager.class.getName());

    private MasterSyncManager(){}
    public static MasterSyncManager instance = new MasterSyncManager();
    public static MasterSyncManager getInstance(){
        return instance;
    }

    private static Map<Constants.SyncElement, Object> syncElements = new HashMap<>();

    public void addSyncElement(Constants.SyncElement element, Object obj){
        syncElements.put(element, obj);
    }
    public void receiveEvent(SyncMessageDto messageDto){
        Constants.SyncElement syncElement = messageDto.getSyncElement();
        if(!syncElements.containsKey(syncElement))
            throw new UnsupportedOperationException("This element is not supported in this version");

        sendEventToAllWorkerExcept(messageDto);
    }



    //event를 보낸 worker를 제외한 모든 worker에게 보내기
    private void sendEventToAllWorkerExcept(SyncMessageDto messageDto){
        List<Channel> workerChannelList = MasterGlobal.getInstance().getAllWorkerChannelExcept(messageDto.getWorkerId());
        workerChannelList.stream()
                .filter(channel -> channel.isActive())
                .forEach(
                        channel -> {
                            ChannelFuture channelFuture = channel.writeAndFlush(messageDto);
                            logger.info("send event to worker : {}", channel.remoteAddress());
                        });
    }
}
