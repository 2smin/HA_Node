package common.sync.master;

import common.core.master.MasterGlobal;
import common.enums.Constants;
import common.sync.Action;
import common.sync.SyncMessageDto;
import common.db.WorkerService;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.local.LocalChannel;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class MasterNodeSyncHandler extends ChannelDuplexHandler {

    private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(MasterNodeSyncHandler.class.getName());

    private LocalChannel localChannelToCore;

    public MasterNodeSyncHandler(LocalChannel localChannelToCore) {
        this.localChannelToCore = localChannelToCore;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //get worker channel ip address or something, value is channel , insert into global channelMap
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String host = socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();

        String workerAddress = host + ":" + port;
        logger.info("master node sync handler channel active, worker address : {}", workerAddress);

        System.out.println(workerAddress);

        String workerID = WorkerService.getInstance().addWorkerToDatabase(workerAddress);
        MasterGlobal.getInstance().addWorkerChannel(workerID, ctx.channel());

        SyncMessageDto syncMessageDto = new SyncMessageDto();
        syncMessageDto.setAction(Action.INITIALIZE);
        syncMessageDto.setWorkerId(workerID);
        syncMessageDto.setSyncElement(Constants.SyncElement.BOOTSTRAP);

        //FIXME : syncElement : global?? do we need ?

        //send uuid to worker
        ctx.writeAndFlush(syncMessageDto);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("master node sync handler received message");

        SyncMessageDto syncMessageDto = (SyncMessageDto) msg;
        Action action = syncMessageDto.getAction();
        Constants.SyncElement syncElement = syncMessageDto.getSyncElement();
        String workerId = syncMessageDto.getWorkerId();

        logger.info("sync message from worker node : {}", workerId);
        logger.info("action : {}", action);
        logger.info("sync element : {}", syncElement);

        //TODO : bypass all those message to other worker node, from core bootstrap
        MasterSyncManager.getInstance().receiveEvent(syncMessageDto);

    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.info("master node sync handler write message");
        ctx.writeAndFlush(msg);
    }
}
