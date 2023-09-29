package bootstraps;

import enums.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import repository.RateLimitConfig;

public class ExternalBootstrap {

    private EventLoopGroup httpEventLoopGroup;
    private ServerBootstrap serverBootstrap;
    private LocalChannel localChannelToMaster;

    public void initBootstrap() throws InterruptedException {
        httpEventLoopGroup = new NioEventLoopGroup(1);
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(httpEventLoopGroup, new NioEventLoopGroup(10));
        serverBootstrap.channel(NioServerSocketChannel.class);
        addHandlers();
        serverBootstrap.bind(8080).sync();
    }

    private void addHandlers(){
        serverBootstrap.childHandler(
                new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        // Channel from external http client
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(65536));
                        pipeline.addLast(new HttpContentCompressor());
                        pipeline.addLast(new RateLimitHandler(new RateLimitConfig()));
                        pipeline.addLast(new SimpleHttpRequestHandler(localChannelToMaster));

                    }
                }
        );
    }

    public void connectToMaster(){
        this.localChannelToMaster = new LocalChannel();
        httpEventLoopGroup.register(localChannelToMaster);
        localChannelToMaster.connect(new LocalAddress(Constants.MASTER_LOCAL_SERVER)).addListener(
                (ChannelFutureListener) future -> {
                    if(future.isSuccess()){
                        System.out.println("connect to master complete");
                    }
                }
        );
    }
}
