package game.offline;

import engine.Component;
import engine.WorldContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityConstructor {

    private String entityClassName;
    private Map<Class<? extends Component>, ComponentConstructor> componentConstructors = new HashMap<>();

    public void addComponentConstructor(Class<? extends Component> compType, ComponentConstructor constructor) {
        componentConstructors.put(compType, constructor);
    }

    public void addComponentConstructors(Map<Class<? extends Component>, ComponentConstructor> constructors) {
        constructors.forEach(this::addComponentConstructor);
    }

    public EntityConstructor(String entityClassName) {
        this(entityClassName, new HashMap<>());
    }

    public EntityConstructor(String entityClassName, Map<Class<? extends Component>, ComponentConstructor> constructors) {
        this.entityClassName = entityClassName;
        addComponentConstructors(constructors);
    }


    public String getEntityClassName() {
        return entityClassName;
    }

    public int construct(WorldContainer world, int entity) {
        //get comps to construct and verify that they belong to the entity
        componentConstructors.entrySet().stream()
                .filter(entry -> {
                    boolean entityHasComp = world.hasComponent(entity, entry.getKey());

                    if (!entityHasComp)
                        System.err.println("EntityConstructor encountered component to be constructed that was not in the given entity" +
                                "\n\tentity: " + world.getEntityName(entity) + " component: " + entry.getKey().getName());

                    return entityHasComp;
                })
                .collect(Collectors.toMap(entry -> world.getComponent(entity, entry.getKey()), Map.Entry::getValue))
                //perform construction
                .forEach((comp, construct) -> construct.modifyInitially(comp));

        return entity;
    }

}
