package common.core.master;

import common.sync.Action;
import common.sync.SyncManager;
import common.sync.SyncMessageDto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

public class MasterControlServerHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LogManager.getLogger(MasterControlServerHandler.class.getName());

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("Node Initialized with master configuration server");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
