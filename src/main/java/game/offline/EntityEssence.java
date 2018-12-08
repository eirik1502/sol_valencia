package game.offline;

import engine.Component;
import engine.WorldContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 23.11.2018.
 */
public class EntityEssence {

    public final String name;
    public final List<Component> components = new ArrayList<>();

    public EntityEssence(String name) {
        this.name = name;
    }
    public EntityEssence(String name, List<Component> components) {
        this.name = name;
        this.components.addAll(components);
    }


    public void instanciate(WorldContainer wc) {
        int e = wc.createEntity(name);
        components.forEach(c -> wc.addComponent(e, c));
    }
}
