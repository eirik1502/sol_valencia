import numpy as np
import os
from load_game_cleaned import load_1v1_game


processed_data_filepath = "processed_game_data/"
game_logs_filepath = "../../../logs/game_state_logs/"

# process_log_name = "190513-135857_server-packets_eirik-eirik_MagneT-MagneT.log"


def process_client_game(client_inputs, game_states):
    # remove the rotation entries
    game_states = game_states[:, :-2]
    return client_inputs, game_states

def save_client_game(logname, client_inputs, game_states):
    np.savetxt(processed_data_filepath + logname + '.solinputs', client_inputs)
    np.savetxt(processed_data_filepath + logname + '.solstates', game_states)

def process_and_save_game_log(log_filename):
    log_name = log_filename[:log_filename.rfind('.')]
    log_path = game_logs_filepath + log_filename
    client1_states, client2_states, client1_inputs, client2_inputs = load_1v1_game(log_path)

    client1_inputs, client1_states = process_client_game(client1_inputs, client1_states)
    client2_inputs, client2_states = process_client_game(client2_inputs, client2_states)

    # print(client1_inputs.shape)
    # print(client2_inputs.shape)
    # print(client1_states.shape)
    # print(client2_states.shape)

    save_client_game(log_name + "_1", client1_inputs, client1_states)
    save_client_game(log_name + "_2", client2_inputs, client2_states)

def process_all_logs():
    game_logs = []
    with os.scandir(game_logs_filepath) as entries:
        for entry in entries:
            if entry.is_file():
                game_logs.append(entry.name)

    for game_log in game_logs:
        process_and_save_game_log(game_log)

# process_test_log()
process_all_logs()