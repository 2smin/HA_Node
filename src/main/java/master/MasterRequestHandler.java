package master;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;

public class MasterRequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            System.out.println("master request handler received http message");
            FullHttpRequest httpRequest = (FullHttpRequest) msg;

            System.out.println(httpRequest.uri());
            System.out.println(httpRequest.method());

            System.out.println("[headers]");
            for(Map.Entry<String, String> httpHeader : httpRequest.headers()){
                System.out.println(httpHeader.getKey() + " : " + httpHeader.getValue());
            }
        }
    }
}
