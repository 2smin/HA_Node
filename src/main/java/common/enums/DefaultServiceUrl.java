package common.enums;


import common.core.master.service.APIKeyService;
import common.core.master.service.NodeService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * For simple http request handling, remove this after set url routing system
 */
public class DefaultServiceUrl {

    public static Map<String,Class<NodeService>> urlMap = new HashMap<>();

    public static void registerServiceClass(String url, Class clazz){
        urlMap.put(url.toUpperCase(Locale.ROOT), clazz);
    }

    public static Class getServiceClass(String url){
        return urlMap.get(url.toUpperCase(Locale.ROOT));

    }

    static {
        registerServiceClass("/apiKey", APIKeyService.class);
    }



}
