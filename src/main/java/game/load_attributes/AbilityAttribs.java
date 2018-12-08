package game.load_attributes;

import utils.maths.Vec4;

/**
 * Created by eirik on 06.12.2018.
 */
public abstract class AbilityAttribs {
    //meta
    public String abilityName;

    //media
    public String abilityImage;
    public Vec4 abilityColor; //color is overwritten with the image if imageName is present
    public String startupSound;
    public String onHitSound;

    //placement
    public float radius;

    //duration
    public int startupTime;
    public int endlagTime;
    public int rechargeTime;

    //damage
    public float damage;
    public float baseKnockback;
    public float knockbackRatio;
    public float knockbackPoint;
    public boolean knockbackTowardPoint;
}
