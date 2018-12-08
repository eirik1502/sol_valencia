package game.offline;

import engine.UserInput;
import engine.utils.DeltaTimer;
import game.chart.Plotter;
import game.chart.StaticDataset;
import game.web_socket.WebSocketServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 03.12.2018.
 */
public class DevGame extends Game{

    private float accumulatedTime = 0;

    private boolean devMode = false;

    private DeltaTimer updateTimer = new DeltaTimer(),
            roundtripTimer = new DeltaTimer();

    private List<Float> updateTimes = new ArrayList<>(1000);
    private List<Float> roundtripTimes = new ArrayList<>(1000);
    private List<Float> pollEventTimes = new ArrayList<>(1000);

    //WebSocket for communicating stats to control panel clients
    private int controlServerPort = 4444;
    private WebSocketServer controlServer;

    private boolean controlClientConnected = false;



    public void init() {
        super.init();

        controlServer = new WebSocketServer(controlServerPort);
    }

    public void start() {
        controlServer.start();

        //wait for the control panel client to connect to the server.
        controlServer.setOnClientConnected((conn, handshake) -> {
            this.controlClientConnected = true;
        });
        while(!this.controlClientConnected) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //will block
        super.start();
    }

    private boolean keyRightLastFrame = false;
    private boolean keyEnterPressedLastFrame = false;

    //do not call super as we will override everything
    @Override
    public void update(float deltaTime) {
        roundtripTimer.setTime();

        //poll window events
        super.pollEvents();

        //performance measures
        float pollEventTime = roundtripTimer.getTime();
        float updateTime = 0;
        float roundtripTime = 0;

        accumulatedTime += deltaTime;
        //System.out.println("Delta time " + deltaTime);
        //System.out.println("Accumulated time: " + accumulatedTime);


        boolean shouldUpdate = checkShouldUpdateEngine();

        //do update
        if (shouldUpdate) {
            updateTimer.setTime();
            wc.updateSystems();
            updateTime = updateTimer.deltaTime();
            updateTimes.add(updateTime);

        } else {
            updateTime = 0;
        }

        enterEditModeIfRequested();

        super.stopIfRequested();

        //This will happen many times while you hold in r
        //should remove existing entities before loading new
        if (userInput.isKeyboardPressed(UserInput.KEY_R)) {
            createConfigEntities();
            wc.printEntities();
        }

        //performance measures handeling
        roundtripTime = roundtripTimer.deltaTime();

        pollEventTimes.add(pollEventTime);
        updateTimes.add(updateTime);
        roundtripTimes.add(roundtripTime);

        //communicate measures to control client
        String data = String.format("{\"timePassed\":%f, \"pollEventTime\":%f, \"updateTime\":%f, \"roundtripTime\":%f}",
                deltaTime, pollEventTime, updateTime, roundtripTime);
        controlServer.send(data);
    }

    protected void enterEditModeIfRequested() {
        //enter edit mode if key pressed
        if (userInput.isKeyboardPressed(UserInput.KEY_ENTER)) {
            if (!keyEnterPressedLastFrame) {
                devMode = !devMode;
            }
            keyEnterPressedLastFrame = true;
        } else keyEnterPressedLastFrame = false;
    }

    protected boolean checkShouldUpdateEngine() {
        boolean doUpdate = false;

        if (devMode) {
            if (userInput.isKeyboardPressed(UserInput.KEY_LEFT)) {
                doUpdate= true;
            }
            if (userInput.isKeyboardPressed(UserInput.KEY_RIGHT)) {
                if (!keyRightLastFrame) {
                    doUpdate = true;
                }
                keyRightLastFrame = true;
            } else {
                keyRightLastFrame = false;
            }
        }
        else {

            doUpdate = true;
        }
        return doUpdate;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        controlServer.stop();

        //something creates a large value for the last pollEvents call.
        //maybe the window.close()
        //.. so we remove it
        pollEventTimes.remove(pollEventTimes.size()-1);
        roundtripTimes.remove(roundtripTimes.size()-1);

        Plotter plotter = new Plotter("Linear ticker");
        StaticDataset d = new StaticDataset("data");
        d.addSeries("Ticker delta times", ticker.deltaTimes);
        d.addSeries("Ticker time between updates", ticker.timeBetweenUpdates);
        d.addSeries("Update times", updateTimes);
        d.addSeries("Roundtrip times", roundtripTimes);
        d.addSeries("Poll events times", pollEventTimes);
        plotter.addDataset(d);

        //plotter.create();
    }
}
