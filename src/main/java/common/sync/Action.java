package common.sync;

public enum Action {

    REGISTER, // register new instance to container
    UNREGISTER, // remove  instance from container
    UPDATE, // for sync, trigger mock event to another nodes
    INITIALIZE; // for node init.

}
