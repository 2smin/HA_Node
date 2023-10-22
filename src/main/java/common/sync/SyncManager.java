package common.sync;

import common.core.master.MasterGlobal;
import common.core.worker.WorkerGlobal;
import common.enums.Constants;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class SyncManager {

    /*
    worker : send event
    master : receive event
     */

    private static Logger logger = LogManager.getLogger(SyncManager.class.getName());

    private static Map<Constants.SyncElement, Object> syncElements = new HashMap<>();

    private SyncManager (){}
    public static SyncManager instance = new SyncManager();
    public static SyncManager getInstance(){
        return instance;
    }

    public void addSyncElement(Constants.SyncElement element, Object obj){
        syncElements.put(element, obj);
    }

    /**
     * For master, send sync event to all worker nodes except for the one who sent the event <br>
     * This method only used by master node
     * @param message
     */
    protected void byPassToAllWorker(SyncMessageDto message){

        /*
        1. act as proxy
        2. get ip address or channel information of worker
        3. send event to add server
         */

        Constants.SyncElement element = message.getSyncElement();
        Action action = message.getAction();

        String workerNodeId = message.getWorkerId();

        //db에서 확인 workerId 있는지?? 굳이 해야할까??? 일단 하자..

        Set<Map.Entry<String,Channel>> workerChannelEntrySet =  MasterGlobal.getInstance().getEntries();

        Set<String> failedWorkerIdSet = new HashSet<>();
        workerChannelEntrySet.stream().filter(
                entry -> !entry.getKey().equals(workerNodeId))
                .forEach(
                entry -> entry.getValue().writeAndFlush(message).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(future.isSuccess()){
                            logger.info("master send sync event to worker successfully");
                        }else{
                            logger.error("master send sync event to worker failed");
                            failedWorkerIdSet.add(entry.getKey());
                        }
                    }
                }));

        if(failedWorkerIdSet.size() > 0){
            //TODO : retry?
        }
    }

    /**
     * For worker, receive sync event from master
     * This method only used by worker node
     * @param message sync messageDto
     */
    public void receiveSyncEvent(SyncMessageDto message){

        Action action = message.getAction();
        String actionKey = message.getActionKey();
        Constants.SyncElement element = message.getSyncElement();

        if(!syncElements.containsKey(element))
            throw new UnsupportedOperationException("This element is not supported in this version");

        Synchronizer syncElement = (Synchronizer) syncElements.get(element);
        syncElement.doSync(actionKey, action);
    }

    /**
     * For worker, send sync event to master
     * This method only used by worker node
     */
    public void sendSyncEvent(SyncMessageDto message){
        WorkerGlobal.getInstance().getMasterChannel().writeAndFlush(message);
    }

}
