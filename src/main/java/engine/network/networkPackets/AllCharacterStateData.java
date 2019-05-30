package engine.network.networkPackets;

import engine.network.NetworkUtils;
import utils.StringUtils;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by eirik on 21.06.2017.
 */
public class AllCharacterStateData implements NetworkPacket{

//    public static final int BYTES = Integer.BYTES + Float.BYTES*3 * NetworkUtils.CHARACTER_NUMB;

    private int frameNumber;

    private float[] x,
                    y,
                    rotation;


    public AllCharacterStateData() {
        x = new float[NetworkUtils.CHARACTER_COUNT];
        y = new float[NetworkUtils.CHARACTER_COUNT];
        rotation = new float[NetworkUtils.CHARACTER_COUNT];

    }


    public int getFrameNumber() {
        return frameNumber;
    }
    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public float getX(int charNumb) {
        return x[charNumb];
    }

    public void setX(int charNumb, float x) {
        this.x[charNumb] = x;
    }

    public float getY(int charNumb) {
        return y[charNumb];
    }

    public void setY(int charNumb, float y) {
        this.y[charNumb] = y;
    }

    public float getRotation(int charNumb) {
        return rotation[charNumb];
    }

    public void setRotation(int charNumb, float rotation) {
        this.rotation[charNumb] = rotation;
    }

    @Override
    public String toString() {
        return "[AllCharacterStateData: x="+ Arrays.toString(x)+" y="+Arrays.toString(y)+" rotation="+Arrays.toString(rotation)+"]";
    }

    @Override
    public int getPacketId() {
        return NetworkUtils.SERVER_CHARACTER_STATE_ID;
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, x.length).forEach(i -> sb.append(x[i]).append(','));
        sb.append(' ');
        IntStream.range(0, y.length).forEach(i -> sb.append(y[i]).append(','));
        sb.append(' ');
        IntStream.range(0, rotation.length).forEach(i -> sb.append(rotation[i]).append(','));

        return sb.toString();
    }
}
