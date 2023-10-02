import bootstraps.ExternalBootstrap;

import master.Master;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {

        try{

            Master master = Master.Holder.INSTANCE;
            master.init();

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
