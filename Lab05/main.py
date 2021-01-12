import os
import shutil
import numpy as np
import matplotlib.pyplot as plt

from SimplePerceptron import SimplePerceptron

np.set_printoptions(precision=None, suppress=None)
plt.style.use('ggplot')
plt.rcParams.update({'mathtext.default': 'regular'})


def calculate_z(x0, c0, x1, c1, sig):
    return np.exp(
        -1 * (
                ((x0 - c0) ** 2 + (x1 - c1) ** 2)
                /
                (2 * sig ** 2)
        )
    )


def main():
    np.random.seed(0)
    shutil.rmtree('plots')
    os.mkdir('plots')

    params = np.genfromtxt(r'params.txt', delimiter=',')

    print(f'Number of plots to create: {len(params)}')

    for it, param in enumerate(params):

        sigma, center_count, k_max = (param[0], int(param[1]), int(param[2]))

        m = 1000
        X1 = np.random.rand(m, 1) * 2 * np.pi  # [ 0, 2pi]
        X2 = np.random.rand(m, 1) * 2 - 1.0  # [-1,  1]

# 1 Zbiór danych dla zadania
        X = np.c_[X1, X2]
        y = np.array([-1 if np.abs(np.sin(x[0])) > np.abs(x[1]) else 1 for x in X])

# 2 Normalizacja zmiennych wejściowych
        X = np.c_[X[:, 0] / np.pi - 1, X[:, 1]]  # [ 0, 2pi] z do [-1, 1]

# 3 Podniesienie wymiarowości przestrzeni wejściowej
        Centers = np.array([(np.random.uniform(-1, 1), np.random.uniform(-1, 1)) for _ in range(center_count)])
        z = np.empty((m, center_count))

        for i in range(m):
            for j in range(center_count):
                z[i][j] = calculate_z(X[i][0], Centers[j][0], X[i][1], Centers[j][1], sigma)

# 4 Warunek stopu (k_max) - wewnątrz perceptronu
        clf = SimplePerceptron(eta=0.8).fit(z, y, k_max)
        # print(clf.w_, clf.k_)
        score = clf.score(z, y)

# X Wykresy
        xs, ys = np.meshgrid(np.linspace(-1, 1, 100), np.linspace(-1, 1, 100))
        zs = np.zeros(xs.shape)

        for i in range(xs.shape[0]):
            for j in range(xs.shape[1]):
                Z = np.zeros(center_count)
                for k in range(center_count):
                    Z[k] = calculate_z(xs[i, j], Centers[k][0], ys[i, j], Centers[k][1], sigma)

                zs[i, j] = clf.predict(np.array([Z]))

        # predykcja
        col_bar = plt.contourf(xs, ys, zs, levels=1, colors=['yellow', 'red'])
        plt.contour(col_bar, levels=[0, 1], colors=('k',), linestyles=('-',), linewidths=(3,))
        # punkty
        colors = ['b' if yi == -1 else 'lightgreen' for yi in y]
        plt.scatter(X[:, 0], X[:, 1], s=8, facecolors='none', edgecolors=colors)
        # centra
        for center in Centers:
            plt.scatter(center[0], center[1], c='black', s=16)
        plt.xticks(np.arange(-1, 1.5, 0.5))
        plt.yticks(np.arange(-1, 1.2, 0.2))
        plt.title(f'plot_{sigma:.3f}_{center_count}_{k_max} \n TRAIN ACC: {score}')
        plt.xlabel('$x_1$')
        plt.ylabel('$x_2$')
        plt.savefig(f'plots/{score:.3f}_plot_{sigma}_{center_count}_{k_max}.png', dpi=300)
        plt.clf()

        print(f'{it+1}: Created plot with score {score:.3f}')


if __name__ == '__main__':
    main()
