package common.sync;

import common.core.worker.WorkerGlobal;

public abstract class Synchronizer {

    private static SyncManager syncManager = SyncManager.getInstance();

    public abstract void doSync(String actionKey, Action action);

    /**
     * Create a sync message and send it to master
     */
    public void sendEvent(String actionKey, Action action){
        SyncMessageDto messageDto = new SyncMessageDto();
        messageDto.setAction(action);
        messageDto.setActionKey(actionKey);
        messageDto.setWorkerId(WorkerGlobal.getInstance().getCurrentWorkerId());
        syncManager.sendSyncEvent(messageDto);
    }
}
