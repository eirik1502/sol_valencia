package game.offline;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;

/**
 * Using a movementInputComp to update the position of an entity
 *
 * Created by eirik on 16.11.2018.
 */
public class MovementControlSys implements Sys {

    private WorldContainer wc;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {
        wc.entitiesOfComponentTypeStream(MovementControlComp.class).forEach(e -> {
            MovementControlComp moveCtrlComp = wc.getComponent(e, MovementControlComp.class);
            MovementInputComp moveInpComp = wc.getComponent(e, MovementInputComp.class);
            PositionComp posComp = wc.getComponent(e, PositionComp.class);

            posComp.addX(moveInpComp.xAxis * moveCtrlComp.speed);
            posComp.addY(moveInpComp.yAxis * moveCtrlComp.speed);
        });
    }

    @Override
    public void terminate() {

    }
}
