package engine.combat.abilities;

import engine.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by eirik on 27.06.2017.
 */
public class HitboxComp implements Component {


    private int owner = -1;
    private Set<Integer> entityInteractions = new HashSet<>();


    public HitboxComp() {
    }

    public int getOwner() {
        return owner;
    }
    public void setOwner(int entity) {
        owner = entity;
    }

    Stream<Integer> streamEntityInteractions() {
        return entityInteractions.stream();
    }

    void addInteraction(int interactingEntity) {
        entityInteractions.add(interactingEntity);
    }


    public void reset() {
        owner = -1;
        entityInteractions.clear();
    }
}
