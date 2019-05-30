import time
import struct
import socket
import matplotlib.pyplot as plt

from game_client.data_byte_buffer import DataByteBuffer
import game_client.data_byte_buffer as DBB

# general packets
ALIVE_PACKET = -1
DISCONNECT_PACKET = -2

GAME_SERVER_EXIT = 42
GAME_CLIENT_EXIT = 43

# pregame packets
QUEUE_CLIENT_REQUEST_QUEUE_1V1 = 30
QUEUE_CLIENT_REQUEST_QUEUE_2V2 = 31
QUEUE_SERVER_PUT_IN_QUEUE = 32
QUEUE_SERVER_GOTO_CHARACTERSELECT = 33
QUEUE_CLIENT_EXIT = 34

CHARSELECT_CLIENT_CHOSE_CHARACTER = 37
CHARSELECT_SERVER_GOTO_GAME = 38

INGAME_CLIENT_READY = 40
INGAME_SERVER_CLIENT_GAME_TEAMS = 41

# ingame packets
SERVER_CHARACTER_STATE_ID = 0
SERVER_ABILITY_STARTED_ID = 1
SERVER_HIT_DETECTED_ID = 2
SERVER_CHARACTER_DEAD_ID = 3
SERVER_PROJECTILE_DEAD_ID = 4
SERVER_GAME_OVER_ID = 5

CLIENT_CHARACTER_INPUT = 6

INT_BYTES = 4
FLOAT_BYTES = 4
BOOL_BYTES = 1

NET_GAME_CHARCTERS = 4


SERVER_PORT = 7779

class Teams:
    def __init__(self):
        self.num_teams = -1
        self.teams_chars_id = []
        self.my_team_ind = -1
        self.my_team_char_ind = -1

class GameState:
    def __init__(self):
        self.frame_number = -1
        self.xs = []
        self.ys = []
        self.rots = []

    def __str__(self):
        return "[Game state   frame=" + str(self.frame_number) + " xs=" + str(self.xs) + " ys=" + str(self.ys) + " rots=" + str(self.rots) + "]"


