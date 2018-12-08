package engine.audio;

import engine.Component;
import utils.maths.Vec2;
import utils.maths.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.openal.AL10.*;

/**
 * Created by haraldvinje on 26-Jul-17.
 */
public class AudioComp implements Component {

    public List<Sound> soundList = new ArrayList<Sound>();

    int sourcePointer;

    private int requestSoundIndex = -1;
    public boolean requestStopSource = false; //what are these for date 07.12.18
    public boolean backgroundAudio = false; //waht are these for


    public AudioComp(Sound... s){
        this(Arrays.asList(s), 20,50,300);
    }

    public AudioComp(Sound s, int rollOffFactor, int referenceDistance, int maxDistance){
        this.soundList.add(s);

        this.sourcePointer = alGenSources();
        setRollOffFactor(rollOffFactor);
        setReferenceDistance(referenceDistance);
        setMaxDistance(maxDistance);
    }




    public AudioComp(List<Sound> soundList, int rollOffFactor, int referenceDistance, int maxDistance){
        this.soundList = soundList;

        this.sourcePointer = alGenSources();
        setRollOffFactor(rollOffFactor);
        setReferenceDistance(referenceDistance);
        setMaxDistance(maxDistance);
    }

    public AudioComp(List<Sound> soundList){
        this.soundList = soundList;
        this.sourcePointer = alGenSources();
    }

    public void requestSound(int soundIndex) {
        requestSoundIndex = soundIndex;
    }

    void setRequestSoundIndex(int index) {
        this.requestSoundIndex = index;
    }
    int getRequestSoundIndex() {
        return requestSoundIndex;
    }


    public void stopSound() {
        alSourceStop(sourcePointer);
    }


    public boolean hasSound(int index) {
        return soundList.size()-1 >= index;
    }

    public void addSound(Sound sound){
        soundList.add(sound);
    }

    public void setPosition(Vec3 vec3){
        alSource3f(sourcePointer, AL_POSITION, vec3.x, vec3.y, vec3.z);
    }

    public void setRollOffFactor(int value){
        alSourcef(sourcePointer, AL_ROLLOFF_FACTOR, value);
    }

    public void setReferenceDistance(int value){
        alSourcef(sourcePointer, AL_REFERENCE_DISTANCE, value);
    }

    public void setMaxDistance(int value){
        alSourcef(sourcePointer, AL_MAX_DISTANCE, value);
    }


    public void backgroundMusic(){
        alSourcei( sourcePointer, AL_SOURCE_RELATIVE, AL_TRUE );
        alSourcei(sourcePointer, AL_LOOPING, AL_TRUE);

        alSourcef( sourcePointer, AL_ROLLOFF_FACTOR, 0.0f );

        alSourcef(sourcePointer, AL_GAIN, 0.5f);
    }

    public void backgroundSound(){
        alSourcei( sourcePointer, AL_SOURCE_RELATIVE, AL_TRUE );

        alSourcef( sourcePointer, AL_ROLLOFF_FACTOR, 0.0f );

        alSourcef(sourcePointer, AL_GAIN, 0.5f);
    }

//    public void addSound(String filename){
//        addSound(new Sound(filename));
//    }
//
//    public void setPosition(Vec2 vec2){
//        alSource3f(sourcePointer, AL_POSITION, vec2.x, vec2.y, 0);
//    }
//
//    public void setPosition(int x, int y){
//        alSource3f(sourcePointer, AL_POSITION, x, y, 0);
//    }
//
//
//
//    public void setPosition(int x, int y, int z){
//        alSource3f(sourcePointer, AL_POSITION, x, y, z);
//    }
//



}
