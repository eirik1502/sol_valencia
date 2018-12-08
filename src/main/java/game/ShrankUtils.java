package game;

import engine.WorldContainer;
import engine.audio.Sound;
import engine.combat.abilities.AbilityComp;
import engine.combat.abilities.MeleeAbility;
import engine.combat.abilities.ProjectileAbility;
import engine.physics.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 08.12.2018.
 */
public class ShrankUtils implements SpecificCharacterUtils{

    public void addAbilityComps(WorldContainer wc, int entity, boolean media) {
        Sound sndBoom = null;
        Sound sndHit = null;
        float proj1Radius = 8;
        float proj2Radius = 20;

        int proj1Entity = wc.createEntity("shrankRapidShot");
        int proj2Entity = wc.createEntity("shrankHyperbeam");
        ProjectileUtils.addProjectileCoreComps(wc, proj1Entity, proj1Radius);
        ProjectileUtils.addProjectileCoreComps(wc, proj2Entity, proj2Radius);

        if (media) {
            float[] proj1Color = {1, 1, 0};
            float[] proj2Color = {1, 0, 1};

            sndBoom = new Sound("audio/boom-bang.ogg");
            sndHit = new Sound("audio/laser_hit.ogg");

            ProjectileUtils.addProjectilesMediaColorComps(wc, proj1Entity, proj1Radius, proj1Color, sndBoom);
            ProjectileUtils.addProjectilesMediaColorComps(wc, proj2Entity, proj2Radius, proj2Color, sndHit);
        }

        int rapidShotAbilityIndex = 0;
        int powershotAbilityIndex = 1;
        int pufferAbilityIndex = 2;

        //rapidshot
        ProjectileAbility abRapidshot = new ProjectileAbility(wc, rapidShotAbilityIndex, proj1Entity, 2, 2, 30, 1200, 30 );
        abRapidshot.setDamagerValues(wc, 100, 180, 0.5f, -128, false);

        //hyperbeam3
        ProjectileAbility abHyperbeam = new ProjectileAbility(wc, powershotAbilityIndex, proj2Entity, 15, 10, 120, 1500, 120);
        abHyperbeam.setDamagerValues( wc, 350,900, 1.1f, -256, false);

        //puffer
        int melee1Entity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(98f), sndBoom, media); //old rad = 128. Old base knockback = 900
        MeleeAbility abPuffer = new MeleeAbility(wc, pufferAbilityIndex, melee1Entity, 8, 2, 8, 60*3, 0f);
        abPuffer.setDamagerValues(wc, 20, 1200f, 0.1f, 0f, false);

        wc.addComponent(entity, new AbilityComp(abRapidshot, abHyperbeam, abPuffer));
    }

    public List<Sound> createCharacterSounds() {
        Sound sndPowershot = new Sound("audio/powershot.ogg");
        Sound sndBoom = new Sound ("audio/boom-bang.ogg");
        Sound sndRapidsShot = new Sound("audio/click4.ogg");

        List<Sound> soundList = new ArrayList<Sound>();
        soundList.add(sndRapidsShot);
        soundList.add(sndPowershot);
        soundList.add(sndBoom);
        return soundList;
    }
}