class PacketHandler:

    def __init__(self, verbose=False):
        self.verbose = verbose
        self.socket = None
        self.running = False

        self._onPacketRecvFuncs = {
            QUEUE_SERVER_PUT_IN_QUEUE: self._onServerPutInQueue,
            QUEUE_SERVER_GOTO_CHARACTERSELECT: self._onServerGotoCharselect,
            CHARSELECT_SERVER_GOTO_GAME: self._onServerGotoGame,
            INGAME_SERVER_CLIENT_GAME_TEAMS: self._onServerClientTeams,
            SERVER_CHARACTER_STATE_ID: self._onGameState,

            GAME_SERVER_EXIT: self._onAnyExit,
            DISCONNECT_PACKET: self._onAnyExit,
            SERVER_GAME_OVER_ID: self._onAnyExit
        }

    def onServerPutInQueue(self):
        pass
    def onServerGotoCharselect(self):
        pass
    def onServerGotoGame(self):
        pass
    def onServerClientTeams(self, teams):
        pass
    def onGameState(self, state):
        pass

    def sendAlivePacket(self):
        db = DataByteBuffer()
        db.writeInt(0)
        db.writeInt(ALIVE_PACKET)
        self.socket.send(db.getBytes())

    def sendGotoQueue(self):
        db = DataByteBuffer()
        db.writeInt(0)
        db.writeInt(QUEUE_CLIENT_REQUEST_QUEUE_1V1)
        self.socket.send(db.getBytes())

    def sendChooseCharacter(self, charId):
        db = DataByteBuffer()
        db.writeInt(INT_BYTES)
        db.writeInt(CHARSELECT_CLIENT_CHOSE_CHARACTER)
        db.writeInt(charId)
        self.socket.send(db.getBytes())

    def sendIngameReady(self):
        db = DataByteBuffer()
        db.writeInt(0)
        db.writeInt(INGAME_CLIENT_READY)
        self.socket.send(db.getBytes())

    def sendCharInput(self, left=False, right=False, up=False, down=False,
                      action1=False, action2=False, action3=False,
                      aimx=0.0, aimy=0.0):
        db = DataByteBuffer()

        # packet header
        db.writeInt(BOOL_BYTES * 7 + FLOAT_BYTES * 2)
        db.writeInt(CLIENT_CHARACTER_INPUT)

        #packet data
        db.writeBool(left)
        db.writeBool(right)
        db.writeBool(up)
        db.writeBool(down)

        db.writeBool(action1)
        db.writeBool(action2)
        db.writeBool(action3)

        db.writeFloat(aimx)
        db.writeFloat(aimy)

        self.socket.send(db.getBytes())

    def _onServerPutInQueue(self, byte_buff):
        self.onServerPutInQueue()

    def _onServerGotoCharselect(self, byte_buff):
        self.onServerGotoCharselect()

    def _onServerGotoGame(self, byte_buff):
        self.onServerGotoGame()



    def _onServerClientTeams(self, byte_stream):
        teams = Teams()
        num_teams = byte_stream.readInt()
        teams.num_teams = num_teams
        for i in range(num_teams):
            team_size = byte_stream.readInt()
            team_char_ids = [byte_stream.readInt()] * team_size
            teams.teams_chars_id.append(team_char_ids)

        teams.my_team_ind = byte_stream.readInt()
        teams.my_team_char_ind = byte_stream.readInt()

        self.onServerClientTeams(teams)


    def _onGameState(self, byte_buff):
        # convert bytes to state
        game_state = GameState()
        game_state.frame_number = byte_buff.readInt()
        for i in range(NET_GAME_CHARCTERS):
            game_state.xs.append(byte_buff.readFloat())
            game_state.ys.append(byte_buff.readFloat())
            game_state.rots.append(byte_buff.readFloat())

        self.onGameState(game_state)

    def _onAnyExit(self, byte_buff):
        self.running = False
        print("Ending session")


    def connect(self, addr="127.0.0.1", initFunc=None):

        # create an INET, STREAMing socket
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # now connect to the web server on port 80 - the normal http port
        self.socket.connect((addr, SERVER_PORT))

        self.running = True

        if initFunc is not None:
            initFunc()

        packet_size_hist = []
        packet_id_hist = []
        packet_time_hist = []

        byte_buff = b''

        next_packet_id = -1
        bytes_to_read = -1

        start_time = time.time()
        last_packet_time = start_time
        last_update_time = start_time
        last_recv_time = start_time

        while self.running:
            time_now = time.time()

            # receive pending socket data
            if time_now - last_recv_time >= 0.016:
                last_recv_time = time_now
                recv_bytes = self.socket.recv(1024)
                byte_buff += recv_bytes

            # send alive packets at some interval
            if time_now - last_update_time >= 1.0:
                # print("Byte buff size:", len(byte_buff))
                last_update_time = time_now
                self.sendAlivePacket()

            # switch between waiting for a new packet
            # and reading data for a pending packet
            if bytes_to_read == -1:
                if len(byte_buff) >= 8:
                    # read size and id of packet
                    data_Size = DBB.bytes_to_int(byte_buff[:4])
                    packet_id = DBB.bytes_to_int(byte_buff[4:8])

                    # update receive state
                    bytes_to_read = data_Size
                    next_packet_id = packet_id
                    byte_buff = byte_buff[8:]

                    # update timeout
                    last_packet_time = time_now

                    # log packets
                    packet_size_hist.append(data_Size)
                    packet_id_hist.append(packet_id)
                    packet_time_hist.append(last_packet_time - start_time)

            else:
                # check if the whole pending packte has arrived,
                # if not, wait for it
                if len(byte_buff) >= bytes_to_read:
                    packet_data = byte_buff[:bytes_to_read]
                    byte_buff = byte_buff[bytes_to_read:]

                    packet_data_buff = DataByteBuffer(packet_data)

                    # switch between packets
                    if next_packet_id == ALIVE_PACKET:
                        # print("alive packet received")
                        pass

                    # elif next_packet_id == DISCONNECT_PACKET:
                    #     print("Server disconnected us")

                    elif next_packet_id in self._onPacketRecvFuncs:
                        self._onPacketRecvFuncs[next_packet_id](packet_data_buff)

                    else:
                        pass
                        # print("Got some other packet", next_packet_id)

                    next_packet_id = -1
                    bytes_to_read = -1

            if time.time() - last_packet_time > 2:
                break

        if self.verbose:
            plt.plot(packet_time_hist, packet_id_hist, 'r.', label="packet id")
            plt.plot(packet_time_hist, packet_size_hist, 'b.', label="packet size")
            plt.legend()
            plt.show()

        self.socket.close()
