package engine.network;

import game.ClientGameTeams;

/**
 * Created by eirik on 28.07.2017.
 */
public class NetworkPregameUtils {

    public static NetworkDataOutput clientGameTeamsToPacket(ClientGameTeams clientTeams) {
        NetworkDataOutput data = new NetworkDataOutput();

        int[][] characterIds = clientTeams.getCharacterIds();

        //team count
        int teamCount = characterIds.length;
        data.writeInt(teamCount);

        //team n count, team n character ids
        for (int i = 0; i < teamCount; i++) {
            //team size
            int teamSize = characterIds[i].length;
            data.writeInt( teamSize );

            //team characters
            for (int j = 0; j < teamSize; j++) {
                data.writeInt( characterIds[i][j] );
            }
        }

        //control character team
        data.writeInt(clientTeams.getControlCharacterTeam());

        //control character index
        data.writeInt(clientTeams.getControlCharacterIndex());

        return data;
    }

    public static ClientGameTeams packetToClientGameTeams(NetworkDataInput dataIn) {
        ClientGameTeams clientTeams = new ClientGameTeams();

        int[][] characterIds;

        //team count
        int teamCount = dataIn.readInt();
        characterIds = new int[ teamCount ][];

        //team n count, team n character ids
        for (int i = 0; i < teamCount; i++) {
            //team size
            int teamSize = dataIn.readInt();
            characterIds[i] = new int[teamSize];

            //team characters
            for (int j = 0; j < teamSize; j++) {
                int characterIndex = dataIn.readInt();
                characterIds[i][j] = characterIndex;
            }
        }

        //set character ids
        clientTeams.setCharacterIds(characterIds);

        //control character team
        clientTeams.setControlCharacterTeam( dataIn.readInt() );

        //control character index
        clientTeams.setControlCharacterIndex( dataIn.readInt() );

        return clientTeams;
    }
}
