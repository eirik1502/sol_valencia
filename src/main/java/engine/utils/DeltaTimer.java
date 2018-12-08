package engine.utils;

/**
 * Created by eirik on 26.11.2018.
 */
public class DeltaTimer {

    private long lastTime;


    /**
     * Sets time point to now.
     * @return
     */
    public void setTime() {
        lastTime = System.nanoTime();
    }

    /**
     * Returns the delta time without reseting the time point.
     * @return
     */
    public float getTime() {
        long newTime = System.nanoTime();
        long deltaTime = newTime - lastTime;

        return nanoToSeconds(deltaTime);
    }

    /**
     * Retrievs the time passed since last time point.
     * This sets the timepoint to now.
     * @return
     */
    public float deltaTime() {
        long newTime = System.nanoTime();
        long deltaTime = newTime - lastTime;

        lastTime = newTime;

        return nanoToSeconds(deltaTime);
    }

    private float nanoToSeconds(long nanoSeconds) {
        double seconds = ((double)nanoSeconds) * 0.000000001;



        return (float) seconds;
    }

}
