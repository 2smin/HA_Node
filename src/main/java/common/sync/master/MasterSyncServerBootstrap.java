package common.sync.master;

import common.core.master.MasterGlobal;
import common.enums.Constants;
import common.sync.Action;
import common.sync.SyncMessageDecoder;
import common.sync.SyncMessageDto;
import common.sync.SyncMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static common.enums.Constants.MASTER_CONFIG_NODE_SYNC_PORT;

/**
 * Communication between nodes is done through this bootstrap node. <br><br>
 * Master Configuration Server open this bootstrap as server mode <br>
 * Worker nodes open this bootstrap as client mode
 * </p> <br>
 *
 * Commute with websocket protocol
 */
public class MasterSyncServerBootstrap {

    private static Logger logger = LogManager.getLogger(MasterSyncServerBootstrap.class.getName());

    private MasterSyncServerBootstrap() {}
    private static MasterSyncServerBootstrap instance = new MasterSyncServerBootstrap();

    public static MasterSyncServerBootstrap getInstance(){
        return instance;
    }

    private ServerBootstrap masterSyncServerBootstrap;
    private EventLoopGroup nodeSyncEventLoopGroup;

    private LocalChannel localChannelToCore;

    public void init() throws InterruptedException{
        nodeSyncEventLoopGroup = new NioEventLoopGroup(1);
        masterSyncServerBootstrap = new ServerBootstrap();
        masterSyncServerBootstrap.group(nodeSyncEventLoopGroup);
        masterSyncServerBootstrap.channel(NioServerSocketChannel.class);
        masterSyncServerBootstrap.option(ChannelOption.SO_REUSEADDR, true);
        masterSyncServerBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                logger.info("Node initialized with worker sync server : ", ch.remoteAddress().toString());
                pipeline.addLast(new SyncMessageEncoder());
                pipeline.addLast(new SyncMessageDecoder());
                pipeline.addLast(new MasterNodeSyncHandler(localChannelToCore));

            }
        });
        masterSyncServerBootstrap.bind(MASTER_CONFIG_NODE_SYNC_PORT).sync();

        connectToCore();

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
