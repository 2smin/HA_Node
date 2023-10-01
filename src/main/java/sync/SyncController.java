package sync;

import sync.Action;

public abstract class SyncController {

    private String actionKey;

    public String getActionKey() {
        return actionKey;
    }

    public void setActionKey(String actionKey) {
        this.actionKey = actionKey;
    }

    public abstract void doSync(String actionKey, Action action);
}
