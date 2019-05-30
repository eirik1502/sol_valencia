import numpy as np
import sys
import time
from threading import Thread
import cv2

from game_client.sol_gotogame_client import SolClient
import learning.ann_learner as ann
import learning.data_loader as dl


class AnnGameClient(SolClient):

    def __init__(self):
        super().__init__(verbose=False)

        self.last_frame_time = time.time()
        self.last_frame_data = None
        self.players_ready = True  # False

        self.frame = 0
        self.teamId = -1

    def start(self, charId=0, trained_model=False, graphical=False):
        self.graphical = graphical

        if not trained_model:
            ann.train_model()

        if graphical:
            self.initDraw()

        while True:
            print("Enter to start, s to save model")
            line = sys.stdin.readline().strip()

            if line in ['s', 'save']:
                self.saveModel()

            else:
                super().connect(charId=charId)

    def initDraw(self):
        self.imgW = 1600
        self.imgH = 900
        cv2.namedWindow('image')
        # cv2.setMouseCallback('image',draw_circle)

    def draw(self, gamestate):
        img = np.zeros((self.imgH, self.imgW), np.uint8)
        for x, y in zip(gamestate.xs[:2], gamestate.ys[:2]):
            cv2.circle(img,(int(x), int(y)), 32, 255, -1)
        # cv2.circle(img,(100, 100), 32, 255, -1)
        img = cv2.resize(img, (int(self.imgW/4), int(self.imgH/4)))
        cv2.imshow('image', img)
        cv2.waitKey(1)


    def endDraw(self):
        cv2.destroyAllWindows()

    def onServerClientTeams(self, teams):
        self.teamId = teams.my_team_ind

    def onGameState(self, state):
        if self.graphical:
            self.draw(state)

        # convert state
        pred_state = np.array(state.xs[:2] + state.ys[:2])

        # swap char ind if on the second team
        if self.teamId == 1:
            pred_state = pred_state[[1, 0,  3, 2]]

        pred_state = dl.norm_center_states(pred_state)

        # store the frame as the last frame for the very first frame
        if self.frame == 0:
            self.last_frame_data = pred_state.copy()

        not_moving = False
        if np.all(pred_state == self.last_frame_data):
            not_moving = True

        # calculate velocities
        vels = pred_state - self.last_frame_data
        self.last_frame_data = pred_state.copy()

        pred_state = np.hstack([pred_state, vels])

        inp_pred = None
        if self.players_ready:
            inp_pred = ann.predict(pred_state)

            # # limit to calculate every other frame
            # if frame % 2 == 0:
            #     inp_pred = ann.predict(pred_state)
            # else:
            #     inp_pred = None

        else:
            inp_pred = np.zeros(9)

            if vels.any():
                self.players_ready = True

        # move if standing still
        # if not_moving:
        #     inp_pred = np.zeros(9)
        #     inp_pred[2] = 1.


        print_interval = 60
        if self.frame % print_interval == 0:
            curr_time = time.time()
            avr_frame_time = (curr_time - self.last_frame_time) / print_interval
            self.last_frame_time = curr_time

            print("state", pred_state)
            print("predict", inp_pred)
            print("players ready", self.players_ready)
            print("Avr frame time:", avr_frame_time)

        if inp_pred is not None:
            super().sendCharInput(
                bool(inp_pred[0]), bool(inp_pred[1]), bool(inp_pred[2]), bool(inp_pred[3]),
                bool(inp_pred[4]), bool(inp_pred[5]), bool(inp_pred[6]),
                float(inp_pred[7]), float(inp_pred[8]))

        self.frame += 1

    def saveModel(self):
        ann.save_model(ann.MODEL_STATE_FILE_KINGSKURK_KINGSKURK)
        print("Model saved!")

print("l - load model, t - train model")
while True:
    line = sys.stdin.readline().strip()
    if line == 'l':
        ann.load_model(ann.MODEL_STATE_FILE_KINGSKURK_KINGSKURK)
    elif line == 't':
        ann.train_model()
    else:
        continue
    break

AnnGameClient().start(charId=1, trained_model=True, graphical=False)

# def startClient(graphical=False):
#     AnnGameClient().start(charId=3, trained_model=True, graphical=graphical)
#
# client1Thread = Thread(target=startClient, args=(True, ))
# client2Thread = Thread(target=startClient)
#
# client1Thread.start()
# client2Thread.start()
#
# client1Thread.join()
# client2Thread.join()
