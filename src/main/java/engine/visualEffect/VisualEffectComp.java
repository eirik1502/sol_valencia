package engine.visualEffect;

import engine.Component;
import org.lwjgl.system.linux.Visual;
import utils.maths.Vec2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by eirik on 06.07.2017.
 */
public class VisualEffectComp implements Component {


    public List<VisualEffect> effects;

//    private LinkedList<VisualEffect> runEffects = new LinkedList<>();

    public int requestEffectId = -1;
    public Vec2 requestEffectPos = new Vec2();

    public int runningEffectId = -1;


    public VisualEffectComp(List<VisualEffect> effects) {
        this.effects = effects;
    }

    public VisualEffectComp(VisualEffect... effects) {
        this.effects = new ArrayList<>(Arrays.asList(effects));
    }

    /**
     * Effect playes until it times out
     * @param effectId
     */
    public void requestEffect(int effectId, Vec2 pos) {
        requestEffectId = effectId;
        requestEffectPos = pos;
    }
    void startEffect(int effectId, Vec2 pos) {
        VisualEffect effect = effects.get(effectId);

        effect.startEffect(pos);

        //request effect

//        runEffects.add(effect);
    }

//    public boolean hasEffectsToStart() {
//        return !runEffects.isEmpty();
//    }
//    public VisualEffect popVisualEffect() {
//        return runEffects.poll();
//    }

}
