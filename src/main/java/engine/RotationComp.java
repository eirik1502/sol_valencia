package engine;

import utils.maths.TrigUtils;

/**
 * Created by eirik on 15.06.2017.
 */
public class RotationComp implements Component {

    private float angle;


    public RotationComp() {

    }
    public RotationComp(float angle) {
        this.angle = angle;
    }


    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = TrigUtils.mapAngleToRange(angle);
    }

    public void addAngle(float angle) {
        setAngle(this.angle + angle);
    }
}
