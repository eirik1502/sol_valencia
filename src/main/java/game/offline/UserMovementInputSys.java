package game.offline;

import engine.Sys;
import engine.UserInput;
import engine.WorldContainer;

/**
 * Created by eirik on 16.11.2018.
 */
public class UserMovementInputSys implements Sys {

    private WorldContainer wc;
    private UserInput userInput;


    public UserMovementInputSys(UserInput userInput) {
        this.userInput = userInput;
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
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
