import engine.network.*;
import engine.network.networkPackets.*;
import org.junit.Assert;
import org.junit.Test;
import utils.maths.M;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by eirik on 07.07.2017.
 */
public class NetworkProtocolTest {


    @Test
    public void testCharacterInput() {

        //create data
        CharacterInputData origDataOut = new CharacterInputData();

        origDataOut.setActions(true, false, false);
        origDataOut.setMovement(false, true, true, false);
        origDataOut.setAim(largeRandom(), largeRandom());

        //transfer data from output to input
        NetworkDataOutput dataOut = NetworkUtils.characterInputToPacket(origDataOut);
        NetworkDataInput dataIn = new NetworkDataInput(dataOut.getBytes());

        //read data
        CharacterInputData origDataIn = NetworkUtils.packetToCharacterInput(dataIn);


        //compare input and output
        Assert.assertEquals(origDataOut.toString(), origDataIn.toString());

    }

    @Test
    public void testCharacterState() {

        //create data
        AllCharacterStateData origDataOut = new AllCharacterStateData();
        origDataOut.setFrameNumber(10);
        for (int i = 0; i < NetworkUtils.CHARACTER_COUNT; i++) {
            origDataOut.setX(i, largeRandom());
            origDataOut.setY(i, largeRandom());
            origDataOut.setRotation(i, largeRandom());
        }

        //transfer data from output to input
        NetworkDataOutput dataOut = NetworkUtils.gameStateToPacket(origDataOut);
        NetworkDataInput dataIn = new NetworkDataInput(dataOut.getBytes());


        //read data
        AllCharacterStateData origDataIn = NetworkUtils.packetToGameState(dataIn);


        //compare input and output
        Assert.assertEquals(origDataOut.toString(), origDataIn.toString());

    }

    @Test
    public void testAbilityStarted() {

        //create data
        AbilityStartedData origDataOut = new AbilityStartedData();
        origDataOut.setAbilityId(largeRandomInt());
        origDataOut.setEntityId(largeRandomInt());

        //transfer data from output to input
        NetworkDataOutput dataOut = NetworkUtils.abilityStartedDataToPacket(origDataOut);
        NetworkDataInput dataIn = new NetworkDataInput(dataOut.getBytes());


        //read data
        AbilityStartedData origDataIn = NetworkUtils.packetToAbilityStarted(dataIn);


        //compare input and output
        Assert.assertEquals(origDataOut.toString(), origDataIn.toString());

    }

    @Test
    public void testHitDetected() {


        //create data
        HitDetectedData origDataOut = new HitDetectedData();
        origDataOut.setEntityDamageable(largeRandomInt());
        origDataOut.setDamageTaken(largeRandom());

        //transfer data from output to input
        NetworkDataOutput dataOut = NetworkUtils.hitDetectedToPacket(origDataOut);
        NetworkDataInput dataIn = new NetworkDataInput(dataOut.getBytes());


        //read data
        HitDetectedData origDataIn = NetworkUtils.packetToHitDetected(dataIn);


        //compare input and output
        Assert.assertEquals(origDataOut.toString(), origDataIn.toString());


    }

    @Test
    public void testProjectileDead() {


        //create data
        ProjectileDeadData origDataOut = new ProjectileDeadData();
        origDataOut.setEntityOwnerId(largeRandomInt());
        origDataOut.setProjectileAbilityId(largeRandomInt());

        //transfer data from output to input
        NetworkDataOutput dataOut = NetworkUtils.projectileDeadToPacket(origDataOut);
        NetworkDataInput dataIn = new NetworkDataInput(dataOut.getBytes());


        //read data
        ProjectileDeadData origDataIn = NetworkUtils.packetToProjectileDead(dataIn);


        //compare input and output
        Assert.assertEquals(origDataOut.toString(), origDataIn.toString());

    }


    private float largeRandom() {
        return M.random() * Integer.MAX_VALUE;
    }
    private int largeRandomInt() {
        return (int)( M.random() * Integer.MAX_VALUE );
    }
}
