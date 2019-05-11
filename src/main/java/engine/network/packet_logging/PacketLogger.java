package engine.network.packet_logging;

import engine.network.networkPackets.AllCharacterStateData;
import engine.network.networkPackets.CharacterInputData;
import engine.network.networkPackets.EntityDeadData;
import utils.loggers.Logger;

public class PacketLogger {


    private Logger logger;

    public PacketLogger(String logname) {

        logger = new Logger(logname, true);
    }

    public void logClientInput(int frame, int clientId, CharacterInputData data) {
        logger.println(""+frame+' '+clientId+';'+data.serialize());
    }
    public void logCharactersState(int frame, AllCharacterStateData data) {
        logger.println(""+frame+';'+data.serialize());
    }

    public void logDeadEntity(int frame, EntityDeadData data) {
        logger.println(""+frame+';'+data.serialize());
    }
}
