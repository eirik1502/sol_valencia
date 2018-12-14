package engine.network_new;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 13.12.2018.
 */
public class Host {

    NetworkManager netManager;
    int id;
    String name;


    Host(NetworkManager netManager, int id) {
        this.netManager = netManager;
        this.id = id;
    }

    public void addDisconnectedListener(HostDisconnectListener disconnectListener) {

    }

    public void sendSafe(NetPackage pack) {

    }
    public void sendFast(NetPackage pack) {

    }
    public List<NetPackage> read() {
        return null;
    }
}
