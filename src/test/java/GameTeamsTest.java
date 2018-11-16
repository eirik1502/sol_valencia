import engine.network.NetworkDataInput;
import engine.network.NetworkDataOutput;
import engine.network.NetworkPregameUtils;
import game.ClientGameTeams;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Created by eirik on 28.07.2017.
 */
public class GameTeamsTest {


    private PipedInputStream pipeIn;
    private PipedOutputStream pipeOut;


    @Before
    public void setup() {
        try {
            pipeOut = new PipedOutputStream();
            pipeIn = new PipedInputStream(pipeOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void ClientGameTransferTest() {

        int[][] characterIds = {
                {0, 1},
                {0, 2}
        };
        int clientTeam = 0;
        int clientCharId = 1;
        ClientGameTeams clientTeams = new ClientGameTeams(characterIds, clientTeam, clientCharId);

        NetworkDataOutput dataOut = NetworkPregameUtils.clientGameTeamsToPacket( clientTeams );

        //transmit bytes from data out to data in
        NetworkDataInput dataIn = new NetworkDataInput(dataOut.getBytes());

        ClientGameTeams clientTeamsRec = NetworkPregameUtils.packetToClientGameTeams(dataIn);


        //check if teams transmitted is equal to teams received
        boolean success = clientTeams.toString().equals( clientTeamsRec.toString() );

        if (!success) {
            //print
            System.out.println("ClientTeams sendt = "+clientTeams);
            System.out.println("ClientTeams received = "+clientTeamsRec);
        }

        Assert.assertTrue(success);

    }
}
