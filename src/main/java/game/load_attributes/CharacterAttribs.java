package game.load_attributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 06.12.2018.
 */
public abstract class CharacterAttribs {
    public String characterName;

    public String charImage1;
    public String charImage2;

    public float radius;
    public float moveAccel;

    public List<AbilityAttribs> abilityAttribs = new ArrayList<>(3);
}
