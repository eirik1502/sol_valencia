package engine.network.client;

import engine.Component;
import engine.network.CharacterStateData;

/**
 * Created by eirik on 27.06.2017.
 */
public class InterpolationComp implements Component {


    //private int interpolSpeed;

    private int lastFrame = 0;
    private CharacterStateData lastFrameState = new CharacterStateData();

    private int nextFrame = 0;
    private CharacterStateData nextFrameState = new CharacterStateData();

    private int newFrame = 0;
    private CharacterStateData newFrameState = null;

    //int interpFrameCount;

    private int currentInterpFrame = 0;



    public void addFrame(int frame, CharacterStateData state) {
//        newFrame = frame;
//        newFrameState = state;
        //next becomes last state
        lastFrame = nextFrame;
        lastFrameState = nextFrameState;

        nextFrame = frame;
        nextFrameState = state;
    }

    public boolean reachedNextFrame() {
//        if (frame <= nextFrame) return;

        if (newFrameState == null) return false;

        //next becomes last state
        lastFrame = nextFrame;
        lastFrameState = nextFrameState;

        nextFrame = newFrame;
        nextFrameState = newFrameState;

        newFrameState = null;
        return true;
    }



    public int getLastFrame() {
        return lastFrame;
    }

    public void setLastFrame(int lastFrame) {
        this.lastFrame = lastFrame;
    }

    public CharacterStateData getLastFrameState() {
        return lastFrameState;
    }

    public void setLastFrameState(CharacterStateData lastFrameState) {
        this.lastFrameState = lastFrameState;
    }

    public int getNextFrame() {
        return nextFrame;
    }

    public void setNextFrame(int nextFrame) {
        this.nextFrame = nextFrame;
    }

    public CharacterStateData getNextFrameState() {
        return nextFrameState;
    }

    public void setNextFrameState(CharacterStateData nextFrameState) {
        this.nextFrameState = nextFrameState;
    }

    public int getCurrentInterpFrame() {
        return currentInterpFrame;
    }

//    public void setCurrentInterpFrame(int currentInterpFrame) {
//        this.currentInterpFrame = currentInterpFrame;
//    }
    public void incrementCurrentInterpFrame() {
        currentInterpFrame++;
    }

    public String toString() {
        return "lastFrame="+lastFrame+" lastFrameState="+lastFrameState+" nextFrame="+nextFrame+" nextFrameState"+nextFrameState;
    }
}
