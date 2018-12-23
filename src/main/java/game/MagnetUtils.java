package game;

import engine.WorldContainer;
import engine.audio.Sound;
import engine.combat.abilities.Ability;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.MeleeAbility;
import engine.combat.abilities.ProjectileAbility;
import engine.physics.Circle;

import java.util.ArrayList;
import java.util.List;

public class MagnetUtils implements SpecificCharacterUtils {

    public void addAbilityComps(WorldContainer wc, int entity, boolean media) {
        float projSpearRadius = 32;
        float projLionRadius = 64;

        int projSpearEntity = wc.createEntity("magnetSpearThrow");
        int projLionEntity = wc.createEntity("magnetLion");
        ProjectileUtils.addProjectileCoreComps(wc, projSpearEntity, projSpearRadius);
        ProjectileUtils.addProjectileCoreComps(wc, projLionEntity, projLionRadius);

        if (media) {
            Sound sndSpearHit = new Sound("audio/arrow_impact.ogg");
            Sound sndLionHit = new Sound("audio/hook_hit.ogg");

            ProjectileUtils.addProjectilesMediaTextureComps(wc, projSpearEntity, projSpearRadius, "magnet_spear.png", 48, 536, 32*2, sndSpearHit);
            ProjectileUtils.addProjectilesMediaTextureComps(wc, projLionEntity, projLionRadius, "masai_lion.png", 210/2, 435, 457, sndLionHit);
        }

        //spear poke
        int spearPokeEntity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(20f), null, media);
        MeleeAbility abSpearPoke = new MeleeAbility(wc, 0, spearPokeEntity, 6, 6, 6, 13,128.0f);
        abSpearPoke.setDamagerValues(wc, 150, 600, 0.7f, -100f, false);

        //spear throw
        ProjectileAbility abSpearThrow = new ProjectileAbility(wc, 1, projSpearEntity, 30, 18, 50, 1500, 90);
        abSpearThrow.setDamagerValues(wc, 400f, 800f, 2f, -32f, false);

        //lion
        ProjectileAbility abLion = new ProjectileAbility(wc, 2, projLionEntity, 12, 12, 50, 400, 40);
        abLion.setDamagerValues(wc, 600f, 800f, 0.5f, 0, false);

        wc.addComponent(entity, new AbilityComp(abSpearPoke, abSpearThrow, abLion));
    }


    public List<Sound> createCharacterSounds() {
        List<Sound> soundList = new ArrayList<Sound>();
        soundList.add(new Sound("audio/click4.ogg"));
        soundList.add(new Sound("audio/masai_arrow_throw.ogg"));
        soundList.add(new Sound("audio/lion-roar.ogg"));
        return soundList;
    }
}