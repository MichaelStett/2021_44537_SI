from sklearn.base import BaseEstimator, ClassifierMixin
import numpy as np


class SimplePerceptron(BaseEstimator, ClassifierMixin):
    def __init__(self, eta=1.0):
        self.class_labels_ = None
        self.w_ = None        # wagi
        self.k_ = None        # liczba kroków algorytmu
        self.eta_ = eta       # współczynnik uczenia

    def fit(self, X, y, k_max=5000):
        m, n = X.shape
        self.class_labels_ = np.unique(y)
        y_normalized = np.ones(m).astype('int')
        y_normalized[y == self.class_labels_[0]] = -1
        self.w_ = np.zeros(n + 1)
        self.k_ = 0
        X = np.c_[np.ones((m, 1)), X]
        while True:
            E = []    # lista punktów źle zklasyfikowanych
            E_y = []  # lista etykiet
            for i in range(m):
                x = X[i]
                f = -1 if self.w_.dot(x.T) <= 0 else 1
                if f != y_normalized[i]:
                    E.append(x)
                    E_y.append(y_normalized[i])
                    break

            if len(E) == 0:
                break

            i = 0  # int(np.random.rand() * len(E))
            x = E[i]
            y = E_y[i]

            self.w_ += self.eta_ * y * x
            self.k_ += 1

            if self.k_ == k_max:
                break

        return self

    def predict(self, X):
        return self.class_labels_[(self.decision_function(X) > 0) * 1]

    # zamiast predict proba
    def decision_function(self, X):
        return self.w_.dot(
            np.c_[np.ones((X.shape[0], 1)), X].T
        )
