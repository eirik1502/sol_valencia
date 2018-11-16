package engine.physics;

import engine.Component;

/**
 * Created by haraldvinje on 20-Jun-17.
 */
public class AffectedByHoleComp implements Component {


    private boolean holeAffectedFlag = false;


    public boolean isHoleAffectedFlag() {
        return holeAffectedFlag;
    }

    public void setHoleAffectedFlag() {
        holeAffectedFlag = true;
    }
    public void resetHoleAffectedFlag() {
        this.holeAffectedFlag = false;
    }
}
