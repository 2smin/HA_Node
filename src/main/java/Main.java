
import common.core.master.MasterGlobal;
import common.sync.master.MasterSyncManager;
import common.sync.worker.WorkerSyncManager;
import common.sync.master.K8SAPIServerConnector;
import common.sync.master.MasterSyncServerBootstrap;
import common.sync.worker.WorkerSyncClientBootstrap;
import worker.bootstraps.ExternalBootstrap;

import common.enums.Constants;
import common.core.CoreBootstrap;
import worker.ratelimiter.RateLimitContainer;

import javax.persistence.Persistence;
import java.util.logging.Logger;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {

        logger.info("Initialize node type : " + Constants.NODE_TYPE);
        try{
            CoreBootstrap master = CoreBootstrap.Holder.INSTANCE;
            master.init();
            if(Constants.NODE_TYPE.equalsIgnoreCase("master")){

                MasterGlobal.emf = Persistence.createEntityManagerFactory("hibernate_mysql");

                master.asMasterConfigServer();
                MasterSyncServerBootstrap.getInstance().init();

                MasterSyncManager masterSyncManager = MasterSyncManager.getInstance();
                masterSyncManager.addSyncElement(
                        Constants.SyncElement.RATE_LIMITER, RateLimitContainer.getInstance()
                );
            }else{

                //FIXME : remove coreBootstraps on worker node ????
//                master.asWorkerNode();
                WorkerSyncClientBootstrap.getInstance().init();

                //TODO : init bootstrapManager to manage all bootstraps, and put boot in to manager
                ExternalBootstrap externalBootstrap = new ExternalBootstrap();
                externalBootstrap.initBootstrap();
                externalBootstrap.connectToCore();

                WorkerSyncManager workerSyncManager = WorkerSyncManager.getInstance();
                workerSyncManager.addSyncElement(
                        Constants.SyncElement.RATE_LIMITER, RateLimitContainer.getInstance()
                );
            }

        }catch (InterruptedException e){
            e.printStackTrace();
            logger.severe("error occurred while initializing master and httpBootstraps");
        }

    }
}
