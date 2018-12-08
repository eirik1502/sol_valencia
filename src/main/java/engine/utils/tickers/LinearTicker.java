package engine.utils.tickers;

import engine.UserInput;
import engine.utils.DeltaTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 26.11.2018.
 */
public class LinearTicker implements Ticker {

    private boolean running;

    private float tickTime;
    private Tickable listener;

    private DeltaTimer deltaTimer;


    //store runtime data
    public List<Float> deltaTimes = new ArrayList<>(1000);
    public List<Float> timeBetweenUpdates = new ArrayList<>(1000);
    public List<Integer> iterationsBetweenUpdates = new ArrayList<>(1000);


    public LinearTicker(float tickTime) {
        this(tickTime, null);
    }
    public LinearTicker(float tickTime, Tickable listener) {
        this.tickTime = tickTime;
        this.listener = listener;

        this.deltaTimer = new DeltaTimer();
        this.running = false;
    }

    @Override
    public void setListener(Tickable listener) {
        this.listener = listener;
    }


    @Override
    public void start() {
        running = true;
        this.deltaTimer.setTime();

        float deltaTime = 0;
        float timeSinceUpdate = 0;
        int iterationsBetweenUpdate = 0;

        while (running) {
            float newTime = deltaTimer.deltaTime();
            deltaTime += newTime;
            timeSinceUpdate += newTime;

            if (deltaTime >= tickTime) {

                listener.onTick(timeSinceUpdate);

                deltaTimes.add(deltaTime);
                timeBetweenUpdates.add(timeSinceUpdate);
                iterationsBetweenUpdates.add(iterationsBetweenUpdate);

                deltaTime -= tickTime;
                timeSinceUpdate = 0;
                iterationsBetweenUpdate = 0;

//                if (deltaTime > tickTime) {
//                    deltaTime = 0;
//                }
            } else {
                ++iterationsBetweenUpdate;

            }
        }
    }

    @Override
    public void stop() {
        running = false;
    }

}
