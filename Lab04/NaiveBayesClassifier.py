import numpy as np
from sklearn.base import BaseEstimator, ClassifierMixin


class NaiveBayesClassifier(BaseEstimator, ClassifierMixin):
    def __init__(self, domain_sizes=None, laplace=False):
        self.class_labels_ = None
        self.PY_ = None  # wektor numpy a prawdopodobieństwo klas apriori
        self.P_ = None  # struktura trojwymiarowa z wszytskimi rozkladami warunkowymi P(2,7,1] = Pr(X_7 = 1 | Y=2)
        self.domain_sizes_ = domain_sizes
        self.laplace_ = laplace

    def fit(self, X, y):
        m, n = X.shape
        self.class_labels_ = np.unique(y)  # 1 2 3
        y_normalized = np.zeros(m, dtype='int')  # 0 1 2
        for yy, label in enumerate(self.class_labels_):
            y_normalized[y == label] = yy

        self.PY_ = np.zeros(self.class_labels_.size)

        for yy, label in enumerate(self.class_labels_):
            self.PY_[yy] = np.sum(y == label) / m

        self.P_ = np.empty((self.class_labels_.size, n), dtype='object')
        for yy, label in enumerate(self.class_labels_):
            for j in range(n):
                self.P_[yy, j] = np.zeros(self.domain_sizes_[j])

        # uczenie
        for i in range(m):
            for j in range(n):
                v = X[i, j]
                self.P_[y_normalized[i], j][v] += 1

        if not self.laplace_:
            for yy, label in enumerate(self.class_labels_):
                y_sum = self.PY_[yy] * m  # liczba przykaldow klasy y
                for j in range(n):
                    self.P_[yy, j] /= y_sum
        else:
            for yy, label in enumerate(self.class_labels_):
                y_sum = self.PY_[yy] * m  # liczba przykaldow klasy y
                for j in range(n):
                    self.P_[yy, j] = (self.P_[yy, j] + 1) / (y_sum + self.domain_sizes_[j])

        # print(self.P_)

    # zwraca dla kazdego x  w X etykiety klas
    def predict(self, X):
        return self.class_labels_[np.argmax(self.predict_proba(X), axis=1)]

    # zwraca dla kazdego x w X wektor prawdopodobienstw P(Y = 1 |x), P(y=2 |x), P(Y=3|x)
    def predict_proba(self, X):
        m, n = X.shape
        scores = np.ones((m, self.class_labels_.size))
        for i in range(m):
            x = X[i]
            for yy in range(self.class_labels_.size):
                for j in range(n):
                    scores[i, yy] += np.log2(self.P_[yy, j][x[j]])
                scores[i, yy] += np.log2(self.PY_[yy])
            # s= scores[i].sum()
            # if s > 0.0:
            #     scores[i] /= s
        return scores
        # print(scores)

#     y*= arg max_y prod_{j=1}^n P(X_j = x_j | Y=y) P(Y-y)

