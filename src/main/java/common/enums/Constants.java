package common.enums;


public class Constants {

    public static final String EXTERNAL_HTTP_EP1 = "external_http_ep1";
    public static final String MAIN_LOCAL_BOOTSTRAP = "main_local_bootstrap";
    public static final int MASTER_CONFIG_NODE_SYNC_PORT = 8081;
    public static final int WORKER_NODE_SYNC_PORT = 8082;
    public static final String NODE_TYPE = System.getenv("node_type");

    public static final int WORKER_HTTP_PORT = System.getenv("worker_http_port") != null ? Integer.parseInt(System.getenv("worker_http_port")) : 8080;
    //Sync Elementes
    public enum SyncElement {
        RATE_LIMITER,
        API_KEY,
        BOOTSTRAP;


    }

    public enum AuthenticationType {
        API_KEY,
        JWT;

    }

}

