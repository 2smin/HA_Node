package common.enums;


public class Constants {

    public static final String EXTERNAL_HTTP_EP1 = "external_http_ep1";
    public static final String MAIN_LOCAL_BOOTSTRAP = "main_local_bootstrap";
    public static final int MASTER_CONFIG_NODE_SYNC_PORT = 8081;
    public static final int WORKER_NODE_SYNC_PORT = 8082;
    public static String NODE_TYPE = System.getenv("node_type");

    //Sync Elementes
    public static class SyncElement {

        public static final String RATE_LIMITER = "rate_limiter";
        public static final String API_KEY = "api_key";

        public SyncElement valueOf(String str){
            if(str.equalsIgnoreCase(RATE_LIMITER)){
                return new SyncElement();
            }else if(str.equalsIgnoreCase(API_KEY)){
                return new SyncElement();
            }else{
                return null;
            }
        }
    }

}

