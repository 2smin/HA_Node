import bootstraps.ExternalBootstrap;
import io.netty.channel.local.LocalChannel;
import master.Master;

public class Main {

    public static void main(String[] args) {

        Master master = Master.Holder.INSTANCE;
        master.init();

        //TODO : init bootstrapManager to manage all bootstraps, and put boot in to manager
        ExternalBootstrap externalBootstrap = new ExternalBootstrap(master.getMasterLocalChannel());
        externalBootstrap.initBootstrap();


    }
}
