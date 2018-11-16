package engine.network.client;

import engine.PositionComp;
import engine.RotationComp;
import engine.Sys;
import engine.WorldContainer;
import engine.network.NetworkUtils;
import game.GameUtils;
import utils.maths.TrigUtils;
import utils.maths.Vec2;

/**
 * Created by eirik on 27.06.2017.
 */
public class InterpolationSys implements Sys{

    private WorldContainer wc;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        for (int entity : wc.getEntitiesWithComponentType(InterpolationComp.class)) {

            InterpolationComp interpComp = (InterpolationComp)wc.getComponent(entity, InterpolationComp.class);
            PositionComp posComp = (PositionComp)wc.getComponent(entity, PositionComp.class);
            RotationComp rotComp = (RotationComp)wc.getComponent(entity, RotationComp.class);

            float interpolationRatio = 1.0f/ (1.0f + NetworkUtils.CLIENT_INTERPOLATION_FRAME_COUNT);

            //calculate the vector from current character position to next state pos
            Vec2 interpVelocity = interpComp.getNextFrameState().getPos().subtract( posComp.getPos() );

            //scale the length by the number of frames to interpolate over
            interpVelocity = interpVelocity.scale(interpolationRatio);

            //apply one frame interpolation
            posComp.addPos(interpVelocity);


            //interpolate angle
            float diffAngle = TrigUtils.shortesAngleBetween(rotComp.getAngle(), interpComp.getNextFrameState().getRotation());
            rotComp.addAngle(diffAngle * interpolationRatio);


//            posComp.setPos(interpComp.getNextFrameState().getPos()); //just sets position to newest state
//            rotComp.setAngle(interpComp.getNextFrameState().getRotation());
        }
    }

    @Override
    public void terminate() {

    }
}
