package engine.ecs_network;

import engine.Component;

import java.util.Map;

/**
 * Created by eirik on 14.12.2018.
 */
public class NetOutputComp {

    //public Map<Class<? extends Component>, Class<? extends NetCompMapper>> componentMappers;

    public Map<ComponentNetPackage, Boolean> netComps;
}
