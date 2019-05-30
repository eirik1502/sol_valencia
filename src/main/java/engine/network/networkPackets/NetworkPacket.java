package engine.network.networkPackets;

public interface NetworkPacket {

    int getPacketId();
    String serialize();

}
