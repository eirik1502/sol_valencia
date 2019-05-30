import game_client.sol_gotogame_client as gotogame_client
import game_client.sol_packet_handler as solsock
import numpy as np


frame = 0


def onGameState(game_state):
    global frame

    aimx = np.cos(frame/60 *np.pi*2) * 300 + 800
    aimy = np.sin(frame/60 *np.pi*2) * 300 + 450
    solsock.sendCharInput(aimx=aimx, aimy=aimy)

    frame += 1
    print(game_state)


gotogame_client.connect(onGameState=onGameState)