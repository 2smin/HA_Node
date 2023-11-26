package common.core;

import common.core.master.MasterControlServerHandler;
import common.core.master.MasterGlobal;
import common.core.worker.WorkerRequestHandler;
import common.enums.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

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
        masterEventLoopGroup = MasterGlobal.masterLoop;
        masterServerBootstrap = new ServerBootstrap();
        masterServerBootstrap.group(masterEventLoopGroup);
        masterServerBootstrap.channel(NioServerSocketChannel.class);
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
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        pipeline.addLast(new HttpContentCompressor());
                        pipeline.addLast(new MasterControlServerHandler());
                        logger.info("Node initialized as master configuration server");
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
                        logger.info("Node initialized as worker");

                        // set Handler differently for each client (http, sync), distinguish by channelAttr ? or by port?


                    }
                }
        );
        bind();
    }

    private void bind() {
        try{
            masterServerBootstrap.bind(8000).sync();
        }catch (InterruptedException e){
            logger.error("Error while binding master configuration server", e);
            throw new RuntimeException(e);
        }
    }

    public EventLoopGroup getMasterEventLoopGroup(){
        return masterEventLoopGroup;
    }

    public LocalChannel getMasterLocalChannel(){
        return masterLocalChannel;
    }
}
