package game.offline;

import engine.Sys;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics_module.GraphicsModule;

/**
 * Created by eirik on 16.11.2018.
 */
public class UserMovementInputSys implements Sys {

    private WorldContainer wc;
    private UserInput userInput;


    public UserMovementInputSys() {
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
        this.userInput = wc.getGame().getModule(GraphicsModule.class).getUserInput();
    }

    @Override
    public void update() {
        wc.entitiesOfComponentTypeStream(UserMovementInputComp.class).forEach(e -> {
            MovementInputComp moveInpComp = wc.getComponent(e, MovementInputComp.class);

            float xAxis = userInput.getMouseX() - wc.getView().getWidth()/2;
            float yAxis = userInput.getMouseY() - wc.getView().getHeight()/2;
            moveInpComp.xAxis = xAxis / wc.getView().getWidth();
            moveInpComp.yAxis = yAxis / wc.getView().getHeight();

        });
    }

    @Override
    public void terminate() {

    }
}
