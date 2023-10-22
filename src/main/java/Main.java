
import common.sync.SyncManager;
import common.sync.master.MasterSyncServerBootstrap;
import common.sync.worker.WorkerSyncClientBootstrap;
import worker.bootstraps.ExternalBootstrap;

import common.enums.Constants;
import common.core.CoreBootstrap;
import worker.ratelimiter.RateLimitContainer;

import java.util.logging.Logger;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {

        logger.info("Initialize node type : " + Constants.NODE_TYPE);
        try{
            CoreBootstrap master = CoreBootstrap.Holder.INSTANCE;
            master.init();
            if(Constants.NODE_TYPE.equalsIgnoreCase("master")){
                master.asMasterConfigServer();
                MasterSyncServerBootstrap.getInstance().init();
            }else{
                master.asWorkerNode();
                WorkerSyncClientBootstrap.getInstance().init();

                //TODO : init bootstrapManager to manage all bootstraps, and put boot in to manager
                ExternalBootstrap externalBootstrap = new ExternalBootstrap();
                externalBootstrap.initBootstrap();
                externalBootstrap.connectToCore();

            }
            initSynchronizer();
        }catch (InterruptedException e){
            e.printStackTrace();
            logger.severe("error occurred while initializing master and httpBootstraps");
        }

    }


    private static void initSynchronizer(){
        SyncManager syncManager = SyncManager.getInstance();
        syncManager.addSyncElement(
                Constants.SyncElement.RATE_LIMITER, RateLimitContainer.getInstance()
        );
    }
}
