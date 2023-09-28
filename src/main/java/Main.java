import bootstraps.ExternalBootstrap;
import master.Master;

public class Main {

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
            System.out.println("error occurred while initializing master and httpBootstraps");
        }

    }
}
