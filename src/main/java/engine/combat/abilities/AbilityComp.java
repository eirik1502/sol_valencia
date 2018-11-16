package engine.combat.abilities;

import engine.Component;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by eirik on 19.06.2017.
 */
public class AbilityComp implements Component {

    private static final int EXPECTED_ABILITY_COUNT = 4;
    private static final int EXPECTED_MELEE_ABILITIES = 2;
    private static final int EXPECTED_PROJECTILE_ABILITIES = 2;

    private List<Ability> abilities = new ArrayList<>(EXPECTED_ABILITY_COUNT);
//    private List<MeleeAbility> meleeAbilities = new ArrayList<>(EXPECTED_MELEE_ABILITIES);
//    private List<ProjectileAbility> projectileAbilities = new ArrayList<>(EXPECTED_PROJECTILE_ABILITIES);

    private Ability occupiedBy;

    private boolean abortExecution = false;

    private int newExecuted = -1; //to be used by network gamestate read


    public AbilityComp(Ability... abilities) {
        Arrays.asList(abilities).forEach(a -> {
            addAbility(a);
        });
    }


    public void addAbility(Ability ability){
        ability.setAbilityId(abilities.size());
        abilities.add(ability);

    }

    public boolean hasNewExecuting() {
        return newExecuted != -1;
    }
    public int popNewExecuting() {
        int n = newExecuted;
        newExecuted = -1;
        return n;
    }

    public Stream<Ability> streamAbilities(){
        return abilities.stream();
    }

    public Ability getOccupiedBy(){
        return occupiedBy;
    }


    public void requestExecution(int meleeId) {
        abilities.get(meleeId).requestExecution();
    }

    public void abortExecution() {
        abortExecution = true;
    }
    public void forceExecution(int meleeId) {
        abortExecution();
        requestExecution(meleeId);
    }

    public void setOccupiedBy(Ability a) {
        this.occupiedBy = a;
        if (a != null) {
            this.newExecuted = a.getAbilityId();
        }
    }

    boolean isAbortExecution() {
        return abortExecution;
    }
    void resetAbortExecution() {
        abortExecution = false;
    }
}
