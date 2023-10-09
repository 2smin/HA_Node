package common.sync;

import common.enums.Constants;

//use gRPC someday...
public class SyncMessageDto {

    private String actionKey;
    private Constants.SyncElement syncElement;
    private Action action;

    private String workerId;

    public SyncMessageDto() {
    }
    public SyncMessageDto(String actionKey, Constants.SyncElement syncElement, Action action, String workerId) {
        this.actionKey = actionKey;
        this.syncElement = syncElement;
        this.action = action;
        this.workerId = workerId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getActionKey() {
        return actionKey;
    }

    public void setActionKey(String actionKey) {
        this.actionKey = actionKey;
    }

    public Constants.SyncElement getSyncElement() {
        return syncElement;
    }

    public void setSyncElement(Constants.SyncElement syncElement) {
        this.syncElement = syncElement;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
