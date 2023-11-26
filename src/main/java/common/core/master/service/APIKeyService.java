package common.core.master.service;

import com.google.gson.JsonObject;
import common.core.master.MasterGlobal;
import io.netty.handler.codec.http.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//TODO : set required params such as header, body as annotation
public class APIKeyService implements NodeService{

    private static Map<String,String> apiKeyMap = new HashMap<>();

    @Override
    public FullHttpResponse doGet(FullHttpRequest request) {
        String[] urlSplit = request.uri().split("/");
        String apiKey = urlSplit[urlSplit.length-1];

        if(!apiKeyMap.containsKey(apiKey)){
            String response = "apiKey not found";
            FullHttpResponse httpResponse =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            httpResponse.content().writeBytes(response.getBytes());
            return httpResponse;
        }else{
            String response = apiKeyMap.get(apiKey);
            FullHttpResponse httpResponse =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            httpResponse.content().writeBytes(response.getBytes());
            return httpResponse;
        }
    }

    @Override
    public FullHttpResponse doPost(FullHttpRequest request) {
        return null;
    }

    @Override
    public FullHttpResponse doDelete(FullHttpRequest request) {
        return null;
    }
}
