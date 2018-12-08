package game;

import engine.WorldContainer;
import engine.audio.Sound;
import engine.combat.abilities.Ability;
import engine.combat.abilities.MeleeAbility;
import engine.combat.abilities.ProjectileAbility;
import engine.physics.Circle;
import game.load_attributes.AbilityAttribs;
import game.load_attributes.MeleeAbilityAttribs;
import game.load_attributes.ProjectileAbilityAttribs;

import java.util.List;

/**
 * Created by eirik on 06.12.2018.
 */
public class CharacterAbilityUtils {


    public static List<Ability> parseApilities(WorldContainer wc, List<AbilityAttribs> abilitiesAttribs) {
        return null;
    }
    public static Ability parseApility(WorldContainer wc, AbilityAttribs abilityAttrib) {

        if (abilityAttrib instanceof MeleeAbilityAttribs) {

            int melee1Entity = MeleeAbilityUtils.allocateHitboxEntity(wc, new Circle(20f), null, true);
            MeleeAbility ab1 = new MeleeAbility(wc, 0, melee1Entity, 6, 6, 6, 13,128.0f);
            ab1.setDamagerValues(wc, 150, 600, 0.7f, -100f, false);
            return ab1;

        } else if (abilityAttrib instanceof ProjectileAbilityAttribs) {

            int spearProj = ProjectileUtils.allocateImageProjectileEntity(wc, "magnet_spear.png", 48, 536, 32*2, 32, new Sound("audio/arrow_impact.ogg")); //both knockback angle and image angle depends on rotation comp. Cheat by setting rediusOnImage negative
            ProjectileAbility ab2 = new ProjectileAbility(wc, 0, spearProj, 30, 18, 50, 1500, 90);
            ab2.setDamagerValues(wc, 400f, 800f, 2f, -32f, false);
            return ab2;

        } else {
            System.err.println("cannot parse the given ability, not a melee or projectile ability");
        }
        return null;
    }

}
