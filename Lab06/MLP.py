import numpy as np
from sklearn.base import BaseEstimator, RegressorMixin


class MLP(BaseEstimator, RegressorMixin):
    def __init__(self, K=1, T=10**3, eta=0.01, random_state=0):
        self.K_ = K
        self.T_ = T
        self.eta_ = eta
        self.random_state_ = random_state

        self.V_ = None  # K - wierszy, n+1 - kolumn
        self.W_ = None  # rozmiar K+1
        self.V_scale_ = 10**(-3)

    def fit(self, X, y):
        np.random.seed(self.random_state_)

        m, n = X.shape
        self.V_ = (np.random.rand(self.K_, n + 1) * 2 - 1) * self.V_scale_
        self.W_ = np.random.rand(1, self.K_ + 1)
        for t in range(self.T_):
            i = np.random.randint(0, m)
        x = (np.c_[1.0, np.array([X[i]])]).T
        s = self.V_.dot(x)  # K sum wazonych
        phi = 1.0 / (1.0 + np.exp(-s))
        one_phi = np.r_[np.array([[1.0]]), phi]
        y_MLP = self.W_.dot(one_phi)[0, 0]
        dV = np.dot((y_MLP - y[i]) * np.array([self.W_[0, 1:]]).T * phi * (1.0 - phi),
                    x.T)  # (y_MLP - y[i]) * w_k * phi_k * (1 - phi_k) * x_j
        dW = ((y_MLP - y[i]) * one_phi).T  # (y_MLP - y[i]) * phi_k
        self.V_ = self.V_ - self.eta_ * dV
        self.W_ = self.W_ - self.eta_ * dW

    # def fit(self, X, y):
    #     np.random.seed(self.random_state_)
    #
    #     m, n = X.shape
    #     self.V_ = (np.random.rand(self.K_, n+1) * 2 - 1) * self.V_scale_
    #     self.W_ = np.random.rand(1, self.K_ + 1)
    #
    #     # liczymy poprawki błedu kwadratowego i korygujemy wagi
    #     for t in range(self.T_):
    #         i = np.random.randint(0, m)
    #         x = np.c_[1.0, np.array([X[i]])].T
    #
    #         # wrktor k sum wazonych dochodzącuch do kolejnych neuronów
    #         s = self.V_.dot(x)
    #         # kolumna aktywacji
    #         phi = 1.0 / (1.0 + np.exp(-s))
    #         one_phi = np.r_[np.array([[1.0]]), phi]
    #
    #         y_MLP = self.W_.dot(one_phi)[0, 0]
    #         dV = np.dot(
    #             (y_MLP - y[i])  # skalar
    #             * np.array([self.W_[0, 1:]]).T  # wagi z pominięciem zerowej
    #             * phi * (1.0 - phi),  # wyjścia neuronów * 1 - wyjścia
    #             x.T)
    #         # (K, 1) x (1, n+1) => (K, n+1)
    #
    #         dW = ((y_MLP - y[i]) * one_phi).T  # (y_MLP - y[i]) * phi_k
    #
    #         # mamy otrzymacie macierz poprawek
    #         self.V_ = self.V_ - self.eta_ * dV
    #         self.W_ = self.W_ - self.eta_ * dW

    def predict(self, X):
        m = X.shape[0]
        y_pred = np.c_[np.ones((m, 1)), X].T  # (n+1, m)

        s = self.V_.dot(X)  # (K, m)
        phi = 1.0 / (1.0 + np.exp(-s))
        one_phi = np.r_[np.ones((1, m)), phi]

        y_MLP = self.W_.dot(one_phi)
        return y_MLP[0]
