package engine.audio;

/**
 * Created by haraldvinje on 27-Jul-17.
 */
public class AudioTest {


    public static void main(String[] args) {
        AudioMaster.init();
        Sound sound = new Sound("audio/si.ogg");
        AudioComp ac = new AudioComp(sound);
        //ac.playSound(0);
        System.out.println(ac.soundList.get(0).toSting());
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
