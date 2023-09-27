package bootstraps;

import enums.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class ExternalBootstrap {

    private ServerBootstrap serverBootstrap;
    private LocalChannel masterLocalChannel;

    public ExternalBootstrap(LocalChannel masterLocalChannel){
        this.masterLocalChannel = masterLocalChannel;
    }

    public void initBootstrap(){
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup(10));
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.localAddress(new LocalAddress(Constants.MASTER_EVENT_MANAGER));
        serverBootstrap.bind(8080);
    }

    private void addHandlers(){
        serverBootstrap.childHandler(
                new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        // In case of Master Event Manager channel
                        if(ch instanceof LocalChannel){
                            //TODO : add http response hadnler
                        }

                        // Others, channel from external http client
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        pipeline.addLast(new HttpContentCompressor());

                        pipeline.addLast(new SimpleHttpRequestHandler(masterLocalChannel));

                    }
                }
        );
    }
}
