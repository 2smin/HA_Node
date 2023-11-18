package common.db;

import common.core.master.MasterGlobal;
import entity.Worker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class WorkerService {

    private static Logger logger = LogManager.getLogger(WorkerService.class.getName());
    private WorkerService(){}

    private static class WorkerHolderInstance{
        private static final WorkerService MW_CONNECTION_MANAGER = new WorkerService();
    }

    public static WorkerService getInstance(){
        return WorkerHolderInstance.MW_CONNECTION_MANAGER;
    }

    //TODO : how to handler reconnected worker?
    public String addWorkerToDatabase(String ip){

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

            return id;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public String issueNewWorkerId(){
        String workerId = java.util.UUID.randomUUID().toString().substring(0,10);
        logger.info("issue new worker id : " + workerId);
        return workerId;
    }
}
