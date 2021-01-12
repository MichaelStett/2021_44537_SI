import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
from MLP import MLP
import time

if __name__ == '__main__':
    np.random.seed(0)
    m = 1000
    X = np.random.rand(m, 2) * np.pi
    y = np.cos(X[:, 0] * X[:, 1]) * np.cos(2 * X[:, 0])
    X = (X / np.pi) * 2 - 1  # normalization to [-1.0, 1.0]

    mlp = MLP(K=16, T=1 * 10 ** 6, eta=0.01,
              random_state=1)  # random_state=0 -> unlucky starting point, random_state=1 -> lucky starting point
    print("FITTING MLP...")
    t1 = time.time()
    mlp.fit(X, y)
    t2 = time.time()
    print("FITTING DONE IN " + str(t2 - t1) + " s.")
    y_pred = mlp.predict(X)
    err = 0.5 * (y_pred - y).dot(y_pred - y) / m
    print("MEAN 1/2 ERR^2: " + str(err))

    steps = 20
    #     X1, X2 = np.meshgrid(np.linspace(0.0, np.pi, steps), np.linspace(0.0, np.pi, steps))
    #     X12 = np.array([X1.ravel(), X2.ravel()]).T
    #     y_ref = np.cos(X12[:, 0] * X12[:, 1]) * np.cos(2 * X12[:, 0])

    X1, X2 = np.meshgrid(np.linspace(-1.0, 1.0, steps), np.linspace(-1.0, 1.0, steps))
    X12 = np.array([X1.ravel(), X2.ravel()]).T
    X1_temp = (X1 + 1.0) / 2.0 * np.pi  # denormalization
    X2_temp = (X2 + 1.0) / 2.0 * np.pi  # denormalization
    X12_temp = np.array([X1_temp.ravel(), X2_temp.ravel()]).T
    y_ref = np.cos(X12_temp[:, 0] * X12_temp[:, 1]) * np.cos(2 * X12_temp[:, 0])

    Y_ref = np.reshape(y_ref, (steps, steps))
    y_pred = mlp.predict(X12)
    Y_pred = np.reshape(y_pred, (steps, steps))

    fig = plt.figure()
    ax = fig.add_subplot(1, 2, 1, projection='3d')
    ax.plot_surface(X1, X2, Y_ref, cmap=cm.get_cmap("Spectral"))
    ax = fig.add_subplot(1, 2, 2, projection='3d')
    ax.plot_surface(X1, X2, Y_pred, cmap=cm.get_cmap("Spectral"))
    # ax.scatter(X[:, 0], X[:, 1], y)
    plt.show()

    # print("TRAIN SCORE: " + str(mlp.score(X, y)))

#     fig = plt.figure()
#     ax = fig.add_subplot(1, 1, 1, projection='3d')
#     ax.scatter(X[:, 0], X[:, 1], y)
#     plt.show()

