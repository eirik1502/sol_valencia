package game.server;

import game.ClientGameTeams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by eirik on 28.07.2017.
 */
public class ServerGameTeams {

    private int teamCount;
    private ServerClientHandler[][] clients;
    private HashMap<ServerClientHandler, Integer> clientCharacterid = new HashMap<>();


    private ServerGameTeams(int teams, int... charsOnTeam) {
        if (teams != charsOnTeam.length) throw new IllegalArgumentException("Not given char numb per team");

        teamCount = teams;
        clients = new ServerClientHandler[teams][];

        for (int i = 0; i < teams; i++) {
            clients[i] = new ServerClientHandler[charsOnTeam[i]];
        }
    }

    /**
     * Put one client per team.
     * @param client1
     * @param client2
     */
    public ServerGameTeams(ServerClientHandler client1, ServerClientHandler client2) {
        this(2, 1, 1);
        putClientOnTeam(0, 0, client1);
        putClientOnTeam(1, 0, client2);
    }

    /**
     * Put two first clients on team1, two last on team2
     * @param client1team1
     * @param client2team1
     * @param client1team2
     * @param client2team2
     */
    public ServerGameTeams(ServerClientHandler client1team1, ServerClientHandler client2team1,
                           ServerClientHandler client1team2, ServerClientHandler client2team2) {
        this(2, 2, 2);
        //team 1
        putClientOnTeam(0, 0, client1team1);
        putClientOnTeam(0, 1, client2team1);
        //team 2
        putClientOnTeam(1, 0, client1team2);
        putClientOnTeam(1, 1, client2team2);

    }


    public int getTotalClientCount() {
        int count = 0;
        for (int i = 0; i < teamCount; i++) {
            count += clients[i].length;
        }
        return count;
    }

    public int getTeamCount() {
        return clients.length;
    }

    public boolean hasCharacterSelectionRemaining() {
        return clientCharacterid.size() < getTotalClientCount();
    }

    public void putClientOnTeam(int team, int clientNumber, ServerClientHandler client) {
        clients[team][clientNumber] = client;
    }

    public int[] getCharacterIdsOnTeam(int team) {
        int teamSize = clients[team].length;

        int[] characterIds = new int[teamSize];

        //get the character of each client
        for (int j = 0; j < teamSize; j++) {

            characterIds[j] = clientCharacterid.get( clients[team][j] );
        }
        return characterIds;
    }

    public void setClientCharacterId(ServerClientHandler client, int characterId) {
        clientCharacterid.put(client, characterId);
    }

//    public List<Integer> getTeamCharacters(int team) {
//
//    }
    public int getClientTeam(ServerClientHandler client) {
        for (int i = 0; i < teamCount; i++) {
            if (Arrays.asList(clients[i]).contains(client)) {
                return i;
            }
        }
        throw new IllegalStateException("trying to retrieve team of client that is on no team");
    }
    public int getClientIndexOnTeam(int team, ServerClientHandler client) {
        int index = Arrays.asList( clients[team] ).indexOf(client);
        if (index == -1) throw new IllegalStateException("trying to retrieve the client index of a client not on team specified");

        return index;
    }

    public void forEachClient(Consumer<ServerClientHandler> c) {
        for (int i = 0; i < teamCount; i++) {
            Arrays.stream( clients[i] ).forEach(c);
        }
    }

    public ServerClientHandler[] getAllClients() {
        List<ServerClientHandler> clients = new ArrayList<>();

        forEachClient(client -> clients.add(client));

        return clients.toArray(new ServerClientHandler[0]);
    }

    public ClientGameTeams getClientGameTeams(ServerClientHandler client) {
        int[][] characterIds = new int[clients.length][];
        int controlCharacterTeam;
        int controlCharacterIndex;

        //get the character of each client
        for (int i = 0; i < clients.length; i++) {
            characterIds[i] = getCharacterIdsOnTeam(i);
        }

        controlCharacterTeam = getClientTeam(client);
        controlCharacterIndex = getClientIndexOnTeam(controlCharacterTeam, client);

        return new ClientGameTeams(characterIds, controlCharacterTeam, controlCharacterIndex);
    }
}
