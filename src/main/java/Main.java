import bootstraps.ExternalBootstrap;

import enums.Constants;
import master.MainBootstrap;

import java.util.logging.Logger;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {

        try{
            MainBootstrap master = MainBootstrap.Holder.INSTANCE;
            master.init();
            if(Constants.NODE_TYPE.equals("master")){
                master.asMasterConfigServer();
            }else{
                master.asWorkerNode();
            }

            //TODO : init bootstrapManager to manage all bootstraps, and put boot in to manager
            ExternalBootstrap externalBootstrap = new ExternalBootstrap();
            externalBootstrap.initBootstrap();
            externalBootstrap.connectToMaster();

        }catch (InterruptedException e){
            e.printStackTrace();
            logger.severe("error occurred while initializing master and httpBootstraps");
        }

    }
}
