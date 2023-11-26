package common.core.master.service;

import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpMethod.*;

public interface NodeService {

    default FullHttpResponse doService(FullHttpRequest request){
        HttpMethod method = request.getMethod();
        if (GET.equals(method)) {
            return doGet(request);
        } else if (POST.equals(method)) {
            return doPost(request);
        } else if (DELETE.equals(method)) {
            return doDelete(request);
        }
        return null;
    }

    public FullHttpResponse doGet(FullHttpRequest request);

    public FullHttpResponse doPost(FullHttpRequest request);

    public FullHttpResponse doDelete(FullHttpRequest request);

}
