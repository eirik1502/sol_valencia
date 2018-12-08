package game.load_attributes.characters;

import engine.WorldContainer;
import engine.audio.Sound;
import engine.combat.abilities.Ability;
import engine.combat.abilities.ProjectileAbility;
import game.ProjectileUtils;
import game.load_attributes.CharacterAttribs;
import game.load_attributes.MeleeAbilityAttribs;
import game.load_attributes.ProjectileAbilityAttribs;
import utils.maths.Vec4;

/**
 * Created by eirik on 06.12.2018.
 */
public class CharacterMagnetAttribs extends CharacterAttribs {


    public CharacterMagnetAttribs() {
        characterName = "magnet";

        charImage1 = "charMagnetBlue";
        charImage2 = "charMagnetRed";

        radius = 36;
        moveAccel = 2000;



        abilityAttribs.add(new MeleeAbilityAttribs() {
            {
                //meta
                abilityName = "spear poke";

                //media
                abilityImage = "";
                abilityColor = new Vec4(0, 1, 0, 1); //color is overwritten with the image if imageName is present
                startupSound = "charMagnetSpearPokeStart";
                onHitSound = "charMagnetSpearPokeHit";

                //placement
                radius = 20f;
                distanceFromChar = 128f;
                activeHitboxTime = 6;

                //duration
                startupTime = 6;
                endlagTime = 6;
                rechargeTime = 13;

                //damage
                damage = 150f;
                baseKnockback = 600f;
                knockbackRatio = 0.7f;
                knockbackPoint = -100f;
                knockbackTowardPoint = false;
            }
        });

        abilityAttribs.add(new ProjectileAbilityAttribs() {
            {
                //meta
                abilityName = "spear throw";

                //media
                abilityImage = "charMagnetSpear";
                abilityColor = null;
                startupSound = "charMagnetSpearThrowStart";
                onHitSound = "charMagnetSpearThrowHit";

                //placement
                radius = 32f;
                startSpeed = 1500;
                lifeTime = 90;

                //duration
                startupTime = 30;
                endlagTime = 18;
                rechargeTime = 50;

                //damage
                damage = 400f;
                baseKnockback = 800f;
                knockbackRatio = 2f;
                knockbackPoint = -32f;
                knockbackTowardPoint = false;
            }
        });

        abilityAttribs.add(new ProjectileAbilityAttribs() {
            {
                //meta
                abilityName = "lion dance";

                //media
                abilityImage = "charMagnetLion";
                abilityColor = null;
                startupSound = "charMagnetLionDanceStart";
                onHitSound = "";

                //placement
                radius = 64f;
                startSpeed = 400;
                lifeTime = 40;

                //duration
                startupTime = 12;
                endlagTime = 12;
                rechargeTime = 50;

                //damage
                damage = 600f;
                baseKnockback = 800f;
                knockbackRatio = 0.5f;
                knockbackPoint = 0;
                knockbackTowardPoint = false;
            }
        });
    }
}
