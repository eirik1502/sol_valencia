import os
import numpy as np
import torch.utils.data as tutils
import torch

# char_main = "KingSkurkTwo"
# char_other = "*"
# player_main = "eirik"
# player_other = "*"

map_width = 1600
map_height = 900

state_max_vals = np.array([map_width, map_width, map_height, map_height])
aim_max_vals = np.array([map_width, map_height])

data_filepath = "processed_game_data/"
input_file_ext = "solinputs"
state_file_ext = "solstates"

relevant_files = []

# def players_match(player1, player2):
#     return entities_match(player1, player2, player_main, player_other)
#
# def chars_match(char1, char2):
#     return entities_match(char1, char2, char_main, char_other)

def entities_match(inp_ent1, inp_ent2, match_ent1, match_ent2):
    return (inp_ent1.lower() == match_ent1.lower() or match_ent1 == '*')\
            and (inp_ent2.lower() == match_ent2.lower() or match_ent2 == '*')

def load_games(chars=('*', '*'), players=('*', '*')):
    char_main = chars[0]
    char_other = chars[1]

    player_main = players[0]
    player_other = players[1]

    # read and filter files according to players and characters
    with os.scandir(data_filepath) as entries:
        for entry in entries:
            if entry.is_file():
                fname = entry.name
                fbase, fext = fname.split('.')

                # assuming there is a state file for every input file
                if fext == input_file_ext:
                    timestamp, data_type, players, chars, char_priority = fbase.split('_')

                    chars = chars.split('-')
                    players = players.split('-')

                    char_priority_ind = int(char_priority) -1  # 1 indexed

                    main_char = chars[char_priority_ind]
                    other_char = chars[1 - char_priority_ind]
                    main_player = players[char_priority_ind]
                    other_player = players[1-char_priority_ind]

                    # filter the players and chars wanted
                    if entities_match(main_player, other_player, player_main, player_other)\
                            and entities_match(main_char, other_char, char_main, char_other):
                        relevant_files.append(fbase)

    print("Game data files that matched:")
    for f in relevant_files:
        print(f)

    # load the data
    inputs = []
    states = []
    for f in relevant_files:
        inp = np.loadtxt(data_filepath + f + '.' + input_file_ext)
        state = np.loadtxt(data_filepath + f + '.' + state_file_ext)

        inputs.append(inp)
        states.append(state)

    # check that inputs and states have equal length
    if not len(inputs) == len(states):
        print("ERROR! not equal games of states and inputs loaded")
        return None

    for inp, state in zip(inputs, states):
        if not inp.shape[0] == state.shape[0]:
            print("ERROR! different length of states and inputs in some games")

    # offset states by one
    inputs = [x[1:] for x in inputs]
    states = [x[:-1] for x in states]

    return inputs, states

def merge_games_frames(inputs, states, interval=60):

    proc_inputs = []
    proc_states = []
    for inp, state in zip(inputs, states):
        inp_intervals = np.array_split(inp, np.arange(interval, inp.shape[0]-interval, interval, dtype=np.int))
        inp_reduced = []
        for x in inp_intervals:
            x_red = np.empty(x.shape[1])

            # dir_btns_count = x[:, :4].sum(axis=0)
            # dir_btns = dir_btns_count > interval/2
            # dir_btns[np.argmin(dir_btns_count[:2])] = 0  # remove left or right, least pressed
            # dir_btns[2+np.argmin(dir_btns_count[2:4])] = 0  # remove up or down, least pressed
            # x_red[:4] = dir_btns

            x_red[:4] = x[:, :4].sum(axis=0) > interval/2
            # print(x_red[:4])

            x_red[4:7] = x[:, 4:7].max(axis=0)
            # print(x_red[4:7])

            x_red[7:] = np.mean(x[:, 7:], axis=0)
            inp_reduced.append(x_red)

        inp = np.vstack(inp_reduced)

        state_intervals = np.array_split(state, np.arange(interval, state.shape[0]-interval, interval, dtype=np.int))
        state_reduced = []
        for x in state_intervals:
            x = np.mean(x, axis=0)
            state_reduced.append(x)

        state = np.vstack(state_reduced)

        # print("state after", state.shape)
        # print("inp after", inp.shape)

        proc_inputs.append(inp)
        proc_states.append(state)

    return proc_inputs, proc_states

def norm_center_inputs(inputs):
    # clip aimxy
    inputs[:, -2:] = np.clip(inputs[:, -2:], [0, 0], aim_max_vals)

    #scale aim xy
    inputs[:, -2:] = inputs[:, -2:] / aim_max_vals

    return inputs

def norm_center_states(states):
    # clip to map size
    states = np.clip(states, [0, 0, 0, 0], state_max_vals)

    # scale state to the range [-1, 1]
    states = states / (state_max_vals * 0.5) - 1

    return states

def add_velocity(states):
    vels = np.empty(states.shape)
    vels[1:] = states[1:] - states[:-1]
    vels[0] = vels[1]
    return np.hstack([states, vels])

def load_datasets(chars=('*', '*'), players=('*', '*'),
                  include_velocities=False,
                  include_ab_aim=False,
                  merge_interval=60,
                  test_games=2):
    # per game processing
    games_inputs, games_states = load_games(chars, players)
    games_inputs, games_states = merge_games_frames(games_inputs, games_states, interval=merge_interval)

    # merge games
    games_len = [game.shape[0] for game in games_inputs]

    inputs = np.vstack(games_inputs)
    states = np.vstack(games_states)

    # all frames processing
    inputs, states = norm_center_inputs(inputs), norm_center_states(states)

    # TODO: fix velocities so they are calculated before frame merge
    if include_velocities:
        states = add_velocity(states)

    # print(states[10])
    # print(states[11])
    # exit()

    print("max states:", list(states.max(axis=0)))
    print("min states:", list(states.min(axis=0)))
    print("mean states:", list(np.mean(states, axis=0)))

    print("max inputs:", list(inputs.max(axis=0)))
    print("min inputs:", list(inputs.min(axis=0)))
    print("mean inputs:", list(np.mean(inputs, axis=0)))

    if not include_ab_aim:
        # keep only dir buttons
        inputs = inputs[:, :4]

    # split into train and test set
    test_split_at = -sum(games_len[-test_games:])  # split at last game start

    input_train_data = inputs[:test_split_at]
    state_train_data = states[:test_split_at]
    input_test_data = inputs[test_split_at:]
    state_test_data = states[test_split_at:]

    # convert to torch representation
    train_dataset = tutils.TensorDataset(torch.from_numpy(state_train_data).type(torch.float32),
                                         torch.from_numpy(input_train_data).type(torch.float32))
    test_dataset = tutils.TensorDataset(torch.from_numpy(state_test_data).type(torch.float32),
                                        torch.from_numpy(input_test_data).type(torch.float32))

    return train_dataset, test_dataset

# data_loaders(["kingskurktwo", '*'])
