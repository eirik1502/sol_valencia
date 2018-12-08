package game.offline;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import utils.maths.M;

/**
 * Created by eirik on 16.11.2018.
 */
public class PlayerControlBotSys implements Sys {
    private WorldContainer wc;


    private float xAxisOfTime(float time) {
        return M.cos(time/30);
    }
    private float yAxisOfTime(float time) {
        return M.sin(time/20);
    }
    private float _xAxisOfTime(float time) {
        return M.cos(time/30);
    }
    private float _yAxisOfTime(float time) {
        return M.sin(time/30);
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {
        wc.entitiesOfComponentTypeStream(PlayerControlBotComp.class).forEach(e -> {
            PlayerControlBotComp playerCtrlBotComp = wc.getComponent(e, PlayerControlBotComp.class);
            MovementInputComp moveInpComp = wc.getComponent(e, MovementInputComp.class);

            float time = playerCtrlBotComp.time;

            moveInpComp.xAxis = this.xAxisOfTime(time);
            moveInpComp.yAxis = this.yAxisOfTime(time);

            playerCtrlBotComp.time ++;
        });
    }

    @Override
    public void terminate() {

    }

}