package worker.auth;

import common.sync.Action;
import common.sync.Synchronizer;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.HashSet;
import java.util.Set;

public class ApiKeyAuthenticator extends Synchronizer implements Authenticator{

    private static Set<String> apiKeys = new HashSet<>();

    @Override
    public boolean authenticate(FullHttpRequest httpRequest) {
        HttpHeaders requestHeaders = httpRequest.headers();
        if(!requestHeaders.contains("API-KEY")) throw new RuntimeException("apiKey header not found");

        String apiKey = requestHeaders.get("API-KEY");

        return apiKeys.contains(apiKey);
    }

    @Override
    public void doSync(String actionKey, Action action) {
        switch (action.toString()){
            case "REGISTER":
                apiKeys.add(actionKey);
                break;
            case "UNREGISTER":
                apiKeys.remove(actionKey);
                break;
        }
    }
}
