package engine.visualEffect;

import engine.Sys;
import engine.WorldContainer;
import engine.graphics.ColoredMesh;
import engine.graphics.RenderSys;
import engine.graphics.TexturedMesh;
import engine.graphics.view_.View;
import utils.maths.Mat4;
import utils.maths.Vec2;
import utils.maths.Vec3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by eirik on 06.07.2017.
 */
public class VisualEffectSys implements Sys{

    private WorldContainer wc;



    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        //get new effects to run
        for (int entity : wc.getEntitiesWithComponentType(VisualEffectComp.class)) {
            VisualEffectComp veComp = (VisualEffectComp)wc.getComponent(entity, VisualEffectComp.class);

            updateVisualEffectComp(entity, veComp);
        }

    }

    @Override
    public void terminate() {

    }

    private void updateVisualEffectComp(int entity, VisualEffectComp  veComp) {
        //accept new effect requests
        if (veComp.requestEffectId != -1) {
            //abort older effects
            if (veComp.runningEffectId != -1) {
                //remove old effect
                veComp.effects.get(veComp.runningEffectId).endEffect();

                System.err.println("Removing old effect\nrequesting effect: " + veComp.requestEffectId
                +"\nrunning effect: "+veComp.runningEffectId);
            }

            //set new effect to the current
            veComp.runningEffectId = veComp.requestEffectId;
            veComp.requestEffectId = -1;

            //actually start effect
            veComp.startEffect(veComp.runningEffectId, veComp.requestEffectPos);
        }

        //return if there are no running effects
        if (veComp.runningEffectId == -1) return;

        //update the currently running effect
        VisualEffect runningEffect = veComp.effects.get(veComp.runningEffectId);

        //update visual effect by progressing particles
        //if it returns false, the effect is over
        if ( !updateVisualEffect(runningEffect) ){
            veComp.runningEffectId = -1;
        }

    }


    private boolean updateVisualEffect(VisualEffect effect) {
        //end effect if it times out
        effect.decrementLifetime();
        if (effect.getLifetime() <= 0) {
            effect.endEffect();

            //return false to indicate that effect is over
            return false;
//            removeEffects.add(effect);
        }

        //update each particle
        effect.activeParticleStream().forEach(p -> {
            updateParticle(p);
        });

//        System.out.println("Visual effect lifetime= "+effect.getLifetime());
        return true;
    }

    private void updateParticle(Particle p) {
        p.addPos( p.getVelocity() );
//        System.out.println("Updating particle, pos="+p.getPos());

        p.decrementLifetime();
        if (p.getLifetime() <= 0) {
            p.deactivate();

        }
    }

}
