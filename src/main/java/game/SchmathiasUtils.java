package game;

import engine.WorldContainer;
import engine.audio.Sound;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.MeleeAbility;
import engine.combat.abilities.ProjectileAbility;
import engine.physics.Circle;

import java.util.Arrays;
import java.util.List;

public class SchmathiasUtils implements SpecificCharacterUtils {
    @Override
    public void addAbilityComps(WorldContainer wc, int entity, boolean media) {
        float hookProjRadius = 24f;

        int frogpunchEntity, hookProjEntity, meteorpunchEntity;

        hookProjEntity = wc.createEntity("schmathiasHook");

        ProjectileUtils.addProjectileCoreComps(wc, hookProjEntity, hookProjRadius);

        if (media) {
            Sound hookHitSnd = new Sound("audio/hook_hit.ogg");

            //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
            ProjectileUtils.addProjectilesMediaTextureComps(wc, hookProjEntity, hookProjRadius, "hook.png", 256/2, 512, 256, hookHitSnd);
        }


        //frogpunch
        int melee1Entity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(64f), null, media);
        MeleeAbility abFrogpunch = new MeleeAbility(wc, 0, melee1Entity, 3, 5, 3, 20,48.0f);
        abFrogpunch.setDamagerValues(wc, 150, 700, 0.8f, -48f, false);

        //hook
        ProjectileAbility abHook = new ProjectileAbility(wc, 1, hookProjEntity, 5, 18, 50, 900, 30);
        abHook.setDamagerValues(wc, 200f, 1400f, 0.2f, -128, true);

        //meteorpunch
        int melee2Entity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(32f), null, media);
        MeleeAbility abMeteorpunch = new MeleeAbility(wc, 2, melee2Entity, 15, 3, 4, 60, 64);
        abMeteorpunch.setDamagerValues(wc, 500, 1000, 1.5f, -128f, false);

        //add ability comp
        wc.addComponent(entity, new AbilityComp(abFrogpunch, abHook, abMeteorpunch));
    }

    @Override
    public List<Sound> createCharacterSounds() {
        return Arrays.asList(
                new Sound("audio/boom-kick.ogg"),
                new Sound("audio/hook_init.ogg"),
                new Sound("audio/boom-kick.ogg")
        );
    }
}
