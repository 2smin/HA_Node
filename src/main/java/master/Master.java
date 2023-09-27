package master;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalChannel;
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
    private LocalChannel masterLocalChannel;
    public void init(){
        masterEventLoopGroup = new NioEventLoopGroup(10);
        masterLocalChannel = new LocalChannel();
        masterEventLoopGroup.register(masterLocalChannel);

        //TODO : bind masterLocalChannel to ExternalBootstrap
    }

    public EventLoopGroup getMasterEventLoopGroup(){
        return masterEventLoopGroup;
    }

    public LocalChannel getMasterLocalChannel(){
        return masterLocalChannel;
    }
}
