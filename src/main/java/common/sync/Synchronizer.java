package common.sync;

import common.core.worker.WorkerGlobal;

public abstract class Synchronizer {



    /**
     * Receive an event from master and do proper action
     * @param actionKey
     * @param action
     */
    public abstract void receiveEvent(String actionKey, Action action);

    /**
     * Create an event and send it to master
     */
    public abstract void sendEvent(String actionKey, Action action);
}
