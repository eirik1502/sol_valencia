package engine.ecs_network;

import engine.Component;
import engine.network_new.NetPackage;

/**
 * Descibes how a component should be networked
 *
 * Created by eirik on 14.12.2018.
 */
public interface NetCompMapper<T extends Component> {
    NetPackage getNetPacket(T comp);
}
