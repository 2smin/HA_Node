package common.sync.master;

import common.core.master.MasterGlobal;
import common.sync.Action;
import common.sync.SyncMessageDto;
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
        logger.info("master node sync handler channel active, worker address : " + workerAddress);

        MasterGlobal.getInstance().addWorkerChannel(workerAddress, ctx.channel());

        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        //check database in case of already connected node or not
        /*
        1. if already connected node, regenerate uuid
        2. if new node, generate uuid
        3. in all case, check api key, (api key or auth key contains in k8s.yaml)
         */

        //TODO : check database exist ip

        String workerID = MasterGlobal.getInstance().issueWorkerId();

        //TODO : save workerID to database

        SyncMessageDto syncMessageDto = new SyncMessageDto();
        syncMessageDto.setAction(Action.INITIALIZE);
        syncMessageDto.setWorkerId(workerID);

        //FIXME : syncElement : global?? do we need ?

        //send uuid to worker
        ctx.writeAndFlush(syncMessageDto);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("master node sync handler received message");


        localChannelToCore.writeAndFlush(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }
}
