package common.sync;

import common.core.worker.WorkerGlobal;
import common.enums.Constants;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.ClientAuth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import common.sync.master.MasterNodeSyncHandler;
import common.sync.worker.WorkerNodeSyncHandler;

import java.net.InetSocketAddress;

import static common.enums.Constants.MASTER_CONFIG_NODE_SYNC_PORT;
import static common.enums.Constants.WORKER_NODE_SYNC_PORT;

/**
 * Communication between nodes is done through this bootstrap node. <br><br>
 * Master Configuration Server open this bootstrap as server mode <br>
 * Worker nodes open this bootstrap as client mode
 * </p> <br>
 *
 * Commute with websocket protocol
 */
public class NodeSyncBootstrap {

    private static Logger logger = LogManager.getLogger(NodeSyncBootstrap.class.getName());

    private NodeSyncBootstrap() {}
    private static NodeSyncBootstrap instance = new NodeSyncBootstrap();

    public static NodeSyncBootstrap getInstance(){
        return instance;
    }

    private AbstractBootstrap nodeSyncBootstrap;
    private EventLoopGroup nodeSyncEventLoopGroup;

    private LocalChannel localChannelToCore;

    public void init() throws InterruptedException{
        connectToCore();
        nodeSyncEventLoopGroup = new NioEventLoopGroup(1);
        //set master serverbootstrap
        if (Constants.NODE_TYPE.equals("master")) {
            nodeSyncBootstrap = new ServerBootstrap();
            nodeSyncBootstrap.group(nodeSyncEventLoopGroup);
            nodeSyncBootstrap.channel(NioServerSocketChannel.class);
            nodeSyncBootstrap.handler(new MasterNodeSyncHandler(localChannelToCore));

            nodeSyncBootstrap.bind(MASTER_CONFIG_NODE_SYNC_PORT);

        //set worker bootstrap
        }else{
            nodeSyncBootstrap = new Bootstrap();
            nodeSyncBootstrap.group(nodeSyncEventLoopGroup);
            nodeSyncBootstrap.channel(NioSocketChannel.class);
            nodeSyncBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    logger.info("Node initialized with worker node");
                    pipeline.addLast(new SyncMessageDecoder());
                    pipeline.addLast(new WorkerNodeSyncHandler(localChannelToCore));
                }
            });
            nodeSyncBootstrap.localAddress(WORKER_NODE_SYNC_PORT);

            Bootstrap clientBootstrap = (Bootstrap) nodeSyncBootstrap;
            InetSocketAddress masterRemoteAddress = new InetSocketAddress(System.getenv("MASTER_CONFIG_NODE_IP"), MASTER_CONFIG_NODE_SYNC_PORT);
            ChannelFuture future = clientBootstrap.connect(masterRemoteAddress).sync();

            if(future.isSuccess()){
                WorkerGlobal.getInstance().registerMaster(future.channel());
            }else{
                logger.error("failed to connect to master");
                throw new RuntimeException("failed to connect to master");
            }
        }


    }

    public void connectToCore(){
        this.localChannelToCore = new LocalChannel();
        nodeSyncEventLoopGroup.register(localChannelToCore);
        localChannelToCore.connect(new LocalAddress(Constants.MAIN_LOCAL_BOOTSTRAP)).addListener(
                (ChannelFutureListener) future -> {
                    if(future.isSuccess()){
                        logger.info("connected to core");
                    }

                    SyncMessageDto syncMessageDto = new SyncMessageDto();
                    syncMessageDto.setAction(Action.INITIALIZE);
                    future.channel().writeAndFlush(syncMessageDto);
                }
        );
    }

}
