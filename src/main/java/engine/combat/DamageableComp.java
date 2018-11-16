package engine.combat;

import engine.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by eirik on 19.06.2017.
 */
public class DamageableComp implements Component {

    private float damaged = 0;

    private int stunTimer = 0;

    private boolean interrupted = false;
    private List<HitData> frameHitData = new ArrayList<>();


    public float getDamage() {
        return damaged;
    }
    public void applyDamage(float dmg) {
        damaged += dmg;
    }

    /**
     * Set an interrupt flag that can be used to abort abilities
     */
    public void interrupt() {
        interrupted = true;
    }
    public boolean isInterrupted() {
        return interrupted;
    }


    public boolean isStunned() {
        return stunTimer > 0;
    }
    void decrementStunTimer() {
        stunTimer--;
    }
    int getStunTimer() {
        return stunTimer;
    }
    void setStunTimer(int time) {
        stunTimer = time;
    }

    void addHitData(HitData data) {
        frameHitData.add(data);
    }
    public Stream<HitData> hitDataStream() {
        return frameHitData.stream();
    }


    void resetFrame() {
        resetInterrupt();
        resetHitData();
    }

    void resetInterrupt() {
        interrupted = false;
    }

    void resetHitData() {
        frameHitData.clear();
    }

    public void reset() {
        damaged = 0;
        stunTimer = 0;
    }
}
