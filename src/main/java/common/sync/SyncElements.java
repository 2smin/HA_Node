package common.sync;

public abstract class SyncElements {

    private String actionKey;

    public String getActionKey() {
        return actionKey;
    }

    public void setActionKey(String actionKey) {
        this.actionKey = actionKey;
    }

    public abstract void doSync(String actionKey, Action action);
}
