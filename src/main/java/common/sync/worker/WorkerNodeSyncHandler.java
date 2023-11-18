package common.sync.worker;

import common.core.worker.WorkerGlobal;
import common.sync.Action;
import common.sync.SyncMessageDto;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.local.LocalChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkerNodeSyncHandler extends ChannelDuplexHandler {

    private static Logger logger = LogManager.getLogger(WorkerNodeSyncHandler.class.getName());
    private LocalChannel localChannelToCore;

    public WorkerNodeSyncHandler(LocalChannel localChannelToCore) {
        this.localChannelToCore = localChannelToCore;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.info("worker node send event to master config server");
        ctx.writeAndFlush(msg);
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("worker node sync handler received message from master config server");

        SyncMessageDto syncMessageDto = (SyncMessageDto) msg;
        Action action = syncMessageDto.getAction();
        WorkerGlobal.getInstance().registerWorkerId(syncMessageDto.getWorkerId());
        if(!action.equals(Action.INITIALIZE)){
            WorkerSyncManager.getInstance().receiveSyncEvent(syncMessageDto);
        }

    }
}
