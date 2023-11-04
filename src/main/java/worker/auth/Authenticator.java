package worker.auth;

import io.netty.handler.codec.http.FullHttpRequest;

public interface Authenticator {

     boolean authenticate(FullHttpRequest httpRequest);


}
