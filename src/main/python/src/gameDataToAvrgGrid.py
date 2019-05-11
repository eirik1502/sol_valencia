import matplotlib.pyplot as plt
import math
import random

file = open("../../../../logs/game_state_logs/game_state.txt_190503_170620.log")

log_header = file.readline()

frames = {}

for line in file:
    # print(line)
    [fpart, lpart] = line.split(";")
    frame = int(fpart)

    [packet_id_str, xs_str, ys_str, rots_str] = lpart.split(" ")

    # print("frame", frame_str, "xs", xs_str, "ys", ys_str, "rots", rots_str)
    packet_id = int(packet_id_str)
    xs = [float(x) for x in xs_str.split(',')[:-1]]
    ys = [float(y) for y in ys_str.split(',')[:-1]]
    rots = [float(rot) for rot in rots_str.split(',')[:-1]]

    frames[frame] = (xs, ys, rots)

file.close()

use_both_players_data = True
xs = []
ys = []

map_width = 1600
map_height = 900
cell_width = 32
cell_height = 32

grid_width = math.ceil(map_width / cell_width)
grid_height = math.ceil(map_height / cell_height)
map_to_grid_rat_x = grid_width / map_width
map_to_grid_rat_y = grid_height / map_height
map_to_cell_x = lambda x: math.floor(x * map_to_grid_rat_x)
map_to_cell_y = lambda y: math.floor(y * map_to_grid_rat_y)

print("grid width", grid_width, "grid_height", grid_height)
print(map_to_cell_x(799), map_to_cell_y(100))

# a 2d grid, with the first dimension according to the characters position
# and the second dimension according to the other characters position
dgrid = [[[[[] for oy in range(grid_height)] for ox in range(grid_width)] for y in range(grid_height)] for x in range(grid_width)]

def insert_in_dgrid(x, y, ox, oy, elem):
    dgrid[map_to_cell_x(x)][map_to_cell_y(y)][map_to_cell_x(ox)][map_to_cell_y(oy)].append(elem)
def get_in_dgrid(x, y, ox, oy):
    return dgrid[map_to_cell_x(x)][map_to_cell_y(y)][map_to_cell_x(ox)][map_to_cell_y(oy)]

def print_dgrid():

    for yi in range(len(dgrid[0])):
        s = ""
        for xi in range(len(dgrid)):
            no_elem = True
            for oxi in range(len(dgrid[xi][yi])):
                for oyi in range(len(dgrid[xi][yi][oxi])):
                    elems = dgrid[xi][yi][oxi][oyi]
                    if len(elems) > 0:
                        no_elem = False
            s += ' - ' if no_elem else ' O '
        print(s)
    print()

x_ind = 0
y_ind = 1
rot_ind = 2
for frame in frames:
    frame_data = frames[frame]
    p1_x = frame_data[x_ind][0]  # x
    p1_y = frame_data[y_ind][0]  # y
    p2_x = frame_data[x_ind][1]
    p2_y = frame_data[y_ind][1]

    insert_in_dgrid(p1_x, p1_y, p2_x, p2_y, "hei")
    if use_both_players_data:
        insert_in_dgrid(p2_x, p2_y, p1_x, p1_y, "kanskje")

print_dgrid()

def get_action(x, y, ox, oy):
    # get the nearet cell with samples
    # if there are no smaples, return no input
    inputs_sets = get_in_dgrid(x, y, ox, oy)

    if len(inputs_sets) == 0:
        return None


    # get up to n random samples
    sample_count = 3
    actual_samples_count = max(len(inputs_sets), sample_count)
    use_inputs = [inputs_sets.pop( random.randint(0, len(inputs_sets)) ) for i in range(actual_samples_count)]

    # combine the n inputs by getting each attribute from one at random
    input = []
    for attr_id in range(len(use_inputs[0])):
        get_from_input_id = random.randint(0, actual_samples_count)
        input.append(use_inputs[get_from_input_id])

    return input