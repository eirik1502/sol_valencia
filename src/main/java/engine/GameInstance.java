package engine;

import game.offline.EntityClass;

import java.util.HashMap;
import java.util.Map;

public class GameInstance {

    private Map<Class<? extends EngineModule>, EngineModule> modules = new HashMap<>();
    private Map<String, EntityClass> entityClassesByName = new HashMap<>();


    private WorldContainer world;


    public void addModule(EngineModuleConfig moduleConfig) {
        EngineModule module = null;

        Class<? extends EngineModule> moduleType = moduleConfig.moduleType;

        try {
            module = moduleType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        modules.put(moduleType, module);
    }



    public void init() {
        world = new WorldContainer();
    }

    @SuppressWarnings("unchecked")
    public <T extends EngineModule> T getModule(Class<T> moduleType) {
        return (T)modules.get(moduleType);
    }

    public void instanciateEntity(String entityClassName) {
        entityClassesByName.get(entityClassName).instanciate(world);
    }
}
