import NetworkUtils as NU
import numpy as np


def load_1v1_game(package_log_path, verbose=True):
    game_states, clients_inputs = load_game_cleaned(package_log_path, verbose)

    if len(clients_inputs) != 2:
        print("ERROR! tried to load a 1v1 game with more than 2 players")
        return None

    # remove frame entry
    game_states = [state[1:] for state in game_states]

    # convert inputs to ndarrays
    client1_states = np.array(game_states)
    client1_states = client1_states.reshape((client1_states.shape[0], -1))

    # invert client entries for client 2, so it is equal to client 2 beeing client 1
    client2_states = client1_states.copy()
    client2_states = client2_states[:, [1,0, 3,2, 5,4]]

    client1_inputs = np.array(clients_inputs[0])[:, 1:]  # remove frame
    client2_inputs = np.array(clients_inputs[1])[:, 1:]  # remove frame

    return client1_states, client2_states, client1_inputs, client2_inputs

def get_empty_input_frame_data(frame):
    return [frame, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]

# returns a single game as a game states and client inputs
def load_game_cleaned(package_log_path, verbose=True):

    log_timestamp_line = None
    players_chars_line = None
    chars_entity_ids_line = None
    data_lines = None

    with open(package_log_path) as log_file:
        log_timestamp_line = log_file.readline().strip()
        players_chars_line = log_file.readline().strip()
        chars_entity_ids_line = log_file.readline().strip()
        data_lines = log_file.readlines()

    players, chars = [field.split('-') for field in players_chars_line.split(" ")]

    num_players = len(chars)  # chars is dymaically generated, players are human input

    if verbose:
        print("\nProcessing", package_log_path[package_log_path.rfind("/")+1:])
        print("timestamp:", log_timestamp_line)
        print("players(", num_players, "):", players)
        print("chars(", num_players, "):", chars)
        print("data lines:", len(data_lines))


    # filter out player input and game state
    players_inputs = [[] for _ in range(num_players)]
    game_states = []
    num_other_lines = 0
    pot_end_frames = []

    def process_input_data(frame, line_data_str):
        client_id = int(line_data_str[0])
        input_data = [float(x) for x in line_data_str[1:]]
        players_inputs[client_id].append([frame] + input_data)

    def process_state_data(frame, line_data_str):
        xs_str, ys_str, rots_str = line_data_str
        xs = [ float(d) for d in xs_str.split(',')[:num_players] ]
        ys = [ float(d) for d in ys_str.split(',')[:num_players] ]
        rots = [ float(d) for d in rots_str.split(',')[:num_players] ]
        game_states.append([frame, xs, ys, rots])

    def process_game_over_data(frame, line_data_str):
        pot_end_frames.append(frame)

    line_process_funcs = {
        NU.SERVER_CHARACTER_STATE_ID : process_state_data,
        NU.CLIENT_CHARACTER_INPUT : process_input_data,
        NU.SERVER_GAME_OVER_ID : process_game_over_data
    }
    for data_line in data_lines:
        data = data_line.split(' ')
        frame = int(data[0])
        packet_id = int(data[1])
        line_data_str = data[2:]

        if packet_id in line_process_funcs:
            line_process_funcs[packet_id](frame, line_data_str)

        else:
            num_other_lines += 1

    game_end_frame = pot_end_frames[0]
    game_start_frame = max(input_line[0][0] for input_line in players_inputs)
    total_game_frames = game_end_frame-game_start_frame+1

    first_recorded_frame = game_states[0][0]
    last_recorded_frame = game_states[-1][0]

    if verbose:
        print("\nFiltered package log")
        print("client inputs:", [len(x) for x in players_inputs])
        print("game states:", len(game_states))
        print("other lines:", num_other_lines)
        print("first recorded frame:", first_recorded_frame)
        print("last recorded frame:", last_recorded_frame)

    # fill in missing frames
    players_inputs_complete = [[None for _ in range(last_recorded_frame+1)] for _ in range(num_players)]

    # fill players inputs
    for player_id, player_inputs in enumerate(players_inputs):
        for player_input in player_inputs:
            frame = player_input[0]
            players_inputs_complete[player_id][frame] = player_input

    # players_inputs_complete_nofill = [[frame.copy() if frame is not None else None for frame in player] for player in players_inputs_complete]

    # fill holes in player inputs
    for player_id, player_inputs_complete in enumerate(players_inputs_complete):

        prev_frame_data = get_empty_input_frame_data(0)

        for frame, frame_data in enumerate(player_inputs_complete):
            if frame_data is None:
                fill_frame_data = prev_frame_data.copy()
                fill_frame_data[0] = frame
                players_inputs_complete[player_id][frame] = fill_frame_data
            else:
                prev_frame_data = frame_data

    # for x, y in zip(players_inputs_complete[0], players_inputs_complete_nofill[0]):
    #     print(x, y)

    # cut non-game frames
    for player_id in range(len(players_inputs_complete)):
        players_inputs_complete[player_id] = players_inputs_complete[player_id][game_start_frame:game_end_frame+1]
    game_states = game_states[game_start_frame:game_end_frame+1]

    if verbose:
        print("\nFilled input frames and cut all frames to game-valid frames")
        print("client inputs:", [len(x) for x in players_inputs_complete])
        print("game states:", len(game_states))
        print("game start frame:", game_start_frame)
        print("game end frame:", game_end_frame)
        print("total game frames:", total_game_frames)


    return game_states, players_inputs_complete