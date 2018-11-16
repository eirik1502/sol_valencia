package engine.audio;

import static org.lwjgl.openal.AL10.*;

/**
 * Created by haraldvinje on 26-Jul-17.
 */
public class Sound {



    private String fileName;

    private int bufferPointer;




    public Sound(String fileName){
        this.fileName = fileName;
        this.bufferPointer = AudioUtils.initSoundBuffer(fileName);
    }

    public int getBufferPointer(){
        return bufferPointer;
    }




    public String toSting(){
        return fileName;
    }

}
