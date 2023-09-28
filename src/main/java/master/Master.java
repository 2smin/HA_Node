package master;

import enums.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;

public class Master {

    private Master() {}
    public static class Holder {
        public static final Master INSTANCE = new Master();
    }
    public Master getInstance(){
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
        registerHandlers();
        masterServerBootstrap.bind(new LocalAddress(Constants.MASTER_LOCAL_SERVER)).sync();
    }

    public void registerHandlers(){
        masterServerBootstrap.childHandler(
                new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MasterRequestHandler());
                        System.out.println("MasterBoostrap received connection from " + ch.remoteAddress());
                    }
                }
        );
    }

    public EventLoopGroup getMasterEventLoopGroup(){
        return masterEventLoopGroup;
    }

    public LocalChannel getMasterLocalChannel(){
        return masterLocalChannel;
    }
}
