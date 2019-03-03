package game;

import engine.WorldContainer;
import engine.audio.Sound;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.MeleeAbility;
import engine.combat.abilities.ProjectileAbility;
import engine.physics.Circle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BrailUtils implements SpecificCharacterUtils {
    @Override
    public void addAbilityComps(WorldContainer wc, int entity, boolean media) {
        float abChaggerRadius = 64f;

        int lightForceEntity, chaggerProjEntity, mergeEntity;

        chaggerProjEntity = wc.createEntity("brailChagger");
        ProjectileUtils.addProjectileCoreComps(wc, chaggerProjEntity, abChaggerRadius);

        if (media) {
            float[] purple = {1.0f, 0f, 1.0f};
            Sound sndHit = new Sound("audio/laser_hit.ogg");
            ProjectileUtils.addProjectilesMediaColorComps(wc, chaggerProjEntity, abChaggerRadius, purple, sndHit);

        }

        //lightForce
        lightForceEntity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(70f), null, media);
        MeleeAbility ab1 = new MeleeAbility(wc, 0, lightForceEntity, 6, 6, 6, 30,64.0f);
        ab1.setDamagerValues(wc, 150, 600, 1.2f, 400f, true);

        //chagger
        ProjectileAbility ab2 = new ProjectileAbility(wc, 1, chaggerProjEntity, 10, 6, 120, 650, 40);
        ab2.setDamagerValues(wc, 300, 400, 0.8f, 64, false);

        //merge
        mergeEntity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(160f), null, media);
        MeleeAbility ab3 = new MeleeAbility(wc, 2, mergeEntity, 10, 2, 8, 60, 128);
        ab3.setDamagerValues(wc, 20, 800, 0.4f, 0, true);

        //add the component
        wc.addComponent(entity, new AbilityComp(ab1, ab2, ab3));
    }

    @Override
    public List<Sound> createCharacterSounds() {
        return Arrays.asList(
                new Sound("audio/click4.ogg"),
                new Sound("audio/laser02.ogg"),
                new Sound("audio/boom-bang.ogg")
        );
    }
}