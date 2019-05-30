import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import torch.utils.data as tutils
import numpy as np
import matplotlib.pyplot as plt

from .data_loader import load_datasets

PATH_STORED_MODELS = "nn_models/"
MODEL_STATE_FILE_KINGSKURK_KINGSKURK = "model_state_kingskurk_kingskurk.pt"

net = None
num_inputs = None
num_outputs = None

class FCProbNet(nn.Module):
    def __init__(self, layers_size):
        super(FCProbNet, self).__init__()
        self.fc_layers = nn.ModuleList(
            [nn.Linear(I, O) for I, O in zip(layers_size[:-1], layers_size[1:])]
        )

    def forward(self, x):
        for layer in self.fc_layers[:-1]:
            x = F.relu(layer(x))
        # sigmoid for the last layer
        x = torch.sigmoid(self.fc_layers[-1](x))
        return x

def load_model(filename):
    global net, num_inputs, num_outputs

    state_dict = torch.load(PATH_STORED_MODELS + filename)

    state_dict_weight_keys = list(state_dict.keys())
    layers_size = [state_dict[weights_entry].shape[1] for weights_entry in state_dict_weight_keys[::2]]
    layers_size.append(state_dict[state_dict_weight_keys[-1]].shape[0])

    num_inputs = layers_size[0]
    num_outputs = layers_size[-1]

    net = FCProbNet(layers_size)
    net.load_state_dict(state_dict)

def save_model(filename):
    torch.save(net.state_dict(), PATH_STORED_MODELS + filename)

def train_model():
    global net, num_inputs, num_outputs

    print("GPU available:", torch.cuda.is_available())

    train_data, test_data = load_datasets(
        chars=["KingSkurkTwo", 'KingSkurkTwo'],
        merge_interval=30,
        test_games=1,
        include_velocities=False,
        include_ab_aim=True
    )

    # batch_size = int(np.ceil(len(train_data)/5))
    batch_size = len(train_data)
    learn_rate = 0.00003  # 0.001
    epochs = 8000

    train_data_loader = tutils.DataLoader(
        train_data,
        batch_size=batch_size,
        shuffle=False
    )
    test_data_loader = tutils.DataLoader(
        test_data,
        batch_size=len(test_data),
        shuffle=False
    )

    num_inputs = train_data.tensors[0].shape[1]
    num_outputs = train_data.tensors[1].shape[1]

    net = FCProbNet([num_inputs, 64*10, 64*10, num_outputs])

    # init weights
    def init_weights(m):
        if type(m) == nn.Linear:
            torch.nn.init.xavier_uniform_(m.weight)
            m.bias.data.fill_(0.01)

    net.apply(init_weights)

    # transform to gpu
    print("torch:", torch.__version__, "cuda:", torch.version.cuda, "CudNN:", torch.backends.cudnn.enabled, torch.backends.cudnn.version())
    print("current device:", torch.cuda.current_device())
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print("using device:", device, "cuda devices available:", torch.cuda.device_count())

    # net.float()
    net.to(device)

    print(net)

    # criterion = nn.L1Loss(reduction='sum')
    # criterion = nn.MSELoss(reduction='sum')
    loss_func = nn.BCELoss()

    # optimizer = optim.SGD(net.parameters(), lr=learn_rate)
    optimizer = optim.Adam(net.parameters(), lr=learn_rate)


    loss_history = []
    loss_test_history = []

    for epoche in range(epochs):

        running_loss = 0.0
        for i, (X_train, Y_train) in enumerate(train_data_loader):
            X_train, Y_train = X_train.to(device), Y_train.to(device)

            batch_size = len(X_train)
            optimizer.zero_grad()

            out = net(X_train)

            loss = loss_func(out, Y_train)
            loss.backward()
            optimizer.step()

            running_loss += loss.item()# / batch_size

        loss_history.append(running_loss)

        running_test_loss = 0
        for (X_test, Y_test) in test_data_loader:
            X_test, Y_test = X_test.to(device), Y_test.to(device)

            batch_size = len(X_test)
            test_out = net(X_test)
            test_loss = loss_func(test_out, Y_test)

            running_test_loss += test_loss.item()# / batch_size

        loss_test_history.append(running_test_loss)

        if epoche%200 == 0:
            print("epoche", epoche, "train loss:", running_loss, "testloss:", running_test_loss)

    # print(net.fc1.bias.grad)

    test_res = []
    targ = []
    for (X_test, Y_test) in test_data_loader:
        test_out = net(X_test)
        test_loss = loss_func(test_out, Y_test)
        print("test loss:", test_loss.item())

        # check how many buttons are right
        test_res.append(np.array(test_out.detach()))
        targ.append(np.array(Y_test.detach()))

    test_res = np.vstack(test_res)
    targ = np.vstack(targ)

    if num_outputs == 4:
        # add dummy values for other tahn dir buttons
        test_res = np.hstack([test_res, np.zeros((test_res.shape[0], 5))])
        targ = np.hstack([targ, np.zeros((targ.shape[0], 5))])

    test_res_buttons = test_res[:, :-2] > 0.5
    targ_buttons = targ[:, :-2]

    correct_buttons = np.logical_and(test_res_buttons, targ_buttons)

    num_total_buttons = targ_buttons.sum()
    num_total_dir_buttons = targ_buttons[:, :4].sum()
    num_total_ab_buttons = targ_buttons[:, 4:].sum()

    num_correct_buttons = correct_buttons.sum()
    num_correct_dir_buttons = correct_buttons[:, :4].sum()
    num_correct_ab_buttons = correct_buttons[:, 4:].sum()

    np.set_printoptions(formatter={'float': lambda x: "{0:0.2f}".format(x)})

    print("correct dir buttons: (",num_correct_dir_buttons,"/", num_total_dir_buttons, ")",
          num_correct_dir_buttons / num_total_dir_buttons, '%')

    print("correct ab buttons: (",num_correct_ab_buttons,"/", num_total_ab_buttons, ")",
          num_correct_ab_buttons / num_total_ab_buttons if num_total_ab_buttons != 0 else np.inf, '%')

    result_per_button = test_res_buttons.sum(axis=0)
    target_per_button = targ_buttons.sum(axis=0).astype(np.int)
    percent_per_button = result_per_button / (target_per_button + 1e-10)
    percent_per_button[percent_per_button > 100] = np.inf
    print("Correct per button:\ntarget", target_per_button, "\nresult", result_per_button, "\n%%%%%%", percent_per_button)

    # print("res buttons %:", test_res_buttons.sum(axis=0))
    # print("targ buttons %:", targ_buttons.sum(axis=0))

    # print(targ)

    plt.plot(loss_history, label="train loss")
    plt.plot(loss_test_history, label="test loss")
    plt.legend()
    plt.show()

# print(net.fc1.bias.grad)
# list(net.get_parmeters())[0].grad

map_width = 1600
map_height = 900
aim_max_vals = np.array([map_width, map_height])
def clean_to_raw_input(clean_input):
    #scale aim xy
    clean_input[-2:] = clean_input[-2:] * aim_max_vals
    return clean_input

def predict(x):
    global net

    if num_inputs == 4:
        # remove velocities
        x = x[:4]

    x = torch.from_numpy(x.astype(np.float32))
    prediction = net(x)
    prediction = np.array(prediction.detach())

    if num_outputs == 9:
        prediction = clean_to_raw_input(prediction)

    elif num_outputs == 4:
        # predicts only dir keys
        prediction = np.hstack([prediction, np.zeros(5)])

    prediction[:7] = np.round(prediction[:7])
    return prediction
