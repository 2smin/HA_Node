import common.sync.NodeSyncBootstrap;
import worker.bootstraps.ExternalBootstrap;

import common.enums.Constants;
import common.core.CoreBootstrap;

import java.util.logging.Logger;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {

        try{
            CoreBootstrap master = CoreBootstrap.Holder.INSTANCE;
            master.init();
            if(Constants.NODE_TYPE.equals("common/core")){
                master.asMasterConfigServer();
            }else{
                master.asWorkerNode();
            }

            NodeSyncBootstrap nodeSync = NodeSyncBootstrap.getInstance();
            nodeSync.init();

            //TODO : init bootstrapManager to manage all bootstraps, and put boot in to manager
            ExternalBootstrap externalBootstrap = new ExternalBootstrap();
            externalBootstrap.initBootstrap();
            externalBootstrap.connectToCore();

        }catch (InterruptedException e){
            e.printStackTrace();
            logger.severe("error occurred while initializing master and httpBootstraps");
        }

    }
}
