package engine.network_new;

import com.esotericsoftware.kryonet.Connection;

import java.util.Map;
import java.util.Set;

/**
 * Created by eirik on 13.12.2018.
 */
public class NetworkManager {

    private boolean log = false;
    private boolean listeningConnections = false;

    //connections where a connection package has not yet been received
    private Set<Connection> connectionsPending;

    private Map<Host, InternalHostData> connectedHostsData;
    private Map<Integer, Host> connectedHostsById;


    public void assignPackageClasses(Class<? extends NetPackage> packageClass) {

    }

    public void addConnectionListener(int port, ConnectionListener connectionListener) {
        if (!listeningConnections) {
            //start server
            listeningConnections = true;
        }

        //add connection
    }

    /**
     *
     * @param address
     * @param port
     * @return a Host representing the server, null if the connection couldn't be made
     */
    public Host connectToServer(String address, int port, String myName) {

        return null;
    }

    void sendSafe(Host host, NetPackage pack) {

    }
    void sendFast(Host host, NetPackage pack) {

    }
}
