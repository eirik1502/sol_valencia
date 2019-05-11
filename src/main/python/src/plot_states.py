import matplotlib.pyplot as plt

file = open("logs/game_state_logs/game_state.txt_190503_170620.log")

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

for player_id in range(2):
    xs = []
    ys = []
    for frame in frames:
        frame_data = frames[frame]
        xs.append(frame_data[0][player_id])
        ys.append(frame_data[1][player_id])

    print(xs, ys)

    plt.plot(xs, ys)

plt.show()

