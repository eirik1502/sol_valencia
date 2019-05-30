package engine.network.packet_logging;

import engine.network.networkPackets.*;
import game.CharacterUtils;
import game.GameUtils;
import utils.loggers.Logger;

import java.util.stream.Collectors;

public class PacketLogger {

    private Logger logger;

    public PacketLogger(String logname) {
        String playerNames = String.join("-", GameUtils.PLAYER_NAMES);
        String playerCharacters = GameUtils.PLAYER_CHARACTERS_ID.stream()
                .map(charId -> CharacterUtils.CHARACTER_NAMES[charId])
                .collect(Collectors.joining("-"));

        String lognameWithPlayerNames = logname + '_' + playerNames +'_'+playerCharacters;
        logger = new Logger(lognameWithPlayerNames, true);

        logger.println(playerNames +' '+ playerCharacters);
    }

    public void close() {
        logger.close();
    }

    public void log(String s) {
        logger.println(s);
    }

    public void logPacket(int frame, NetworkPacket packet) {
        logger.println(""+frame +' '+ packet.getPacketId() +' '+ packet.serialize());
    }
}
