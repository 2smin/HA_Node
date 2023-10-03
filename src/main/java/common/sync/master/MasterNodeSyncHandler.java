package common.sync.master;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.local.LocalChannel;
import org.apache.logging.log4j.Logger;

public class MasterNodeSyncHandler extends ChannelDuplexHandler {

    private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(MasterNodeSyncHandler.class.getName());

    private LocalChannel localChannelToCore;

    public MasterNodeSyncHandler(LocalChannel localChannelToCore) {
        this.localChannelToCore = localChannelToCore;
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
