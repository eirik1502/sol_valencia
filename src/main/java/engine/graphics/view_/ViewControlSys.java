package engine.graphics.view_;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;

/**
 * Created by eirik on 06.07.2017.
 */
public class ViewControlSys implements Sys {

    private WorldContainer wc;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        wc.entitiesOfComponentTypeStream(ViewControlComp.class).forEach(entity -> {
            ViewControlComp viewctrlComp = (ViewControlComp)wc.getComponent(entity, ViewControlComp.class);
            PositionComp posComp = (PositionComp)wc.getComponent(entity, PositionComp.class);

            View view = wc.getView();
            view.setX(posComp.getX() + viewctrlComp.getViewOffsetX());
            view.setY(posComp.getY() + viewctrlComp.getViewOffsetY());
        });
    }

    @Override
    public void terminate() {

    }
}
