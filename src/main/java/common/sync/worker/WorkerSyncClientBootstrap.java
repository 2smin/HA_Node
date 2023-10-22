package common.sync.worker;

import common.core.worker.WorkerGlobal;
import common.enums.Constants;
import common.sync.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class WorkerSyncClientBootstrap {

    private static Logger logger = LogManager.getLogger(WorkerSyncClientBootstrap.class.getName());

    private WorkerSyncClientBootstrap() {}
    private static WorkerSyncClientBootstrap instance = new WorkerSyncClientBootstrap();

    public static WorkerSyncClientBootstrap getInstance(){
        return instance;
    }

    private Bootstrap workerSyncClientBootstrap;
    private EventLoopGroup nodeSyncEventLoopGroup;

    private LocalChannel localChannelToCore;

    public void init() throws InterruptedException {
        nodeSyncEventLoopGroup = new NioEventLoopGroup(1);
        workerSyncClientBootstrap = new Bootstrap();
        workerSyncClientBootstrap.group(nodeSyncEventLoopGroup);
        workerSyncClientBootstrap.channel(NioSocketChannel.class);
        workerSyncClientBootstrap.option(ChannelOption.SO_REUSEADDR, true);
        workerSyncClientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                logger.info("Node initialized with master sync server");
                pipeline.addLast(new SyncMessageEncoder());
                pipeline.addLast(new SyncMessageDecoder());
                pipeline.addLast(new WorkerNodeSyncHandler(localChannelToCore));
            }
        });
        workerSyncClientBootstrap.localAddress(WORKER_NODE_SYNC_PORT);

        InetSocketAddress masterRemoteAddress = new InetSocketAddress(System.getenv("MASTER_CONFIG_NODE_IP"), MASTER_CONFIG_NODE_SYNC_PORT);
        try{
            ChannelFuture future = workerSyncClientBootstrap.connect(masterRemoteAddress).syncUninterruptibly();
            if(future.isSuccess()){
                logger.info("connected to master successfully");
                WorkerGlobal.getInstance().registerMaster(future.channel());
            }else{
                throw new RuntimeException("failed to connect to master");
            }
            connectToCore();
        }catch (Exception e){
            logger.error("failed to connect to master");
            logger.error(e.getMessage());
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
