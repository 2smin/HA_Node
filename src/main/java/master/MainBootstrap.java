package master;

import enums.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainBootstrap {

    private static Logger logger = LogManager.getLogger(MainBootstrap.class.getName());
    private MainBootstrap() {}
    public static class Holder {
        public static final MainBootstrap INSTANCE = new MainBootstrap();
    }
    public MainBootstrap getInstance(){
        return Holder.INSTANCE;
    }

    private EventLoopGroup masterEventLoopGroup;
    private ServerBootstrap masterServerBootstrap;
    private LocalChannel masterLocalChannel;
    public void init() throws InterruptedException{
        masterEventLoopGroup = new NioEventLoopGroup(10);
        masterServerBootstrap = new ServerBootstrap();
        masterServerBootstrap.group(masterEventLoopGroup);
        masterServerBootstrap.channel(LocalServerChannel.class);
        asMasterConfigServer();
    }

    /**
    * This method will initialize the node as master configuration server
    * Master Config Server hadle synchronize, failover of all worker nodes
     **/
    public void asMasterConfigServer(){
        masterServerBootstrap.childHandler(
                new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MasterControlServerHandler());
                        logger.info("Node initialized with master configuration server");
                    }
                }
        );
    }

    /**
     * This method will initialize the node as worker node
     * Worker node will handle all the requests from external, and send events to master config server
     */
    public void asWorkerNode(){
        masterServerBootstrap.childHandler(
                new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new WorkerRequestHandler());
                        logger.info("Node initialized with worker");
                    }
                }
        );
    }

    private void bind() throws InterruptedException{
        masterServerBootstrap.bind(new LocalAddress(Constants.MAIN_LOCAL_BOOTSTRAP)).sync();
    }

    public EventLoopGroup getMasterEventLoopGroup(){
        return masterEventLoopGroup;
    }

    public LocalChannel getMasterLocalChannel(){
        return masterLocalChannel;
    }
}
