from game_client.sol_packet_handler import PacketHandler


class SolClient(PacketHandler):
    def __init__(self, verbose=False):
        super().__init__(verbose)

        self.charId = -1

    def connect(self, addr="127.0.0.1", charId=0):
        self.charId = charId
        super().connect(addr, initFunc=super().sendGotoQueue)

    def onServerPutInQueue(self):
        print("Server put us in queue")

    def onServerGotoCharselect(self):
        print("We are matched, going to character select")
        super().sendChooseCharacter(self.charId)
        super().sendIngameReady()

    def onServerGotoGame(self):
        print("Going to game")
