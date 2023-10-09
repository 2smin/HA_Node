package common.core;

import common.core.master.MasterControlServerHandler;
import common.core.worker.WorkerRequestHandler;
import common.enums.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CoreBootstrap {

    private static Logger logger = LogManager.getLogger(CoreBootstrap.class.getName());
    private CoreBootstrap() {}
    public static class Holder {
        public static final CoreBootstrap INSTANCE = new CoreBootstrap();
    }
    public CoreBootstrap getInstance(){
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
    public void asMasterConfigServer()throws InterruptedException {
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
        bind();
    }

    /**
     * This method will initialize the node as worker node
     * Worker node will handle all the requests from external, and send events to master config server
     */
    public void asWorkerNode() throws InterruptedException{
        masterServerBootstrap.childHandler(
                new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new WorkerRequestHandler());
                        logger.info("Node initialized with worker");

                        // set Handler differently for each client (http, sync), distinguish by channelAttr ? or by port?


                    }
                }
        );
        bind();
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
