package engine.ecs_network;

import engine.PositionComp;
import engine.network_new.NetPackage;

/**
 * Created by eirik on 14.12.2018.
 */
public class PositionCompMapper implements NetCompMapper<PositionComp> {
    @Override
    public NetPackage getNetPacket(PositionComp comp) {
        return null;
    }
}
