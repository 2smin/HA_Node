package entity;

import common.core.master.MasterGlobal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.HashMap;
import java.util.Map;

public class WorkerHolder {

    private static Logger logger = LogManager.getLogger(WorkerHolder.class.getName());
    private WorkerHolder(){}

    private static class WorkerHolderInstance{
        private static final WorkerHolder workerHolder = new WorkerHolder();
    }

    public static WorkerHolder getInstance(){
        return WorkerHolderInstance.workerHolder;
    }

    private Map<String,String> workerMap = new HashMap<>();

    //TODO : how to handler reconnected worker?
    public boolean checkExist(String ip){
        //check in db
        return false;
    }

    public void reconnectWorker(String workerId, String ip){
        logger.info("reconnect worker : " + workerId + " ip : " + ip);

    }
    public String connectWorker(String ip){

        String id = issueNewWorkerId();

        try{
            EntityManager em = MasterGlobal.emf.createEntityManager();
            EntityTransaction etx = em.getTransaction();
            etx.begin();

            Worker worker = new Worker();
            worker.setId(id);
            worker.setIp(ip);
            em.merge(worker);
            etx.commit();
            em.close();

            workerMap.put(id, ip);

            return id;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getWorkerIp(String workerId){
        return workerMap.get(workerId);
    }

    public void removeWorker(String workerId){
        workerMap.remove(workerId);
    }

    public String issueNewWorkerId(){
        String workerId = java.util.UUID.randomUUID().toString().substring(0,10);
        logger.info("issue new worker id : " + workerId);
        return workerId;
    }
}
